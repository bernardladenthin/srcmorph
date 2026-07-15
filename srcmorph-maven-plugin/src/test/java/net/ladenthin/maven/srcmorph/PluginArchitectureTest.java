// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Random;
import org.slf4j.Logger;

/**
 * Architecture rules for the srcmorph Maven plugin module itself — the {@code mojo}
 * package (Mojo entry points) — after the {@code config}/{@code document}/{@code indexer}/
 * {@code prompt}/{@code provider}/{@code support} packages were extracted into the sibling
 * {@code srcmorph} core library. The rules that governed those layers (the internal layered
 * architecture, {@code nonMojoIsMavenFree}, {@code jniConfinedToProvider}, etc.) now live in that
 * module's own {@code CoreArchitectureTest}; only mojo-relevant rules remain here.
 */
@AnalyzeClasses(packages = "net.ladenthin.maven.srcmorph", importOptions = ImportOption.DoNotIncludeTests.class)
public class PluginArchitectureTest {

    /**
     * Maven plugins use Maven's Log interface for logging; java.util.logging must not be used.
     */
    @ArchTest
    static final ArchRule noJavaUtilLogging = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("java.util.logging..");

    /**
     * Test-framework classes must not appear in production code.
     */
    @ArchTest
    static final ArchRule noTestFrameworksInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.junit..", "net.jqwik..", "com.tngtech.archunit..");

    /**
     * Production code must not write to {@code System.out} / {@code System.err}; all output
     * goes through Maven's {@code Log} via {@code getLog()}. Currently vacuous (no usage); acts
     * as a regression guard.
     */
    @ArchTest
    static final ArchRule noSystemOutOrErrInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
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
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("sun..", "com.sun..", "jdk.internal..");

    /**
     * No package cycles between the layered sub-packages.
     */
    @ArchTest
    static final ArchRule noPackageCycles = slices().matching("net.ladenthin.maven.srcmorph.(*)..")
            .should()
            .beFreeOfCycles()
            .allowEmptyShould(true);

    /**
     * Layered architecture — now a single {@code Mojo} layer (the other tiers moved to the
     * srcmorph core library and are governed by that module's own {@code CoreArchitectureTest}).
     * Kept as a regression guard: nothing inside this module may depend on the {@code mojo}
     * package (the plugin's own code never needs to reach "up" into its own entry points).
     */
    @ArchTest
    static final ArchRule layeredArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Mojo")
            .definedBy("net.ladenthin.maven.srcmorph.mojo..")
            .whereLayer("Mojo")
            .mayNotBeAccessedByAnyLayer();

    /**
     * Public mutable state forbidden: any non-static field declared
     * {@code public} must also be {@code final}. Maven plugin configuration
     * POJOs (now in the srcmorph module) use {@code private} fields with setters;
     * this rule is a regression guard on whatever remains here.
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
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
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
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
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
            .resideInAPackage("net.ladenthin.maven.srcmorph..")
            .should()
            .callMethod(Thread.class, "sleep", long.class)
            .orShould()
            .callMethod(Thread.class, "sleep", long.class, int.class)
            .allowEmptyShould(true);

    /**
     * The Maven Mojo SPI annotations ({@code @Mojo} / {@code @Parameter} from
     * {@code org.apache.maven.plugins.annotations..}) may only appear in the {@code mojo}
     * package — the goal entry points. Any future non-mojo package added to this module must
     * stay plain Java and must not be annotated as a Maven component.
     */
    @ArchTest
    static final ArchRule mavenMojoAnnotationsConfinedToMojo = noClasses()
            .that()
            .resideOutsideOfPackage("net.ladenthin.maven.srcmorph.mojo..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.apache.maven.plugins.annotations..")
            .allowEmptyShould(true);

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
            .beFinal()
            .allowEmptyShould(true);
}
