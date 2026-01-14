package architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * Protects OpenAPI as the source of truth.
 * <p>
 * ADRs: - ADR-001 (Container responsibilities)
 * <p>
 * Rule: - architecture/rules/api-contract.md
 */
class ApiContractTest extends ArchitectureTestSupport {

  @Test
  void order_api_must_not_define_controllers() {

    noClasses()
      .that().resideInAPackage("..order.api..")
      .should().beAnnotatedWith(RestController.class)
      .orShould().beAnnotatedWith(Controller.class)
      .check(importAll());
  }
}
