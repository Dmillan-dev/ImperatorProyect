package imperator.bootstrap;

import imperator.adapters.out.postgresql.PostgresConnectionProvider;
import imperator.adapters.out.postgresql.PostgresDataSource;
import imperator.adapters.out.postgresql.PostgresDecisionRepository;
import imperator.adapters.out.postgresql.PostgresEvidenceRepository;
import imperator.adapters.out.postgresql.PostgresLedgerRepository;
import imperator.adapters.out.postgresql.PostgresMvpReadModelQueryAdapter;
import imperator.adapters.out.postgresql.PostgresRecommendationRepository;
import imperator.adapters.out.postgresql.PostgresTransactionRunner;
import imperator.api.observability.ImperatorInputPortTelemetry;
import imperator.api.observability.ImperatorTelemetry;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.composecase.ComposeDrcAoa001UseCase;
import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.businessvalue.ProjectBusinessValueUseCase;
import imperator.application.query.GetDecisionEvidenceUseCase;
import imperator.application.query.GetDecisionLedgerUseCase;
import imperator.application.query.GetDecisionRoiUseCase;
import imperator.application.query.GetDecisionTimelineUseCase;
import imperator.application.query.GetDecisionUseCase;
import imperator.application.query.GetRecommendationUseCase;
import imperator.application.query.ListDecisionsUseCase;
import imperator.application.query.ListLedgerEntriesUseCase;
import imperator.application.reviewdecision.ReviewDecisionUseCase;
import imperator.application.synchronizeevidence.SynchronizeEvidenceUseCase;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.ComposeDrcAoa001InputPort;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.in.GetDecisionInputPort;
import imperator.ports.in.GetDecisionLedgerInputPort;
import imperator.ports.in.GetDecisionRoiInputPort;
import imperator.ports.in.GetDecisionTimelineInputPort;
import imperator.ports.in.GetRecommendationInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ListDecisionsInputPort;
import imperator.ports.in.ListLedgerEntriesInputPort;
import imperator.ports.in.ProjectBusinessValueInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.in.SynchronizeEvidenceInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.EvidenceSourcePort;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.MvpReadModelQueryPort;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.ObjectProvider;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.function.Supplier;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "imperator.postgresql",
        name = "enabled",
        havingValue = "true"
)
@EnableConfigurationProperties(PostgresRuntimeProperties.class)
public final class PostgresRuntimeConfiguration {

    @Bean
    DataSource postgresDataSource(PostgresRuntimeProperties properties) {
        return new PostgresDataSource(
                properties.url(),
                properties.username(),
                properties.password()
        );
    }

    @Bean
    PostgresConnectionProvider postgresConnectionProvider(DataSource dataSource) {
        return new PostgresConnectionProvider(dataSource);
    }

    @Bean
    EvidenceRepository evidenceRepository(PostgresConnectionProvider connectionProvider) {
        return new PostgresEvidenceRepository(connectionProvider);
    }

    @Bean
    DecisionRepository decisionRepository(PostgresConnectionProvider connectionProvider) {
        return new PostgresDecisionRepository(connectionProvider);
    }

    @Bean
    RecommendationRepository recommendationRepository(
            PostgresConnectionProvider connectionProvider
    ) {
        return new PostgresRecommendationRepository(connectionProvider);
    }

    @Bean
    LedgerRepository ledgerRepository(PostgresConnectionProvider connectionProvider) {
        return new PostgresLedgerRepository(connectionProvider);
    }

    @Bean
    MvpReadModelQueryPort mvpReadModelQueryPort(
            PostgresConnectionProvider connectionProvider,
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            LedgerRepository ledgerRepository
    ) {
        return new PostgresMvpReadModelQueryAdapter(
                connectionProvider,
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                ledgerRepository
        );
    }

    @Bean
    TransactionRunner transactionRunner(PostgresConnectionProvider connectionProvider) {
        return new PostgresTransactionRunner(connectionProvider);
    }

    @Bean
    @ConditionalOnMissingBean(ExplanationProvider.class)
    ExplanationProvider unavailableExplanationProvider() {
        return ignored -> Optional.empty();
    }

