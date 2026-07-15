// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.slf4j.Logger;

/**
 * Architecture rules for the srcmorph CLI ({@code net.ladenthin.srcmorph.cli}).
 *
 * <p>Mirrors the conventions established by {@code CoreArchitectureTest} (the {@code srcmorph}
 * module) and BitcoinAddressFinder's own {@code BitcoinAddressFinderArchitectureTest}: no
 * {@code System.exit}, no public mutable fields outside the {@code configuration} package's
 * Jackson-bound JavaBeans, no reverse dependency onto this module (it is the reactor's leaf-most
 * consumer, nothing else may depend on it), and no accidental dependency on the Maven plugin API
 * (this is a plain standalone CLI, not a Maven-aware component).</p>
 */
@AnalyzeClasses(packages = "net.ladenthin.srcmorph.cli", importOptions = ImportOption.DoNotIncludeTests.class)
public class CliArchitectureTest {

    /**
     * The CLI is the reactor's leaf-most consumer: {@code srcmorph-cli} is not a compile dependency
     * of any sibling reactor module (only {@code srcmorph}'s core library is — see the root
     * {@code pom.xml}'s module comment), so nothing else in the reactor can ever depend on it by
     * construction (a reverse dependency would simply fail to resolve/compile). Because
     * {@code @AnalyzeClasses} here only scans this module's own {@code net.ladenthin.srcmorph.cli}
     * package tree, the in-module half of that check is necessarily vacuous
     * ({@code allowEmptyShould(true)}); this rule exists as a documentation-level regression guard
     * should a future package ever be added to this module outside {@code cli..} that reaches back
     * into it incorrectly.
     */
    @ArchTest
    static final ArchRule cliIsLeaf = noClasses()
            .that()
            .resideOutsideOfPackage("net.ladenthin.srcmorph.cli..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("net.ladenthin.srcmorph.cli..")
            .allowEmptyShould(true);

    /**
     * Public mutable state forbidden: any non-static field declared {@code public} must also be
     * {@code final}. The single documented exception is
     * {@code net.ladenthin.srcmorph.cli.configuration} — the Jackson-bound JavaBeans
     * ({@code CConfiguration}) that mirror the core library's own {@code SrcMorphConfiguration} and
     * BitcoinAddressFinder's {@code cli.configuration.CConfiguration} convention.
     */
    @ArchTest
    static final ArchRule noPublicMutableFields = fields().that()
            .arePublic()
            .and()
            .areNotStatic()
            .and()
            .areDeclaredInClassesThat()
            .resideOutsideOfPackage("net.ladenthin.srcmorph.cli.configuration..")
            .should()
            .beFinal()
            .allowEmptyShould(true); // vacuous today: Main has no public instance fields

    /**
     * Production code must not call {@link System#exit(int)}; a failure propagates as an unchecked
     * exception out of {@link Main#main(String[])} instead, so the JVM's own uncaught-exception
     * handling reports it without bypassing normal shutdown.
     */
    @ArchTest
    static final ArchRule noSystemExit = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph.cli..")
            .should()
            .callMethod(System.class, "exit", int.class)
            .allowEmptyShould(true);

    /**
     * This is a plain standalone CLI, not a Maven-aware component: it must never depend on the
     * Maven Plugin API (the {@code llamacpp-ai-index-maven-plugin} sibling module owns that
     * boundary).
     */
    @ArchTest
    static final ArchRule mavenFree = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph.cli..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.apache.maven..")
            .allowEmptyShould(true);

    /**
     * Test-framework classes must not appear in production code.
     */
    @ArchTest
    static final ArchRule noTestFrameworksInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph.cli..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.junit..", "net.jqwik..", "com.tngtech.archunit..");

    /**
     * Production code must not import unsupported / internal JDK packages.
     */
    @ArchTest
    static final ArchRule noInternalJdkImports = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph.cli..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("sun..", "com.sun..", "jdk.internal..");

    /**
     * Every SLF4J {@link Logger} field must be {@code private static final} — a single shared
     * logger per class, never an instance field or a mutable/visible one.
     */
    @ArchTest
    static final ArchRule loggersArePrivateStaticFinal = fields().that()
            .haveRawType(Logger.class)
            .should()
            .bePrivate()
            .andShould()
            .beStatic()
            .andShould()
            .beFinal();
}
