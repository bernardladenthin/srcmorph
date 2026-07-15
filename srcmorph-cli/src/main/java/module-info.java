// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * JPMS module descriptor for the srcmorph CLI.
 *
 * <p>The module exports the two packages that make up the CLI: {@code cli} (the {@code Main}
 * entry point) and {@code cli.configuration} ({@code CConfiguration} + {@code CCommand}, the
 * JSON/YAML-bindable wrapper around the core library's own {@code SrcMorphConfiguration}). It
 * requires the {@code net.ladenthin.srcmorph} core module (for the shared configuration object and
 * the {@code engine} orchestration classes {@code Main} drives) and Jackson's {@code databind}
 * module (for the JSON/YAML binding); the {@code configuration} package is {@code opens} to
 * {@code com.fasterxml.jackson.databind} because Jackson's field-based binding style — the same
 * public-mutable-field JavaBean convention {@code SrcMorphConfiguration} and BitcoinAddressFinder's
 * own {@code CConfiguration} use — needs reflective access under the Java Platform Module System's
 * strong encapsulation, which {@code exports} alone does not grant for field/method injection
 * (only compile-time type visibility).</p>
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
 * consumers never need jspecify on their runtime path. Checker Framework qualifiers are
 * likewise compile-time only. The fat jar produced by {@code maven-assembly-plugin} is a
 * classpath-only artifact and flattens multiple {@code module-info.class} entries harmlessly,
 * same as any other jar-with-dependencies build.</p>
 *
 * <p><strong>This descriptor is NOT purely decorative</strong> — unlike the core/plugin modules'
 * own {@code module-info.java} (which only need to satisfy the isolated, release-9
 * {@code module-info-compile} execution), Maven Surefire's default module-path detection
 * (triggered by the presence of {@code module-info.class} in {@code target/classes} once the
 * {@code compile} phase has run, i.e. before {@code test}) resolves this module's runtime
 * dependency graph from the {@code requires} clauses below. Both real JPMS modules Jackson's
 * side pulls in — {@code com.fasterxml.jackson.databind} and
 * {@code com.fasterxml.jackson.dataformat.yaml} — must be listed explicitly or a real
 * {@link java.lang.IllegalAccessError} is thrown at test time (not merely a compile-time
 * warning): {@code YAMLMapper} lives in a distinct named module from {@code ObjectMapper}, and
 * JPMS strong encapsulation blocks any access this module does not declare a {@code requires}
 * read-edge for, even though both jars sit on the same dependency list.</p>
 *
 * <p>This descriptor compiles at {@code --release 9}; the rest of the source compiles
 * at {@code --release 8}. Java 8 runtimes silently ignore {@code module-info.class} at
 * the JAR root.</p>
 */
@org.jspecify.annotations.NullMarked
module net.ladenthin.srcmorph.cli {
    requires static org.jspecify;

    // Lombok is `provided` scope: only used at compile time to generate equals/hashCode/toString.
    // `requires static` means the runtime does not need the lombok jar on the module path —
    // the @lombok.Generated annotation carried on generated members has CLASS retention.
    requires static lombok;
    requires net.ladenthin.srcmorph;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    opens net.ladenthin.srcmorph.cli.configuration to
            com.fasterxml.jackson.databind;

    exports net.ladenthin.srcmorph.cli;
    exports net.ladenthin.srcmorph.cli.configuration;
}
