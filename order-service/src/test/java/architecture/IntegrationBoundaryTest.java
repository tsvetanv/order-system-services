package architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.Test;

/**
 * Enforces Integration Service as Anti-Corruption Layer (ACL).
 * <p>
 * ADRs: - ADR-002 (Integration Model)
 * <p>
 * Allowed: - Order Service → Integration *interfaces*
 * <p>
 * Forbidden: - Order Service → external client libraries - Order Service → integration
 * implementation details
 */
class IntegrationBoundaryTest extends ArchitectureTestSupport {

  @Test
  void order_service_must_not_depend_on_external_clients_or_adapters() {

    noClasses()
      .that().resideInAPackage("..order.service..")
      .should().dependOnClassesThat()
      .resideInAnyPackage(
        // HTTP clients
        "org.apache.http..",
        "okhttp3..",
        "retrofit2..",
        "feign..",
        // Cloud / vendor SDKs
        "com.amazonaws..",
        "software.amazon.awssdk..",
        // Integration implementation packages (future-proof)
        "..integration..impl..",
        "..integration..adapter.."
      )
      .check(importAll());
  }
}
