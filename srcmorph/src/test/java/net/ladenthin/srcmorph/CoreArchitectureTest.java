// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaType;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * Strict layered architecture, <b>one layer per package</b> ({@code indexer} with its
     * {@code generation} sub-package). Each layer's {@code mayOnlyBeAccessedByLayers} lists the exact
     * set of layers that reference it today (checked against the compiled bytecode), so a new
     * dependency between two packages fails the build unless this rule is updated to intend it. The
     * mojo package lives in the plugin module and is outside this test's analyzed classes. Tiers:
     * {@code engine} &gt; {@code indexer} &gt; {@code provider}, {@code document}, {@code routing}
     * &gt; {@code prompt} &gt; {@code facts} &gt; {@code config} &gt; {@code support}: the beans in
     * {@code config} depend on nothing but {@code support}, the fact counters and the routing read
     * the beans, a request ({@code prompt}) is what a provider answers, and the {@code .ai.md}
     * header reads the facts line's marker.
     */
    @ArchTest
    static final ArchRule layeredArchitecture = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Engine")
            .definedBy("net.ladenthin.srcmorph.engine..")
            .layer("Indexer")
            .definedBy("net.ladenthin.srcmorph.indexer..")
            .layer("Provider")
            .definedBy("net.ladenthin.srcmorph.provider..")
            .layer("Document")
            .definedBy("net.ladenthin.srcmorph.document..")
            .layer("Routing")
            .definedBy("net.ladenthin.srcmorph.routing..")
            .layer("Prompt")
            .definedBy("net.ladenthin.srcmorph.prompt..")
            .layer("Facts")
            .definedBy("net.ladenthin.srcmorph.facts..")
            .layer("Config")
            .definedBy("net.ladenthin.srcmorph.config..")
            .layer("Support")
            .definedBy("net.ladenthin.srcmorph.support..")
            .whereLayer("Engine")
            .mayNotBeAccessedByAnyLayer()
            .whereLayer("Indexer")
            .mayOnlyBeAccessedByLayers("Engine")
            .whereLayer("Provider")
            .mayOnlyBeAccessedByLayers("Indexer", "Engine")
            .whereLayer("Document")
            .mayOnlyBeAccessedByLayers("Indexer")
            .whereLayer("Routing")
            .mayOnlyBeAccessedByLayers("Indexer", "Engine")
            .whereLayer("Prompt")
            .mayOnlyBeAccessedByLayers("Indexer", "Provider", "Engine")
            .whereLayer("Facts")
            .mayOnlyBeAccessedByLayers("Document", "Routing", "Indexer", "Engine")
            .whereLayer("Config")
            .mayOnlyBeAccessedByLayers("Facts", "Routing", "Prompt", "Provider", "Indexer", "Engine")
            .whereLayer("Support")
            .mayOnlyBeAccessedByLayers(
                    "Config", "Facts", "Prompt", "Routing", "Document", "Provider", "Indexer", "Engine");

    /** The root package of this library. */
    private static final String ROOT_PACKAGE = "net.ladenthin.srcmorph.";

    /**
     * The packages {@code module-info.java} exports to every module: a module-path consumer sees only
     * these ({@code support} goes to the CLI alone).
     */
    private static final List<String> EXPORTED_PACKAGES =
            Arrays.asList("net.ladenthin.srcmorph.config", "net.ladenthin.srcmorph.engine");

    /**
     * The exported packages name no type of a package {@code module-info.java} does not export, in no
     * public signature: a module-path consumer could not use that type. A type the engines need from
     * {@code indexer} or {@code provider} gets a view of its own in {@code engine}.
     */
    @ArchTest
    static final ArchRule exportedApiNamesNoInternalType = classes()
            .that()
            .resideInAnyPackage(EXPORTED_PACKAGES.toArray(new String[0]))
            .should(nameNoInternalTypeInTheirApi());

    /**
     * Checks a class's public signature: its supertypes and every public or protected field,
     * constructor and method (their types with their type arguments, and the exceptions they throw).
     *
     * @return the condition
     */
    private static ArchCondition<JavaClass> nameNoInternalTypeInTheirApi() {
        return new ArchCondition<JavaClass>("name no type of a package module-info.java does not export") {
            @Override
            public void check(final JavaClass javaClass, final ConditionEvents events) {
                if (!isApi(javaClass)) {
                    return;
                }
                final List<JavaType> named = new ArrayList<>();
                javaClass.getSuperclass().ifPresent(named::add);
                named.addAll(javaClass.getInterfaces());
                checkTypes(javaClass.getName(), named, events);
                for (final JavaMember member : javaClass.getMembers()) {
                    if (!isApi(member)) {
                        continue;
                    }
                    final List<JavaType> types = new ArrayList<>();
                    if (member instanceof JavaField) {
                        types.add(((JavaField) member).getType());
                    } else if (member instanceof JavaCodeUnit) {
                        final JavaCodeUnit codeUnit = (JavaCodeUnit) member;
                        types.add(codeUnit.getReturnType());
                        types.addAll(codeUnit.getParameterTypes());
                        types.addAll(codeUnit.getThrowsClause().getTypes());
                    }
                    checkTypes(member.getFullName(), types, events);
                }
            }
        };
    }

    /**
     * Reports every type of a package not exported that the given types involve.
     *
     * @param where the class or member naming them
     * @param types the types it names
     * @param events where a violation goes
     */
    private static void checkTypes(
            final String where, final List<? extends JavaType> types, final ConditionEvents events) {
        for (final JavaType type : types) {
            for (final JavaClass raw : type.getAllInvolvedRawTypes()) {
                final String name = raw.getName();
                if (name.startsWith(ROOT_PACKAGE) && !EXPORTED_PACKAGES.contains(raw.getPackageName())) {
                    events.add(SimpleConditionEvent.violated(where, where + " names " + name));
                }
            }
        }
    }

    /**
     * Whether a class is reachable from outside: public, and every class around it public too.
     *
     * @param javaClass the class
     * @return whether a consumer sees it
     */
    private static boolean isApi(final JavaClass javaClass) {
        if (!javaClass.getModifiers().contains(JavaModifier.PUBLIC)) {
            return false;
        }
        return javaClass.getEnclosingClass().map(CoreArchitectureTest::isApi).orElse(true);
    }

    /**
     * Whether a member is part of the signature: public or protected, and written in the source.
     *
     * @param member the member
     * @return whether a consumer sees it
     */
    private static boolean isApi(final JavaMember member) {
        final boolean visible = member.getModifiers().contains(JavaModifier.PUBLIC)
                || member.getModifiers().contains(JavaModifier.PROTECTED);
        return visible
                && !member.getModifiers().contains(JavaModifier.SYNTHETIC)
                && !member.getModifiers().contains(JavaModifier.BRIDGE);
    }

    /**
     * The generation of one summary body ({@code indexer.generation}) does not know the walkers, the plan or
     * the calibration of {@code indexer} that call it: {@code noPackageCycles} slices at the top level, so it
     * cannot see a cycle between the two.
     */
    @ArchTest
    static final ArchRule generationDoesNotKnowTheWalkers = noClasses()
            .that()
            .resideInAPackage("net.ladenthin.srcmorph.indexer.generation..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("net.ladenthin.srcmorph.indexer");

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
     * {@code provider} package, where {@code LlamaCppJniGenerationProvider} wraps it. Every
     * other layer talks to the model exclusively through the {@code GenerationProvider}
     * interface, so the indexers and document/prompt/config code carry no JNI dependency and
     * stay testable with {@code MockGenerationProvider}.
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
