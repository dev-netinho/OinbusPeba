package br.edu.oinbupebas;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes; import com.tngtech.archunit.core.importer.ImportOption; import com.tngtech.archunit.junit.*; import com.tngtech.archunit.lang.ArchRule;
@AnalyzeClasses(packages="br.edu.oinbupebas",importOptions=ImportOption.DoNotIncludeTests.class) class ArchitectureTest {
 @ArchTest static final ArchRule identityRepositoriesArePrivate=
   classes().that().haveSimpleNameEndingWith("Repository").and().resideInAPackage("..identity..").should().onlyBeAccessed().byAnyPackage("..identity..");
 @ArchTest static final ArchRule personalizationRepositoriesArePrivate=
   classes().that().haveSimpleNameEndingWith("Repository").and().resideInAPackage("..personalization..").should().onlyBeAccessed().byAnyPackage("..personalization..");
 @ArchTest static final ArchRule operationsRepositoriesArePrivate=
   classes().that().haveSimpleNameEndingWith("Repository").and().resideInAPackage("..operations..").should().onlyBeAccessed().byAnyPackage("..operations..");
}
