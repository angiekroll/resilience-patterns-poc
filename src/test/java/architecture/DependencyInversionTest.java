/**
 * Copyright 2025, Company. All rights reserved Date: 16/10/25
 */
package architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author angiekroll@gmail.com - Ángela Carolina Castillo Rodríguez.
 * @version - 1.0.0
 * @since - 1.0.0
 */

@DisplayName("Hexagonal Architecture")
@AnalyzeClasses(packages = "com.resiliencepatterns.poc")
public class DependencyInversionTest {

  // ========== DOMAIN LAYER RULES ==========
  @ArchTest
  static final ArchRule domain_should_not_depend_on_application =
      noClasses()
          .that().resideInAPackage("..domain..")
          .should().dependOnClassesThat()
          .resideInAPackage("..application..")
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule domain_should_not_depend_on_infrastructure =
      noClasses()
          .that().resideInAPackage("..domain..")
          .should().dependOnClassesThat()
          .resideInAPackage("..infrastructure..")
          .allowEmptyShould(true);


  // ========== APPLICATION LAYER RULES ==========
  @ArchTest
  static final ArchRule application_should_not_depend_on_infrastructure =
      noClasses()
          .that().resideInAPackage("..application..")
          .should().dependOnClassesThat()
          .resideInAPackage("..infrastructure..")
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule application_should_only_be_accessed_by_infrastructure =
      classes()
          .that().resideInAPackage("..application..")
          .should().onlyBeAccessed().byAnyPackage("..infrastructure..", "..application..")
          .allowEmptyShould(true);


  // ========== PORTS AND ADAPTERS RULES ==========
  @ArchTest
  static final ArchRule ports_should_be_interfaces =
      classes()
          .that().resideInAPackage("..application.port..")
          .should().beInterfaces()
          .allowEmptyShould(true);

  @ArchTest
  static final ArchRule adapters_should_not_be_accessed_by_domain_or_application =
      noClasses()
          .that().resideInAnyPackage("..domain..", "..application..")
          .should().dependOnClassesThat()
          .resideInAPackage("..infrastructure.adapters..")
          .allowEmptyShould(true);

}