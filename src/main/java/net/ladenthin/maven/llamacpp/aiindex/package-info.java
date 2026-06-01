// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

/**
 * Maven plugin that generates {@code .ai.md} index files alongside Java source
 * trees via llama.cpp.
 *
 * <p>The package is JSpecify {@code @NullMarked}: every parameter, return
 * value, and field is non-null unless explicitly annotated {@code @Nullable}.
 * NullAway enforces this at compile time via the configured Error Prone
 * compiler plugin (see {@code pom.xml}). Maven plugin parameter fields
 * (annotated {@code @Parameter}/{@code @Component}) are excluded from
 * initializer checks since they are populated by the plugin framework via
 * reflection after construction.
 */
@NullMarked
package net.ladenthin.maven.llamacpp.aiindex;

import org.jspecify.annotations.NullMarked;
