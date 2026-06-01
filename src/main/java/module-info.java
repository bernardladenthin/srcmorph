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
 * <p>No non-implicit {@code requires} clauses are declared. The annotations from
 * {@code maven-plugin-annotations}, JSpecify, and Checker Framework qualifiers all use
 * {@code RetentionPolicy.CLASS}, so they are not visible at runtime. The classes imported
 * from {@code maven-plugin-api} and {@code net.ladenthin:llama} are referenced from
 * Mojo source files only; javac in the separate {@code module-info-compile} execution
 * compiles {@code module-info.java} in isolation and therefore does not need their module
 * names. Maven, in turn, loads the plugin classpath-only and ignores the descriptor at
 * runtime.</p>
 *
 * <p>This descriptor compiles at {@code --release 9}; the rest of the source compiles
 * at {@code --release 8}. Java 8 runtimes silently ignore {@code module-info.class} at
 * the JAR root.</p>
 */
module net.ladenthin.maven.llamacpp.aiindex {
    exports net.ladenthin.maven.llamacpp.aiindex;
}
