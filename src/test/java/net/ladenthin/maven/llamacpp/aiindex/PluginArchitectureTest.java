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
}
