// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

/**
 * Checked exception thrown by the {@code engine} package for a run that fails because of misconfiguration
 * (an invalid rule set, an unmatched file with no fallback, an oversized file with {@code onOversize=fail},
 * a bad prompt/model definition, …) rather than an I/O failure.
 *
 * <p>This is the framework-free replacement for {@code org.apache.maven.plugin.MojoExecutionException}
 * inside core code: the {@code llamacpp-ai-index-maven-plugin} module's mojos catch this (alongside a
 * plain {@link java.io.IOException} for genuine I/O failures) and rewrap it into a
 * {@code MojoExecutionException} so Maven still reports the same user-facing error it always has.</p>
 */
public class SrcMorphException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@link SrcMorphException} with the given message.
     *
     * @param message the detail message
     */
    public SrcMorphException(final String message) {
        super(message);
    }

    /**
     * Creates a new {@link SrcMorphException} with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public SrcMorphException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
