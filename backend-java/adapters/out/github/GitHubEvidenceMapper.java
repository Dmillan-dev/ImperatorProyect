package imperator.adapters.out.github;

import imperator.domain.shared.EvidenceId;
import imperator.domain.shared.Severity;
import imperator.domain.shared.Timestamp;
import imperator.ports.out.EvidenceCandidate;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

final class GitHubEvidenceMapper {
    private static final UUID EVIDENCE_NAMESPACE = UUID.fromString(
            "f83b8d46-0f43-4ff5-9a70-92067ce4bd47"
    );
    private static final String SOURCE = "GitHub";
    private static final String ENTITY = "ai-onboarding-assistant";
    private static final String CASE = "DRC-AOA-001";
    private static final String EVIDENCE_TYPE = "code_deployment";

    private final GitHubConnectorSettings settings;

    GitHubEvidenceMapper(GitHubConnectorSettings settings) {
        this.settings = settings;
    }

    EvidenceCandidate implementation(GitHubPullRequest pullRequest) {
        String reference = "E-GH-001";
        String canonicalName = reference + "|" + normalizedRepository()
                + "|" + pullRequest.nodeId() + "|" + pullRequest.mergeCommitSha();
        return candidate(
                canonicalName,
                reference,
                pullRequest.mergedAt(),
                "code",
                "github:" + repository() + ":pull:" + pullRequest.number(),
                "code_change_merged",
                safeActor(pullRequest.mergedBy()),
                "Pull request #" + pullRequest.number() + " correlated by IMP-214 merged as "
                        + pullRequest.mergeCommitSha() + " into " + pullRequest.baseReference(),
                "Establishes the implementation chain for DRC-AOA-001",
                "CONFIDENTIAL",
                Map.of(
                        "evidence_ref", reference,
                        "freshness", "fresh",
                        "pull_number", Long.toString(pullRequest.number()),
                        "pull_node_id", pullRequest.nodeId(),
                        "base_branch", pullRequest.baseReference(),
                        "merge_sha", pullRequest.mergeCommitSha(),
                        "repository", repository()
                )
        );
    }

    EvidenceCandidate review(
            GitHubPullRequest pullRequest,
            GitHubReview review,
            int approvalCount
    ) {
        String reference = "E-GH-002";
        String canonicalName = reference + "|" + normalizedRepository()
                + "|" + pullRequest.nodeId() + "|" + review.id() + "|" + review.commitId();
        return candidate(
                canonicalName,
                reference,
                review.submittedAt(),
                "code",
                "github:" + repository() + ":review:" + review.id(),
                "code_review_observed",
                review.reviewer(),
                "Pull request #" + pullRequest.number()
                        + " had an independent approved review before merge",
                "Establishes review Evidence for the implementation chain",
                "CONFIDENTIAL",
                Map.of(
                        "evidence_ref", reference,
                        "freshness", "fresh",
                        "pull_number", Long.toString(pullRequest.number()),
                        "review_id", Long.toString(review.id()),
                        "approval_count", Integer.toString(approvalCount),
                        "reviewed_commit_id", review.commitId(),
                        "merge_sha", pullRequest.mergeCommitSha(),
                        "repository", repository()
                )
        );
    }

    EvidenceCandidate deployment(
            GitHubPullRequest pullRequest,
            GitHubDeployment deployment,
            GitHubDeploymentStatus status
    ) {
        String reference = "E-GH-003";
        String canonicalName = reference + "|" + normalizedRepository()
                + "|" + deployment.id() + "|" + status.id() + "|success";
        return candidate(
                canonicalName,
                reference,
                status.createdAt(),
                "deployment",
                "github:" + repository() + ":deployment-status:" + status.id(),
                "deployment_reference_observed",
                safeActor(status.creator()),
                "Merge " + pullRequest.mergeCommitSha()
                        + " reached successful production deployment",
                "Establishes the shipped date and runtime lineage",
                "INTERNAL",
                Map.of(
                        "evidence_ref", reference,
                        "freshness", "fresh",
                        "deployment_id", Long.toString(deployment.id()),
                        "status_id", Long.toString(status.id()),
                        "environment", deployment.environment(),
                        "merge_sha", pullRequest.mergeCommitSha(),
                        "repository", repository()
                )
        );
    }

    private EvidenceCandidate candidate(
            String canonicalName,
            String evidenceReference,
            java.time.Instant timestamp,
            String sourceType,
            String sourceObjectReference,
            String eventType,
            String actor,
            String observedFact,
            String businessMeaning,
            String sensitivity,
            Map<String, String> metadata
    ) {
        return new EvidenceCandidate(
                new EvidenceId(UuidV5.from(EVIDENCE_NAMESPACE, canonicalName)),
                evidenceReference,
                new Timestamp(timestamp),
                SOURCE,
                sourceType,
                sourceObjectReference,
                ENTITY,
                eventType,
                Severity.INFO,
                actor,
                EVIDENCE_TYPE,
                observedFact,
                businessMeaning,
                CASE,
                CASE,
                sensitivity,
                "HIGH",
                "ACCEPTED",
                "not_stored",
                metadata
        );
    }

    private String repository() {
        return settings.organization() + "/" + settings.repository();
    }

    private String normalizedRepository() {
        return repository().toLowerCase(Locale.ROOT);
    }

    private static String safeActor(String actor) {
        return actor == null || actor.isBlank() ? "unknown" : actor.trim();
    }
}
