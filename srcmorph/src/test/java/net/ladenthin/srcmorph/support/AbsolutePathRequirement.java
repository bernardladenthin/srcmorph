// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Tag;

/**
 * Marks a test of the requirement that <b>an absolute path is always possible</b>, for every path
 * of a configuration that may also be given relative to a directory:
 *
 * <ul>
 *   <li>a model's {@code modelPath} and the fallback {@code llamaModelPath}, relative to
 *       {@code modelDirectory};</li>
 *   <li>a prompt's {@code templateFile}, relative to {@code promptDirectory};</li>
 *   <li>those two directories themselves.</li>
 * </ul>
 *
 * <p>A path with a root of its own (absolute, or on Windows root- or drive-relative) is used as it
 * is, whether the directory is unset, relative, absolute or does not exist, and an absolute directory
 * makes every relative path below it absolute. One rule implements it for both kinds of path,
 * {@link AiPathSupport#resolveAgainst}, so a test carrying this marker guards both: a change that
 * breaks absolute paths for models or for prompts fails a test that says so. The marker is a JUnit
 * {@link Tag} ({@value #TAG}), so {@code mvn test -Dgroups=absolute-path} runs exactly these tests.
 * Modules that cannot see this test class tag their tests with the same string.</p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Tag("absolute-path")
public @interface AbsolutePathRequirement {

    /** The JUnit tag of the requirement, for {@code -Dgroups} and for modules without this class. */
    String TAG = "absolute-path";
}
