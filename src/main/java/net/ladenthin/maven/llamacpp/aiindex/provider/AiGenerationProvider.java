// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.provider;

import java.io.IOException;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;

/**
 * Pluggable AI backend that produces text for an {@link net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest}.
 * Implementations may run locally (llama.cpp) or be mock providers for tests.
 */
public interface AiGenerationProvider extends AutoCloseable {

    /**
     * Generates text for the given request using the provider's default sampling parameters.
     *
     * @param request the generation request containing prompt key, source file, source text, and current header
     * @return the generated text; never {@code null}, but may be blank if the model produced no tokens
     * @throws IOException if the underlying provider fails
     */
    String generate(AiGenerationRequest request) throws IOException;

    @Override
    default void close() throws IOException {}
}
