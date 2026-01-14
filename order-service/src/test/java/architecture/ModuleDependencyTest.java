package architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

/**
 * Enforces module boundaries.
 * <p>
 * ADRs: - ADR-001 (Modular Monolith) - ADR-003 (Deferred Microservices)
 * <p>
 * Rule: - architecture/rules/module-boundaries.md
 */
class ModuleDependencyTest extends ArchitectureTestSupport {

  @Test
  void order_api_must_not_access_database_or_integration() {

    noClasses()
      .that().resideInAPackage("..order.api..")
      .should().dependOnClassesThat()
      .resideInAnyPackage(
        "..order.database..",
        "..integration.."
      )
      .check(importAll());
  }
}
