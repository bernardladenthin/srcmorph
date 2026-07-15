// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph;

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
 * Architecture rules for the srcmorph core library (extracted from the
 * llamacpp-ai-index-maven-plugin's non-mojo layers: config/document/indexer/prompt/provider/
 * support). This library is framework-free — no Maven plugin API dependency anywhere — so it can
 * be unit-tested and consumed without a Maven runtime. The Mojo entry points that build on top of
 * it live in the sibling {@code llamacpp-ai-index-maven-plugin} module, whose own
 * {@code PluginArchitectureTest} carries the mojo-specific rules (Maven annotations confined to
 * {@code mojo}, {@code AbstractMojo} inheritance, etc.) that have no meaning here.
 */
@AnalyzeClasses(packages = "net.ladenthin.srcmorph", importOptions = ImportOption.DoNotIncludeTests.class)
public class CoreArchitectureTest {

    /**
     * java.util.logging must not be used; all logging goes through SLF4J.
     */
    @ArchTest
    static final ArchRule noJavaUtilLogging = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("java.util.logging..");

    /**
     * Test-framework classes must not appear in production code.
     */
    @ArchTest
    static final ArchRule noTestFrameworksInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("org.junit..", "net.jqwik..", "com.tngtech.archunit..");

    /**
     * Production code must not write to {@code System.out} / {@code System.err}; all output
     * goes through an SLF4J {@link Logger}. Currently vacuous (no usage); acts as a regression
     * guard.
     */
    @ArchTest
    static final ArchRule noSystemOutOrErrInProduction = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph..")
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
            .resideInAPackage("net.ladenthin.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("sun..", "com.sun..", "jdk.internal..");

    /**
     * No package cycles between the layered sub-packages.
     */
    @ArchTest
    static final ArchRule noPackageCycles = slices().matching("net.ladenthin.srcmorph.(*)..")
            .should()
            .beFreeOfCycles()
            .allowEmptyShould(true);

    /**
     * Strict layered architecture — <b>one layer per package</b>. Each package's
     * {@code mayOnlyBeAccessedByLayers} lists the EXACT set of packages that reference it today
     * (verified against the compiled bytecode graph), so even intra-tier edges are governed
     * (e.g. {@code provider} may use {@code document}+{@code prompt} but {@code document} and
     * {@code prompt} may not use each other beyond what is listed). A new dependency between any
     * two packages fails the build unless this rule is updated to intend it. Mirrors the pre-
     * extraction {@code PluginArchitectureTest} layering with the {@code Mojo} layer removed (the
     * mojo package lives in the sibling plugin module and is outside this test's analyzed
     * classes; consideringOnlyDependenciesInLayers() means its former accesses to Indexer/
     * Provider/Prompt/Config/Support are simply not part of this in-module rule anymore).
     * Conceptual tiers: {@code Indexer} &gt; {@code Provider} &gt; {@code Document}/{@code Prompt}
     * &gt; {@code Config}/{@code Support}.
     */
    @ArchTest
    static final ArchRule layeredArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Indexer")
            .definedBy("net.ladenthin.srcmorph.indexer..")
            .layer("Provider")
            .definedBy("net.ladenthin.srcmorph.provider..")
            .layer("Document")
            .definedBy("net.ladenthin.srcmorph.document..")
            .layer("Prompt")
            .definedBy("net.ladenthin.srcmorph.prompt..")
            .layer("Config")
            .definedBy("net.ladenthin.srcmorph.config..")
            .layer("Support")
            .definedBy("net.ladenthin.srcmorph.support..")
            .whereLayer("Indexer")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Provider")
            .mayOnlyBeAccessedByLayers("Indexer")
            .whereLayer("Document")
            .mayOnlyBeAccessedByLayers("Indexer", "Prompt", "Provider")
            .whereLayer("Prompt")
            .mayOnlyBeAccessedByLayers("Indexer", "Provider")
            .whereLayer("Config")
            .mayOnlyBeAccessedByLayers("Indexer")
            .whereLayer("Support")
            .mayOnlyBeAccessedByLayers("Config", "Document", "Indexer", "Prompt", "Provider");

    /**
     * Public mutable state forbidden: any non-static field declared
     * {@code public} must also be {@code final}. Maven plugin configuration
     * POJOs use {@code private} fields with setters and therefore pass this rule.
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
     * Production code must not call {@link System#exit(int)}; throw a domain exception instead
     * so the calling Mojo (or any other embedder) surfaces the failure to its caller.
     */
    @ArchTest
    static final ArchRule noSystemExit = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph..")
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
            .resideInAPackage("net.ladenthin.srcmorph..")
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
            .resideInAPackage("net.ladenthin.srcmorph..")
            .should()
            .callMethod(Thread.class, "sleep", long.class)
            .orShould()
            .callMethod(Thread.class, "sleep", long.class, int.class)
            .allowEmptyShould(true);

    // ---------------------------------------------------------------------------------------
    // Per-module banned imports — confine the heavy / framework-specific dependencies to the
    // single layer that owns them, keeping the rest of the library decoupled and unit-testable.
    // ---------------------------------------------------------------------------------------

    /**
     * The llama.cpp JNI binding ({@code net.ladenthin.llama..}) may only be referenced from the
     * {@code provider} package, where {@code LlamaCppJniAiGenerationProvider} wraps it. Every
     * other layer talks to the model exclusively through the {@code AiGenerationProvider}
     * interface, so the indexers and document/prompt/config code carry no JNI dependency and
     * stay testable with {@code MockAiGenerationProvider}.
     */
    @ArchTest
    static final ArchRule jniConfinedToProvider = noClasses()
            .that()
            .resideOutsideOfPackage("net.ladenthin.srcmorph.provider..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("net.ladenthin.llama..")
            .allowEmptyShould(true);

    /**
     * This library must be entirely framework-free: no Maven API at all (not even {@code Log}).
     * Every class here is plain Java, so it can be unit-tested and consumed without a Maven
     * runtime — that is the whole point of the extraction. The Maven Plugin API boundary
     * ({@code AbstractMojo}, {@code @Parameter}, {@code MojoExecutionException}, the {@code mojo}
     * package's Maven Mojo SPI annotations) lives entirely in the sibling
     * {@code llamacpp-ai-index-maven-plugin} module, which is outside this test's analyzed
     * classes and carries its own {@code mavenMojoAnnotationsConfinedToMojo} rule.
     */
    @ArchTest
    static final ArchRule coreIsMavenFree = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.apache.maven..")
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
