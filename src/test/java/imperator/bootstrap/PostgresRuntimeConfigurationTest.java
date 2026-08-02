package imperator.bootstrap;

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
import imperator.application.reviewdecision.ReviewDecisionUseCase;
import imperator.application.businessvalue.ProjectBusinessValueUseCase;
import imperator.application.query.GetDecisionEvidenceUseCase;
import imperator.application.query.GetDecisionLedgerUseCase;
import imperator.application.query.GetDecisionRoiUseCase;
import imperator.application.query.GetDecisionTimelineUseCase;
import imperator.application.query.GetDecisionUseCase;
import imperator.application.query.GetRecommendationUseCase;
import imperator.application.query.ListDecisionsUseCase;
import imperator.application.query.ListLedgerEntriesUseCase;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.GetDecisionEvidenceInputPort;
import imperator.ports.in.GetDecisionInputPort;
import imperator.ports.in.GetDecisionLedgerInputPort;
import imperator.ports.in.GetDecisionRoiInputPort;
import imperator.ports.in.GetDecisionTimelineInputPort;
import imperator.ports.in.GetRecommendationInputPort;
import imperator.ports.in.ListDecisionsInputPort;
import imperator.ports.in.ListLedgerEntriesInputPort;
import imperator.ports.in.ProjectBusinessValueInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.LedgerRepository;
import imperator.ports.out.MvpReadModelQueryPort;
import imperator.ports.out.RecommendationRepository;
import imperator.ports.out.TransactionRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class PostgresRuntimeConfigurationTest {

    @Test
    void composesExistingPortsAdaptersAndUseCasesWithoutOpeningAConnection() {
        SpringApplication application = new SpringApplication(ImperatorApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setLogStartupInfo(false);
        application.setDefaultProperties(Map.of(
                "imperator.postgresql.enabled", "true",
                "imperator.postgresql.url", "jdbc:postgresql://127.0.0.1:1/imperator",
                "imperator.postgresql.username", "imperator_app",
                "imperator.postgresql.password", "not-used"
        ));

        try (ConfigurableApplicationContext context = application.run(
                "--spring.main.banner-mode=off",
                "--debug=false",
                "--logging.level.root=OFF"
        )) {
            assertInstanceOf(
                    PostgresEvidenceRepository.class,
                    context.getBean(EvidenceRepository.class)
            );
            assertInstanceOf(
                    PostgresDecisionRepository.class,
                    context.getBean(DecisionRepository.class)
            );
            assertInstanceOf(
                    PostgresRecommendationRepository.class,
                    context.getBean(RecommendationRepository.class)
            );
            assertInstanceOf(
                    PostgresLedgerRepository.class,
                    context.getBean(LedgerRepository.class)
            );
            assertInstanceOf(
                    PostgresTransactionRunner.class,
                    context.getBean(TransactionRunner.class)
            );
            assertInstanceOf(
                    ExplanationProvider.class,
                    context.getBean(ExplanationProvider.class)
            );
            assertInstanceOf(
                    ImportEvidenceUseCase.class,
                    context.getBean(ImportEvidenceInputPort.class)
            );
            assertInstanceOf(
                    CreateDecisionUseCase.class,
                    context.getBean(CreateDecisionInputPort.class)
            );
            assertInstanceOf(
                    GenerateRecommendationUseCase.class,
                    context.getBean(GenerateRecommendationInputPort.class)
            );
            assertInstanceOf(
                    ReviewDecisionUseCase.class,
                    context.getBean(ReviewDecisionInputPort.class)
            );
            assertInstanceOf(
                    AppendLedgerEntryUseCase.class,
                    context.getBean(AppendLedgerEntryInputPort.class)
            );
            assertInstanceOf(
                    PostgresMvpReadModelQueryAdapter.class,
                    context.getBean(MvpReadModelQueryPort.class)
            );
            assertInstanceOf(ListDecisionsUseCase.class, context.getBean(ListDecisionsInputPort.class));
            assertInstanceOf(GetDecisionUseCase.class, context.getBean(GetDecisionInputPort.class));
            assertInstanceOf(
                    GetDecisionTimelineUseCase.class,
                    context.getBean(GetDecisionTimelineInputPort.class)
            );
            assertInstanceOf(
                    GetDecisionEvidenceUseCase.class,
                    context.getBean(GetDecisionEvidenceInputPort.class)
            );
            assertInstanceOf(GetDecisionRoiUseCase.class, context.getBean(GetDecisionRoiInputPort.class));
            assertInstanceOf(
                    GetRecommendationUseCase.class,
                    context.getBean(GetRecommendationInputPort.class)
            );
            assertInstanceOf(
                    GetDecisionLedgerUseCase.class,
                    context.getBean(GetDecisionLedgerInputPort.class)
            );
            assertInstanceOf(
                    ListLedgerEntriesUseCase.class,
                    context.getBean(ListLedgerEntriesInputPort.class)
            );
            assertInstanceOf(
                    ProjectBusinessValueUseCase.class,
                    context.getBean(ProjectBusinessValueInputPort.class)
            );
        }
    }
}
