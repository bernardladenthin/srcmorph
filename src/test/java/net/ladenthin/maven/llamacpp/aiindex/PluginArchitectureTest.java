// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "net.ladenthin.maven.llamacpp.aiindex", importOptions = ImportOption.DoNotIncludeTests.class)
public class PluginArchitectureTest {

    /**
     * Maven plugins use Maven's Log interface for logging; java.util.logging must not be used.
     */
    @ArchTest
    static final ArchRule noJavaUtilLogging = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("java.util.logging..");

    /**
     * Test-framework classes must not appear in production code.
     */
    @ArchTest
    static final ArchRule noTestFrameworksInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.junit..", "net.jqwik..", "com.tngtech.archunit..");

    /**
     * Production code must not write to {@code System.out} / {@code System.err}; all output
     * goes through Maven's {@link org.apache.maven.plugin.logging.Log}. Currently vacuous
     * (no usage); acts as a regression guard.
     */
    @ArchTest
    static final ArchRule noSystemOutOrErrInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .accessField(System.class, "out")
            .orShould()
            .accessField(System.class, "err");

    /**
     * Production code must not import unsupported / internal JDK packages.
     * These are not part of the Java SE API and may change or disappear without notice.
     */
    @ArchTest
    static final ArchRule noInternalJdkImports = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("sun..", "com.sun..", "jdk.internal..");
}
