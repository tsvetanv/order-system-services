package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Shared support for architectural fitness functions.
 * <p>
 * Architecture sources: - ADRs - Structurizr C4 model - Architecture rules (Markdown)
 */
abstract class ArchitectureTestSupport {

  protected static JavaClasses importAll() {
    return new ClassFileImporter()
      .importPackages("com.tsvetanv.order.processing");
  }

  protected static JavaClasses importOrderServiceOnly() {
    return new ClassFileImporter()
      .importPackages("com.tsvetanv.order.processing.order.service");
  }
}
