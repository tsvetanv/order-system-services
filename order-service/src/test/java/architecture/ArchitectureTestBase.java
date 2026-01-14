package architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

/**
 * Base class for architectural fitness functions.
 * <p/>
 * Architecture sources:
 * <ul>
 *   <li>ADR-001, ... ,ADR-005</li>
 *   <li>Structurizr C4 model</li>
 *   <li>Architecture rules (Markdown)</li>
 * </ul>
 */
public abstract class ArchitectureTestBase {

  protected static final String BASE_PACKAGE =
    "com.tsvetanv.order.processing";

  protected static JavaClasses importedClasses() {
    return new ClassFileImporter()
      .importPackages(BASE_PACKAGE);
  }
}
