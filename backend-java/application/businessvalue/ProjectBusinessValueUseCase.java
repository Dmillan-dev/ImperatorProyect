package imperator.application.businessvalue;

import imperator.application.exceptions.BusinessRuleViolationException;
import imperator.application.exceptions.DecisionNotFoundException;
import imperator.application.exceptions.EvidenceNotFoundException;
import imperator.application.exceptions.RecommendationNotFoundException;
import imperator.application.ledger.LedgerChain;
import imperator.domain.decision.Decision;
import imperator.domain.decision.DrcAoa001RecommendationPolicy;
import imperator.domain.decision.Recommendation;
import imperator.domain.evidence.Evidence;
import imperator.domain.ledger.LedgerEntry;
import imperator.domain.ledger.LedgerEntryType;
import imperator.domain.shared.Currency;
import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Money;
import imperator.domain.shared.ROIAmount;
import imperator.ports.in.ProjectBusinessValueInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Builds the authoritative Business Value view without persisting or recalculating it. */
public final class ProjectBusinessValueUseCase implements ProjectBusinessValueInputPort {
    private final DecisionRepository decisionRepository;
    private final RecommendationRepository recommendationRepository;
    private final EvidenceRepository evidenceRepository;
    private final LedgerRepository ledgerRepository;
    private final TransactionRunner transactionRunner;

    public ProjectBusinessValueUseCase(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        this.decisionRepository = Objects.requireNonNull(decisionRepository, "Decision repository is required");
        this.recommendationRepository = Objects.requireNonNull(recommendationRepository, "Recommendation repository is required");
        this.evidenceRepository = Objects.requireNonNull(evidenceRepository, "Evidence repository is required");
        this.ledgerRepository = Objects.requireNonNull(ledgerRepository, "Ledger repository is required");
        this.transactionRunner = Objects.requireNonNull(transactionRunner, "Transaction runner is required");
    }

    @Override
    public BusinessValueProjection projectBusinessValue(DecisionId decisionId, Optional<String> explanation) {
        Objects.requireNonNull(decisionId, "Decision id is required");
        Optional<String> advisoryExplanation = explanation == null ? Optional.empty() : explanation;
        return transactionRunner.execute(() -> projectInTransaction(decisionId, advisoryExplanation));
    }

