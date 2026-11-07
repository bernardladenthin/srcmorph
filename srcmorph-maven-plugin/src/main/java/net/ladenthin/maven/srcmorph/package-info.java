// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * The srcmorph Maven plugin: its goals run the core's phases (generate,
 * aggregate-packages, aggregate-project, calibrate, check) inside a build, configured
 * by the same file as the CLI.
 *
 * <p>JSpecify {@code @NullMarked} is declared at module level in
 * {@code module-info.java} and applies transitively to every package:
 * every parameter, return value, and field is non-null unless explicitly
 * annotated {@code @Nullable}. NullAway and the Checker Framework Nullness
 * Checker both enforce this at compile time via the configured Error Prone
 * compiler plugin (see {@code pom.xml}). Maven plugin parameter fields
 * (annotated {@code @Parameter}/{@code @Component}) are excluded from
 * initializer checks since they are populated by the plugin framework via
 * reflection after construction. The annotation lives only in
 * {@code module-info.java} so that no source compiled at {@code --release 8}
 * references the JSpecify {@code @NullMarked} type, which avoids the
 * unsuppressible {@code unknown enum constant ElementType.MODULE}
 * classfile-read warning that javac emits otherwise.
 */
package net.ladenthin.maven.srcmorph;
