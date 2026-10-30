// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0

package net.ladenthin.srcmorph.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks code that duplicates a class of a library this project depends on, written here only because
 * the release of the library that carries it is not out yet. Once the dependency is raised to that
 * release, the marked code is replaced by {@link #value()} and the marker goes with it.
 *
 * <p>{@code ReplaceWithLibraryTest} fails as soon as {@link #value()} is on the classpath, so the
 * duplicate cannot outlive its reason, and it fails when no marked code is left, so this annotation
 * and the test are deleted with the last one. The open item is in {@code TODO.md}.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface ReplaceWithLibrary {

    /**
     * The library class that replaces the marked code.
     *
     * @return its fully qualified name
     */
    String value();

    /**
     * The release of the library expected to carry {@link #value()}, and where it comes from.
     *
     * @return e.g. the artifact, the version and the pull request
     */
    String since();
}
