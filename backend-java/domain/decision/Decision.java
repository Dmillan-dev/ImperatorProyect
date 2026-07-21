package imperator.domain.decision;

import imperator.domain.shared.DecisionId;
import imperator.domain.shared.DecisionStatus;
import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.RecommendationId;
import imperator.domain.shared.Timestamp;
import imperator.domain.shared.UserId;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Decision {
    private final DecisionId id;
    private final String caseId;
    private final String title;
    private final String businessNeed;
    private final EvidenceId originatingEvidenceId;
    private final UserId ownerId;
    private final UserId requiredApproverId;
    private final Timestamp createdAt;
    private final Set<EvidenceId> evidenceIds = new LinkedHashSet<>();

    private DecisionStatus status;
    private RecommendationId recommendationId;
    private UserId reviewedBy;
    private Timestamp reviewedAt;
    private String reviewReason;
    private Timestamp updatedAt;

    private Decision(
            DecisionId id,
            String caseId,
            String title,
            String businessNeed,
            EvidenceId originatingEvidenceId,
            UserId ownerId,
            UserId requiredApproverId,
            Timestamp createdAt
    ) {
        this.id = Objects.requireNonNull(id, "Decision id is required");
        this.caseId = requireText(caseId, "Decision case id");
        this.title = requireText(title, "Decision title");
        this.businessNeed = requireText(businessNeed, "Decision business need");
        this.originatingEvidenceId = Objects.requireNonNull(originatingEvidenceId, "Originating evidence is required");
        this.ownerId = Objects.requireNonNull(ownerId, "Decision owner is required");
        this.requiredApproverId = Objects.requireNonNull(requiredApproverId, "Decision approver is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Decision creation timestamp is required");
        this.updatedAt = createdAt;
        this.status = DecisionStatus.CREATED;
        this.evidenceIds.add(originatingEvidenceId);
    }

    public static Decision create(
            DecisionId id,
            String caseId,
            String title,
            String businessNeed,
            EvidenceId originatingEvidenceId,
            UserId ownerId,
            UserId requiredApproverId,
            Timestamp createdAt
    ) {
        return new Decision(id, caseId, title, businessNeed, originatingEvidenceId, ownerId, requiredApproverId, createdAt);
    }

    public void addEvidence(EvidenceId evidenceId, Timestamp changedAt) {
        ensureEditableBeforeReview();
        ensureNotBeforeCreation(changedAt);
        evidenceIds.add(Objects.requireNonNull(evidenceId, "Evidence id is required"));
        updatedAt = changedAt;
    }

    public void attachRecommendation(RecommendationId recommendationId, Timestamp changedAt) {
        ensureEditableBeforeReview();
        ensureNotBeforeCreation(changedAt);
        this.recommendationId = Objects.requireNonNull(recommendationId, "Recommendation id is required");
        updatedAt = changedAt;
    }

    public void markUnderReview(Timestamp changedAt) {
        ensureEditableBeforeReview();
        ensureNotBeforeCreation(changedAt);
        if (recommendationId == null) {
            throw new IllegalStateException("Decision cannot enter review without a recommendation");
        }
        status = DecisionStatus.UNDER_REVIEW;
        reviewedBy = null;
        reviewedAt = null;
        reviewReason = null;
        updatedAt = changedAt;
    }

    public void approve(UserId reviewerId, Timestamp reviewedAt, String approvalNote) {
        closeReviewAs(DecisionStatus.APPROVED, reviewerId, reviewedAt, approvalNote);
    }

    public void reject(UserId reviewerId, Timestamp reviewedAt, String rejectionReason) {
        closeReviewAs(DecisionStatus.REJECTED, reviewerId, reviewedAt, rejectionReason);
    }

    public void defer(UserId reviewerId, Timestamp reviewedAt, String deferralReason) {
        if (DecisionStatus.APPROVED.equals(status) || DecisionStatus.REJECTED.equals(status)) {
            throw new IllegalStateException("Approved or rejected decisions cannot be deferred");
        }
        recordReviewOutcome(DecisionStatus.DEFERRED, reviewerId, reviewedAt, deferralReason);
    }

    public boolean isPendingReview() { return DecisionStatus.CREATED.equals(status) || DecisionStatus.UNDER_REVIEW.equals(status); }

    public boolean wasApproved() { return DecisionStatus.APPROVED.equals(status); }

    public boolean wasRejected() { return DecisionStatus.REJECTED.equals(status); }

    public boolean isDeferred() { return DecisionStatus.DEFERRED.equals(status); }

    public boolean hasRecommendation() { return recommendationId != null; }

    public Set<EvidenceId> evidenceIds() { return Set.copyOf(evidenceIds); }

    public Optional<RecommendationId> recommendationId() { return Optional.ofNullable(recommendationId); }

    public Optional<UserId> reviewedBy() { return Optional.ofNullable(reviewedBy); }

    public Optional<Timestamp> reviewedAt() { return Optional.ofNullable(reviewedAt); }

    public Optional<String> reviewReason() { return Optional.ofNullable(reviewReason); }

    public DecisionId id() { return id; }

    public String caseId() { return caseId; }

    public String title() { return title; }

    public String businessNeed() { return businessNeed; }

    public EvidenceId originatingEvidenceId() { return originatingEvidenceId; }

    public UserId ownerId() { return ownerId; }

    public UserId requiredApproverId() { return requiredApproverId; }

    public DecisionStatus status() { return status; }

    public Timestamp createdAt() { return createdAt; }

    public Timestamp updatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object candidate) {
        return candidate instanceof Decision decision && id.equals(decision.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    private void closeReviewAs(DecisionStatus targetStatus, UserId reviewerId, Timestamp reviewedAt, String reason) {
        if (!DecisionStatus.UNDER_REVIEW.equals(status)) {
            throw new IllegalStateException("Decision must be under review before it can be reviewed");
        }
        recordReviewOutcome(targetStatus, reviewerId, reviewedAt, reason);
    }

    private void recordReviewOutcome(DecisionStatus targetStatus, UserId reviewerId, Timestamp reviewedAt, String reason) {
        ensureNotBeforeCreation(reviewedAt);
        status = targetStatus;
        this.reviewedBy = Objects.requireNonNull(reviewerId, "Decision reviewer is required");
        this.reviewedAt = reviewedAt;
        this.reviewReason = requireText(reason, "Decision review reason");
        this.updatedAt = reviewedAt;
    }

    private void ensureEditableBeforeReview() {
        if (!DecisionStatus.CREATED.equals(status) && !DecisionStatus.DEFERRED.equals(status)) {
            throw new IllegalStateException("Decision can only change before review or after deferral");
        }
    }

    private void ensureNotBeforeCreation(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "Decision timestamp is required");
        if (timestamp.value().isBefore(createdAt.value())) {
            throw new IllegalArgumentException("Decision timestamp cannot be before creation");
        }
    }

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " is required");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }
}
