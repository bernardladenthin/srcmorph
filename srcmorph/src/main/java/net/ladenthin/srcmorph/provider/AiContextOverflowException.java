// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.IOException;

/**
 * The server refused a prompt as longer than the model's context. The caller can shrink the source
 * and ask again; {@code AiFieldGenerationSupport} does, as it does for the same error of a local model.
 */
public final class AiContextOverflowException extends IOException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message what the server answered
     */
    public AiContextOverflowException(final String message) {
        super(message);
    }
}