    private BusinessValueProjection projectInTransaction(
            DecisionId decisionId,
            Optional<String> explanation
    ) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new DecisionNotFoundException(decisionId));
        if (!DecisionStatus.APPROVED.equals(decision.status())) {
            throw incomplete(decisionId, "Decision must be APPROVED");
        }

        var recommendationId = decision.recommendationId()
                .orElseThrow(() -> incomplete(decisionId, "Decision must reference a Recommendation"));
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RecommendationNotFoundException(recommendationId));
        if (!recommendation.belongsTo(decisionId)) {
            throw incomplete(decisionId, "Recommendation ownership is inconsistent");
        }

        List<LedgerEntry> entries = LedgerChain.from(
                decisionId,
                ledgerRepository.findByDecisionId(decisionId)
        ).entries();
        LedgerEntry approval = exactlyOne(entries, LedgerEntryType.APPROVED, decisionId);
        LedgerEntry implementation = exactlyOne(entries, LedgerEntryType.IMPLEMENTATION_MARKED, decisionId);
        LedgerEntry validation = exactlyOne(entries, LedgerEntryType.RESULT_VALIDATED, decisionId);
        requireGovernanceOrder(entries, approval, implementation, validation, decisionId);
        requireRecommendationSnapshot(recommendation, approval, validation, decisionId);

        Set<EvidenceId> allEvidenceIds = new LinkedHashSet<>(recommendation.evidenceIds());
        entries.forEach(entry -> allEvidenceIds.addAll(entry.evidenceSnapshotIds()));
        List<Evidence> recommendationEvidence = loadEvidence(recommendation.evidenceIds());
        loadEvidence(allEvidenceIds).forEach(item -> {
            if (!decision.caseId().equals(item.correlationKey())) {
                throw incomplete(decisionId, "Evidence correlation key is inconsistent");
            }
        });

        List<String> assumptionIds = recommendationEvidence.stream()
                .map(item -> item.metadata().get("assumption_id"))
                .filter(Objects::nonNull)
                .sorted()
                .toList();
        String policyVersion = uniquePolicyVersion(recommendationEvidence, decisionId);
        ROIAmount realized = validation.realizedSaving()
                .orElseThrow(() -> incomplete(decisionId, "Result validation has no realized saving"));
        ROIAmount estimated = validation.estimatedSaving()
                .orElseThrow(() -> incomplete(decisionId, "Result validation has no estimated saving snapshot"));

        return new BusinessValueProjection(
                decision.caseId(),
                decision.caseId(),
                decision.id(),
                decision.title(),
                decision.businessNeed(),
                decision.status(),
                decision.createdAt(),
                recommendation.id(),
                recommendation.type(),
                recommendation.suggestedAction(),
                recommendation.reason(),
                explanation,
                recommendation.createdAt(),
                estimated,
                realized,
                decimalMetadata(validation, "variance", decisionId),
                eurMetadata(validation, "annualized_baseline_cost", decisionId),
                eurMetadata(validation, "annualized_post_action_cost", decisionId),
                eurMetadata(validation, "actual_transition_cost", decisionId),
                recommendation.confidence(),
                recommendation.risk(),
                policyVersion,
                allEvidenceIds.stream().sorted(idComparator()).toList(),
                assumptionIds,
                approval.id(),
                implementation.id(),
                validation.id(),
                entries.stream().map(this::ledgerFact).toList()
        );
    }

    private void requireGovernanceOrder(
            List<LedgerEntry> entries,
            LedgerEntry approval,
            LedgerEntry implementation,
            LedgerEntry validation,
            DecisionId decisionId
    ) {
        int approvalIndex = entries.indexOf(approval);
        int implementationIndex = entries.indexOf(implementation);
        int validationIndex = entries.indexOf(validation);
        if (implementationIndex != approvalIndex + 1
                || validationIndex != implementationIndex + 1
                || validationIndex != entries.size() - 1) {
            throw incomplete(decisionId, "Governance history is not an approved implementation-validation sequence");
        }
    }

    private void requireRecommendationSnapshot(
            Recommendation recommendation,
            LedgerEntry approval,
            LedgerEntry validation,
            DecisionId decisionId
    ) {
        boolean idsMatch = approval.recommendationId().filter(recommendation.id()::equals).isPresent()
                && validation.recommendationId().filter(recommendation.id()::equals).isPresent();
        if (!idsMatch
                || approval.estimatedSaving().filter(recommendation.estimatedSavings()::equals).isEmpty()
                || validation.estimatedSaving().filter(recommendation.estimatedSavings()::equals).isEmpty()
                || validation.confidenceSnapshot().filter(recommendation.confidence()::equals).isEmpty()
                || validation.riskSnapshot().filter(recommendation.risk()::equals).isEmpty()) {
            throw incomplete(decisionId, "Ledger snapshots do not match the authoritative Recommendation");
        }
    }

    private LedgerEntry exactlyOne(
            List<LedgerEntry> entries,
            LedgerEntryType type,
            DecisionId decisionId
    ) {
        List<LedgerEntry> matches = entries.stream().filter(entry -> type.equals(entry.entryType())).toList();
        if (matches.size() != 1) {
            throw incomplete(decisionId, "Ledger must contain exactly one " + type.value() + " fact");
        }
        return matches.getFirst();
    }

    private List<Evidence> loadEvidence(Set<EvidenceId> ids) {
        List<Evidence> evidence = new ArrayList<>(ids.size());
        for (EvidenceId id : ids) {
            evidence.add(evidenceRepository.findById(id).orElseThrow(() -> new EvidenceNotFoundException(id)));
        }
        return List.copyOf(evidence);
    }

    private String uniquePolicyVersion(List<Evidence> evidence, DecisionId decisionId) {
        Set<String> versions = new LinkedHashSet<>();
        evidence.stream()
                .map(item -> item.metadata().get("policy_version"))
                .filter(Objects::nonNull)
                .forEach(versions::add);
        if (!versions.equals(Set.of(DrcAoa001RecommendationPolicy.POLICY_VERSION))) {
            throw incomplete(decisionId, "Recommendation policy provenance is incomplete or ambiguous");
        }
        return versions.iterator().next();
    }

    private BusinessValueProjection.LedgerFact ledgerFact(LedgerEntry entry) {
        return new BusinessValueProjection.LedgerFact(
                entry.id(),
                entry.entryType(),
                entry.actorId(),
                entry.actorRole(),
                entry.occurredAt(),
                entry.previousEntryId(),
                entry.evidenceSnapshotIds().stream().sorted(idComparator()).toList()
        );
    }

    private ROIAmount eurMetadata(LedgerEntry entry, String key, DecisionId decisionId) {
        return new ROIAmount(new Money(decimalMetadata(entry, key, decisionId), Currency.EUR));
    }

    private BigDecimal decimalMetadata(LedgerEntry entry, String key, DecisionId decisionId) {
        String value = entry.metadata().get(key);
        try {
            return new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY);
        } catch (NumberFormatException | ArithmeticException | NullPointerException exception) {
            throw new BusinessRuleViolationException(
                    "BUSINESS_VALUE_METADATA_INVALID",
                    "Business Value metadata " + key + " is invalid for Decision " + decisionId.value(),
                    exception
            );
        }
    }

    private Comparator<EvidenceId> idComparator() {
        return Comparator.comparing(id -> id.value().toString());
    }

    private BusinessRuleViolationException incomplete(DecisionId decisionId, String reason) {
        return new BusinessRuleViolationException(
                "BUSINESS_VALUE_NOT_READY",
                "Business Value is not ready for Decision " + decisionId.value() + ": " + reason
        );
    }
}
