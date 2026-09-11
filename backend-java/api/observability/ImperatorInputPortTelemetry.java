package imperator.api.observability;

import imperator.application.appendledgerentry.AppendLedgerEntryResult;
import imperator.application.composecase.ComposeDrcAoa001Result;
import imperator.application.synchronizeevidence.SynchronizeEvidenceResult;
import imperator.domain.ledger.LedgerEntryType;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.ComposeDrcAoa001InputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ProjectBusinessValueInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.in.SynchronizeEvidenceInputPort;

import java.util.Locale;
import java.util.Objects;

public final class ImperatorInputPortTelemetry {
    private ImperatorInputPortTelemetry() {
    }

    public static ImportEvidenceInputPort evidenceImport(
            ImportEvidenceInputPort delegate,
            ImperatorTelemetry telemetry
    ) {
        Objects.requireNonNull(delegate, "Evidence import delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return command -> {
            long started = System.nanoTime();
            try {
                var result = delegate.importEvidence(command);
                telemetry.evidenceImport(true, System.nanoTime() - started, null);
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.evidenceImport(false, System.nanoTime() - started, failure);
                throw failure;
            }
        };
    }

    public static SynchronizeEvidenceInputPort connectorSync(
            SynchronizeEvidenceInputPort delegate,
            ImperatorTelemetry telemetry,
            String source
    ) {
        Objects.requireNonNull(delegate, "Connector synchronization delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return command -> {
            long started = System.nanoTime();
            try {
                SynchronizeEvidenceResult result = delegate.synchronize(command);
                telemetry.connectorSync(
                        source,
                        result.status().name().toLowerCase(Locale.ROOT),
                        System.nanoTime() - started,
                        null
                );
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.connectorSync(
                        source, "error", System.nanoTime() - started, failure
                );
                throw failure;
            }
        };
    }

    public static ComposeDrcAoa001InputPort decisionComposition(
            ComposeDrcAoa001InputPort delegate,
            ImperatorTelemetry telemetry
    ) {
        Objects.requireNonNull(delegate, "Decision composition delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return command -> {
            long started = System.nanoTime();
            try {
                ComposeDrcAoa001Result result = delegate.compose(command);
                String outcome = result.replayed()
                        ? "replayed"
                        : result.resumed() ? "resumed" : "created";
                telemetry.decisionComposition(
                        outcome, System.nanoTime() - started, null
                );
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.decisionComposition(
                        telemetry.failureOutcome(failure),
                        System.nanoTime() - started,
                        failure
                );
                throw failure;
            }
        };
    }

    public static ReviewDecisionInputPort reviewDecision(
            ReviewDecisionInputPort delegate,
            ImperatorTelemetry telemetry
    ) {
        Objects.requireNonNull(delegate, "Review decision delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return command -> {
            String commandName = command == null || command.action() == null
                    ? "approve"
                    : command.action().value();
            long started = System.nanoTime();
            try {
                var result = delegate.reviewDecision(command);
                telemetry.ledgerCommand(
                        commandName,
                        result.replayed() ? "replayed" : "success",
                        System.nanoTime() - started,
                        null
                );
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.ledgerCommand(
                        commandName,
                        telemetry.failureOutcome(failure),
                        System.nanoTime() - started,
                        failure
                );
                throw failure;
            }
        };
    }

    public static AppendLedgerEntryInputPort appendLedgerEntry(
            AppendLedgerEntryInputPort delegate,
            ImperatorTelemetry telemetry
    ) {
        Objects.requireNonNull(delegate, "Append Ledger delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return command -> {
            String commandName = ledgerCommand(command == null ? null : command.entryType());
            long started = System.nanoTime();
            try {
                AppendLedgerEntryResult result = delegate.appendLedgerEntry(command);
                telemetry.ledgerCommand(
                        commandName,
                        result.replayed() ? "replayed" : "success",
                        System.nanoTime() - started,
                        null
                );
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.ledgerCommand(
                        commandName,
                        telemetry.failureOutcome(failure),
                        System.nanoTime() - started,
                        failure
                );
                throw failure;
            }
        };
    }

    public static ProjectBusinessValueInputPort businessValue(
            ProjectBusinessValueInputPort delegate,
            ImperatorTelemetry telemetry
    ) {
        Objects.requireNonNull(delegate, "Business Value delegate is required");
        Objects.requireNonNull(telemetry, "Telemetry is required");
        return (decisionId, explanation) -> {
            long started = System.nanoTime();
            try {
                var result = delegate.projectBusinessValue(decisionId, explanation);
                telemetry.businessValue("ready", System.nanoTime() - started, null);
                return result;
            } catch (RuntimeException | Error failure) {
                telemetry.businessValue(
                        businessValueOutcome(failure, telemetry),
                        System.nanoTime() - started,
                        failure
                );
                throw failure;
            }
        };
    }

    private static String ledgerCommand(LedgerEntryType entryType) {
        if (LedgerEntryType.RESULT_VALIDATED.equals(entryType)) {
            return "validate_result";
        }
        return "mark_implemented";
    }

    private static String businessValueOutcome(
            Throwable failure,
            ImperatorTelemetry telemetry
    ) {
        return "not_ready".equals(telemetry.failureOutcome(failure))
                ? "not_ready"
                : "error";
    }
}