    @Bean
    ImportEvidenceInputPort importEvidenceInputPort(
            EvidenceRepository evidenceRepository,
            TransactionRunner transactionRunner,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ImportEvidenceInputPort delegate = new ImportEvidenceUseCase(
                evidenceRepository, transactionRunner
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.evidenceImport(delegate, telemetry);
    }

    @Bean("githubSynchronizeEvidenceInputPort")
    SynchronizeEvidenceInputPort githubSynchronizeEvidenceInputPort(
            @Qualifier("githubEvidenceSourcePort") EvidenceSourcePort evidenceSource,
            ImportEvidenceInputPort evidenceImporter,
            EvidenceRepository evidenceRepository,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        SynchronizeEvidenceInputPort delegate = new SynchronizeEvidenceUseCase(
                evidenceSource,
                evidenceImporter,
                evidenceRepository
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.connectorSync(delegate, telemetry, "github");
    }

    @Bean("awsSynchronizeEvidenceInputPort")
    SynchronizeEvidenceInputPort awsSynchronizeEvidenceInputPort(
            @Qualifier("awsEvidenceSourcePort") EvidenceSourcePort evidenceSource,
            ImportEvidenceInputPort evidenceImporter,
            EvidenceRepository evidenceRepository,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        SynchronizeEvidenceInputPort delegate = new SynchronizeEvidenceUseCase(
                evidenceSource,
                evidenceImporter,
                evidenceRepository
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.connectorSync(delegate, telemetry, "aws");
    }

    @Bean
    CreateDecisionInputPort createDecisionInputPort(
            EvidenceRepository evidenceRepository,
            DecisionRepository decisionRepository,
            TransactionRunner transactionRunner
    ) {
        return new CreateDecisionUseCase(
                evidenceRepository,
                decisionRepository,
                transactionRunner
        );
    }

    @Bean
    GenerateRecommendationInputPort generateRecommendationInputPort(
            DecisionRepository decisionRepository,
            EvidenceRepository evidenceRepository,
            RecommendationRepository recommendationRepository,
            TransactionRunner transactionRunner,
            ExplanationProvider explanationProvider
    ) {
        return new GenerateRecommendationUseCase(
                decisionRepository,
                evidenceRepository,
                recommendationRepository,
                transactionRunner,
                explanationProvider
        );
    }

    @Bean
    ComposeDrcAoa001InputPort composeDrcAoa001InputPort(
            CreateDecisionInputPort decisionCreator,
            GenerateRecommendationInputPort recommendationGenerator,
            DecisionRepository decisionRepository,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ComposeDrcAoa001InputPort delegate = new ComposeDrcAoa001UseCase(
                decisionCreator,
                recommendationGenerator,
                decisionRepository
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.decisionComposition(delegate, telemetry);
    }

    @Bean
    ReviewDecisionInputPort reviewDecisionInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ReviewDecisionInputPort delegate = new ReviewDecisionUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.reviewDecision(delegate, telemetry);
    }

    @Bean
    AppendLedgerEntryInputPort appendLedgerEntryInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        AppendLedgerEntryInputPort delegate = new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.appendLedgerEntry(delegate, telemetry);
    }

    @Bean
    ListDecisionsInputPort listDecisionsInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ListDecisionsInputPort delegate = new ListDecisionsUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "list_decisions", () -> delegate.listDecisions(query)
        );
    }

    @Bean
    GetDecisionInputPort getDecisionInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetDecisionInputPort delegate = new GetDecisionUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_decision", () -> delegate.getDecision(query)
        );
    }

    @Bean
    GetDecisionTimelineInputPort getDecisionTimelineInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetDecisionTimelineInputPort delegate = new GetDecisionTimelineUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_decision_timeline", () -> delegate.getDecisionTimeline(query)
        );
    }

    @Bean
    GetDecisionEvidenceInputPort getDecisionEvidenceInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetDecisionEvidenceInputPort delegate = new GetDecisionEvidenceUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_decision_evidence", () -> delegate.getDecisionEvidence(query)
        );
    }

    @Bean
    GetDecisionRoiInputPort getDecisionRoiInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetDecisionRoiInputPort delegate = new GetDecisionRoiUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_decision_roi", () -> delegate.getDecisionRoi(query)
        );
    }

    @Bean
    GetRecommendationInputPort getRecommendationInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetRecommendationInputPort delegate = new GetRecommendationUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_recommendation", () -> delegate.getRecommendation(query)
        );
    }

    @Bean
    GetDecisionLedgerInputPort getDecisionLedgerInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        GetDecisionLedgerInputPort delegate = new GetDecisionLedgerUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "get_decision_ledger", () -> delegate.getDecisionLedger(query)
        );
    }

    @Bean
    ListLedgerEntriesInputPort listLedgerEntriesInputPort(
            MvpReadModelQueryPort readModel,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ListLedgerEntriesInputPort delegate = new ListLedgerEntriesUseCase(readModel);
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null ? delegate : query -> observeDatabase(
                telemetry, "list_ledger_entries", () -> delegate.listLedgerEntries(query)
        );
    }

    @Bean
    ProjectBusinessValueInputPort projectBusinessValueInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner,
            ObjectProvider<ImperatorTelemetry> telemetryProvider
    ) {
        ProjectBusinessValueInputPort delegate = new ProjectBusinessValueUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
        ImperatorTelemetry telemetry = telemetryProvider.getIfAvailable();
        return telemetry == null
                ? delegate
                : ImperatorInputPortTelemetry.businessValue(delegate, telemetry);
    }

    private <T> T observeDatabase(
            ImperatorTelemetry telemetry,
            String operation,
            Supplier<T> authoritativeOperation
    ) {
        try {
            return authoritativeOperation.get();
        } catch (RuntimeException | Error failure) {
            telemetry.databaseFailure(operation, failure);
            throw failure;
        }
    }
}
