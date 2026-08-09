package imperator.bootstrap;

import imperator.adapters.out.postgresql.PostgresConnectionProvider;
import imperator.adapters.out.postgresql.PostgresDataSource;
import imperator.adapters.out.postgresql.PostgresDecisionRepository;
import imperator.adapters.out.postgresql.PostgresEvidenceRepository;
import imperator.adapters.out.postgresql.PostgresLedgerRepository;
import imperator.adapters.out.postgresql.PostgresMvpReadModelQueryAdapter;
import imperator.adapters.out.postgresql.PostgresRecommendationRepository;
import imperator.adapters.out.postgresql.PostgresTransactionRunner;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
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

import javax.sql.DataSource;
import java.util.Optional;

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
            TransactionRunner transactionRunner
    ) {
        return new ImportEvidenceUseCase(evidenceRepository, transactionRunner);
    }

    @Bean("githubSynchronizeEvidenceInputPort")
    SynchronizeEvidenceInputPort githubSynchronizeEvidenceInputPort(
            @Qualifier("githubEvidenceSourcePort") EvidenceSourcePort evidenceSource,
            ImportEvidenceInputPort evidenceImporter,
            EvidenceRepository evidenceRepository
    ) {
        return new SynchronizeEvidenceUseCase(
                evidenceSource,
                evidenceImporter,
                evidenceRepository
        );
    }

    @Bean("awsSynchronizeEvidenceInputPort")
    SynchronizeEvidenceInputPort awsSynchronizeEvidenceInputPort(
            @Qualifier("awsEvidenceSourcePort") EvidenceSourcePort evidenceSource,
            ImportEvidenceInputPort evidenceImporter,
            EvidenceRepository evidenceRepository
    ) {
        return new SynchronizeEvidenceUseCase(
                evidenceSource,
                evidenceImporter,
                evidenceRepository
        );
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
    ReviewDecisionInputPort reviewDecisionInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        return new ReviewDecisionUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
    }

    @Bean
    AppendLedgerEntryInputPort appendLedgerEntryInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        return new AppendLedgerEntryUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
    }

    @Bean
    ListDecisionsInputPort listDecisionsInputPort(MvpReadModelQueryPort readModel) {
        return new ListDecisionsUseCase(readModel);
    }

    @Bean
    GetDecisionInputPort getDecisionInputPort(MvpReadModelQueryPort readModel) {
        return new GetDecisionUseCase(readModel);
    }

    @Bean
    GetDecisionTimelineInputPort getDecisionTimelineInputPort(MvpReadModelQueryPort readModel) {
        return new GetDecisionTimelineUseCase(readModel);
    }

    @Bean
    GetDecisionEvidenceInputPort getDecisionEvidenceInputPort(MvpReadModelQueryPort readModel) {
        return new GetDecisionEvidenceUseCase(readModel);
    }

    @Bean
    GetDecisionRoiInputPort getDecisionRoiInputPort(MvpReadModelQueryPort readModel) {
        return new GetDecisionRoiUseCase(readModel);
    }

    @Bean
    GetRecommendationInputPort getRecommendationInputPort(MvpReadModelQueryPort readModel) {
        return new GetRecommendationUseCase(readModel);
    }

    @Bean
    GetDecisionLedgerInputPort getDecisionLedgerInputPort(MvpReadModelQueryPort readModel) {
        return new GetDecisionLedgerUseCase(readModel);
    }

    @Bean
    ListLedgerEntriesInputPort listLedgerEntriesInputPort(MvpReadModelQueryPort readModel) {
        return new ListLedgerEntriesUseCase(readModel);
    }

    @Bean
    ProjectBusinessValueInputPort projectBusinessValueInputPort(
            DecisionRepository decisionRepository,
            RecommendationRepository recommendationRepository,
            EvidenceRepository evidenceRepository,
            LedgerRepository ledgerRepository,
            TransactionRunner transactionRunner
    ) {
        return new ProjectBusinessValueUseCase(
                decisionRepository,
                recommendationRepository,
                evidenceRepository,
                ledgerRepository,
                transactionRunner
        );
    }
}
