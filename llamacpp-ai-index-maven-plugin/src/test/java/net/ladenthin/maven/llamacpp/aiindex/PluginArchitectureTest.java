// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Random;
import net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition;
import org.slf4j.Logger;

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
     * goes through an SLF4J {@link Logger} (mojo-lifecycle messages still use Maven's
     * {@code Log} via {@code getLog()}). Currently vacuous (no usage); acts as a regression
     * guard.
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
     * No package cycles between the layered sub-packages.
     */
    @ArchTest
    static final ArchRule noPackageCycles = slices().matching("net.ladenthin.maven.llamacpp.aiindex.(*)..")
            .should()
            .beFreeOfCycles()
            .allowEmptyShould(true);

    /**
     * Strict layered architecture — <b>one layer per package</b>. Each package's
     * {@code mayOnlyBeAccessedByLayers} lists the EXACT set of packages that reference it today
     * (verified against the compiled bytecode graph), so even intra-tier edges are governed
     * (e.g. {@code provider} may use {@code document}+{@code prompt} but {@code document} and
     * {@code prompt} may not use each other beyond what is listed). A new dependency between any
     * two packages fails the build unless this rule is updated to intend it. Conceptual tiers:
     * {@code Mojo} &gt; {@code Indexer} &gt; {@code Provider} &gt; {@code Document}/{@code Prompt}
     * &gt; {@code Config}/{@code Support}.
     */
    @ArchTest
    static final ArchRule layeredArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Mojo")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.mojo..")
            .layer("Indexer")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.indexer..")
            .layer("Provider")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.provider..")
            .layer("Document")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.document..")
            .layer("Prompt")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.prompt..")
            .layer("Config")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.config..")
            .layer("Support")
            .definedBy("net.ladenthin.maven.llamacpp.aiindex.support..")
            .whereLayer("Mojo")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Indexer")
            .mayOnlyBeAccessedByLayers("Mojo")
            .whereLayer("Provider")
            .mayOnlyBeAccessedByLayers("Mojo", "Indexer")
            .whereLayer("Document")
            .mayOnlyBeAccessedByLayers("Indexer", "Prompt", "Provider")
            .whereLayer("Prompt")
            .mayOnlyBeAccessedByLayers("Indexer", "Mojo", "Provider")
            .whereLayer("Config")
            .mayOnlyBeAccessedByLayers("Indexer", "Mojo")
            .whereLayer("Support")
            .mayOnlyBeAccessedByLayers("Config", "Document", "Indexer", "Mojo", "Prompt", "Provider");

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

    // ---------------------------------------------------------------------------------------
    // Per-module banned imports — confine the heavy / framework-specific dependencies to the
    // single layer that owns them, keeping the rest of the plugin decoupled and unit-testable.
    // ---------------------------------------------------------------------------------------

    /**
     * The llama.cpp JNI binding ({@code net.ladenthin.llama..}) may only be referenced from the
     * {@code provider} package, where {@code LlamaCppJniAiGenerationProvider} wraps it. Every
     * other layer talks to the model exclusively through the {@code AiGenerationProvider}
     * interface, so the indexers, document/prompt/config and mojo code carry no JNI dependency
     * and stay testable with {@code MockAiGenerationProvider}.
     */
    @ArchTest
    static final ArchRule jniConfinedToProvider = noClasses()
            .that()
            .resideOutsideOfPackage("net.ladenthin.maven.llamacpp.aiindex.provider..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("net.ladenthin.llama..")
            .allowEmptyShould(true);

    /**
     * The Maven Mojo SPI annotations ({@code @Mojo} / {@code @Parameter} from
     * {@code org.apache.maven.plugins.annotations..}) may only appear in the {@code mojo}
     * package — the goal entry points. The lower layers are plain Java and must not be
     * annotated as Maven components.
     */
    @ArchTest
    static final ArchRule mavenMojoAnnotationsConfinedToMojo = noClasses()
            .that()
            .resideOutsideOfPackage("net.ladenthin.maven.llamacpp.aiindex.mojo..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.apache.maven.plugins.annotations..")
            .allowEmptyShould(true);

    /**
     * Every layer except {@code mojo} must be framework-free: no Maven API at all (not even
     * {@code Log} — logging in {@code indexer} goes through an SLF4J {@link Logger} instead).
     * They are plain Java, so they can be unit-tested and eventually extracted into a
     * standalone library without a Maven runtime. Only {@code mojo} is the Maven Plugin API
     * boundary ({@code AbstractMojo}, {@code @Parameter}, {@code MojoExecutionException}, and
     * the injected {@code getLog()} for mojo-lifecycle messages).
     */
    @ArchTest
    static final ArchRule nonMojoIsMavenFree = noClasses()
            .that()
            .resideInAnyPackage(
                    "net.ladenthin.maven.llamacpp.aiindex.config..",
                    "net.ladenthin.maven.llamacpp.aiindex.support..",
                    "net.ladenthin.maven.llamacpp.aiindex.document..",
                    "net.ladenthin.maven.llamacpp.aiindex.prompt..",
                    "net.ladenthin.maven.llamacpp.aiindex.provider..",
                    "net.ladenthin.maven.llamacpp.aiindex.indexer..")
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
