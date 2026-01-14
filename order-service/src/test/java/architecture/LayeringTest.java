package architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Test;

/**
 * Layering rules for the Order Service module.
 * <p>
 * Scope: - order-service module only
 * <p>
 * ADRs: - ADR-001 (Modular Monolith)
 * <p>
 * Notes: - Domain lives in order-database module - Infrastructure is outside Java code (Terraform
 * repo) - Mapping and Application collaborate intentionally
 */
class LayeringTest extends ArchitectureTestSupport {

  @Test
  void order_service_layering_is_respected() {

    layeredArchitecture()
      .consideringAllDependencies()

      .layer("API").definedBy("..order.service.api..")
      .layer("Application").definedBy("..order.service.application..")
      .layer("Mapping").definedBy("..order.service.mapping..")

      // API is an entry point only
      .whereLayer("API").mayNotBeAccessedByAnyLayer()

      // Application and Mapping collaborate
      .whereLayer("Application").mayOnlyBeAccessedByLayers("API", "Mapping")
      .whereLayer("Mapping").mayOnlyBeAccessedByLayers("API", "Application")

      .check(importOrderServiceOnly());
  }
}
