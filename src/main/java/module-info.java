// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * JPMS module descriptor for the llamacpp-ai-index Maven plugin.
 *
 * <p>The module exports the single public package {@code net.ladenthin.maven.llamacpp.aiindex}
 * that holds the configuration POJOs and Mojo entry points. The auto-generated
 * {@code net.ladenthin.llamacpp_ai_index_maven_plugin.HelpMojo} package is deliberately
 * <em>not</em> exported: Maven loads the plugin via its own classpath classloader and
 * never consults the module descriptor for Mojo discovery, so the {@code HelpMojo} class
 * remains reachable as an ordinary classpath type.</p>
 *
 * <p>JSpecify {@code @NullMarked} is declared at the module level here so that no source
 * file compiled at {@code --release 8} references the JSpecify annotation type directly.
 * Otherwise javac would emit an unsuppressible {@code unknown enum constant
 * ElementType.MODULE} classfile-read warning for each source compiled at release 8 that
 * resolves {@code @NullMarked} ({@code @NullMarked} carries
 * {@code @Target({MODULE, PACKAGE, TYPE})} and Java 8 does not know about
 * {@code ElementType.MODULE}). Confining the reference to {@code module-info.java} —
 * which compiles at {@code --release 9} — keeps that warning out of the build entirely.</p>
 *
 * <p>{@code requires static org.jspecify} is needed only at compile time of this
 * descriptor; JSpecify annotations carry {@code RetentionPolicy.CLASS} so module-path
 * consumers never need jspecify on their runtime path. Annotations from
 * {@code maven-plugin-annotations} and Checker Framework qualifiers are likewise
 * compile-time only. The classes imported from {@code maven-plugin-api} and
 * {@code net.ladenthin:llama} are referenced from Mojo source files only; javac in the
 * separate {@code module-info-compile} execution compiles {@code module-info.java} in
 * isolation and therefore does not need their module names. Maven, in turn, loads the
 * plugin classpath-only and ignores the descriptor at runtime.</p>
 *
 * <p>This descriptor compiles at {@code --release 9}; the rest of the source compiles
 * at {@code --release 8}. Java 8 runtimes silently ignore {@code module-info.class} at
 * the JAR root.</p>
 */
@org.jspecify.annotations.NullMarked
module net.ladenthin.maven.llamacpp.aiindex {
    requires static org.jspecify;

    // Lombok is `provided` scope: only used at compile time to generate equals/hashCode/toString.
    // `requires static` means the runtime does not need the lombok jar on the module path —
    // the @lombok.Generated annotation carried on generated members has CLASS retention.
    requires static lombok;

    exports net.ladenthin.maven.llamacpp.aiindex.config;
    exports net.ladenthin.maven.llamacpp.aiindex.document;
    exports net.ladenthin.maven.llamacpp.aiindex.indexer;
    exports net.ladenthin.maven.llamacpp.aiindex.mojo;
    exports net.ladenthin.maven.llamacpp.aiindex.prompt;
    exports net.ladenthin.maven.llamacpp.aiindex.provider;
    exports net.ladenthin.maven.llamacpp.aiindex.support;
}
