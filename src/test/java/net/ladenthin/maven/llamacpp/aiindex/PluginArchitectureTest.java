// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Random;

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

    /**
     * No package cycles between sub-packages. Vacuous today on this
     * single-package plugin; acts as a forward-looking guard so a future
     * sub-package extraction cannot introduce a circular dependency without
     * breaking the build.
     */
    @ArchTest
    static final ArchRule noPackageCycles = slices().matching("net.ladenthin.maven.llamacpp.aiindex.(*)..")
            .should()
            .beFreeOfCycles()
            .allowEmptyShould(true);

    /**
     * Public mutable state forbidden: any non-static field declared
     * {@code public} must also be {@code final}. Maven plugin configuration
     * POJOs use {@code private} fields with setters
     * ({@link AiPromptDefinition}, {@link AiModelDefinition}, etc.) and
     * therefore pass this rule.
     */
    @ArchTest
    static final ArchRule noPublicMutableFields = fields().that()
            .arePublic()
            .and()
            .areNotStatic()
            .should()
            .beFinal()
            .allowEmptyShould(true); // regression guard; passes vacuously today

    /**
     * Production code must not call {@link System#exit(int)}; throw a
     * {@link org.apache.maven.plugin.MojoExecutionException} or
     * {@link org.apache.maven.plugin.MojoFailureException} instead so Maven
     * surfaces the failure to its caller.
     */
    @ArchTest
    static final ArchRule noSystemExit = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .callMethod(System.class, "exit", int.class)
            .allowEmptyShould(true);

    /**
     * Production code must not construct {@link java.util.Random}; {@code Random} is a non-cryptographic
     * PRNG (CWE-338). Use {@link java.security.SecureRandom} or {@link java.util.concurrent.ThreadLocalRandom}
     * depending on whether cryptographic strength or thread-local fast jitter is needed.
     */
    @ArchTest
    static final ArchRule noNewRandom = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .callConstructor(Random.class)
            .orShould()
            .callConstructor(Random.class, long.class)
            .allowEmptyShould(true);

    /**
     * Production code must not call {@link Thread#sleep(long)} / {@link Thread#sleep(long, int)};
     * prefer {@link java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)} or
     * {@link java.util.concurrent.locks.Condition#await(long, java.util.concurrent.TimeUnit)}.
     */
    @ArchTest
    static final ArchRule noThreadSleep = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.llamacpp.aiindex..")
            .should()
            .callMethod(Thread.class, "sleep", long.class)
            .orShould()
            .callMethod(Thread.class, "sleep", long.class, int.class)
            .allowEmptyShould(true);
}
