// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

/**
 * Checked exception thrown by the {@code engine} package when a run fails with a message for the user,
 * not a stack trace. Mostly misconfiguration, found before any model is loaded (an invalid rule set, an
 * unmatched file with no fallback, an oversized file with {@code onOversize=fail}, a bad prompt, fact or
 * model definition, a missing model file); a prompt or facts file that cannot be read is reported here
 * too. It also ends a run that went wrong on the way: files left unwritten after an incomplete answer or
 * a failed request, a model that fails to load or generate during calibration, a calibration report
 * that cannot be written, a server whose models cannot be listed.
 *
 * <p>Walking the source tree and reading or writing the index files fail with a plain
 * {@link java.io.IOException} instead. The plugin's mojos rewrap both into a
 * {@code MojoExecutionException}; the CLI's {@code Main} into an {@link IllegalStateException}.</p>
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
