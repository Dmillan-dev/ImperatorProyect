package imperator.bootstrap;

import imperator.adapters.out.postgresql.PostgresDecisionRepository;
import imperator.adapters.out.postgresql.PostgresEvidenceRepository;
import imperator.adapters.out.postgresql.PostgresLedgerRepository;
import imperator.adapters.out.postgresql.PostgresRecommendationRepository;
import imperator.adapters.out.postgresql.PostgresTransactionRunner;
import imperator.application.appendledgerentry.AppendLedgerEntryUseCase;
import imperator.application.createdecision.CreateDecisionUseCase;
import imperator.application.generaterecommendation.GenerateRecommendationUseCase;
import imperator.application.importevidence.ImportEvidenceUseCase;
import imperator.application.reviewdecision.ReviewDecisionUseCase;
import imperator.ports.in.AppendLedgerEntryInputPort;
import imperator.ports.in.CreateDecisionInputPort;
import imperator.ports.in.GenerateRecommendationInputPort;
import imperator.ports.in.ImportEvidenceInputPort;
import imperator.ports.in.ReviewDecisionInputPort;
import imperator.ports.out.DecisionRepository;
import imperator.ports.out.EvidenceRepository;
import imperator.ports.out.ExplanationProvider;
import imperator.ports.out.LedgerRepository;
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
        }
    }
}
