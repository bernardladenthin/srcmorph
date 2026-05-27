// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;

/**
 * Pluggable AI backend that produces text for an {@link AiGenerationRequest}.
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

    /**
     * Generates text for the given request using the specified temperature, overriding any
     * temperature configured in the provider itself.
     *
     * <p>This method is called during retry attempts to use a higher temperature than the
     * original request, which can break out of EOS-early failure modes that produce empty
     * responses. Implementations that support per-call temperature overrides should override
     * this method; the default implementation ignores {@code temperatureOverride} and
     * delegates to {@link #generate(AiGenerationRequest)}.</p>
     *
     * @param request           the generation request containing prompt key, source file,
     *                          source text, and current header
     * @param temperatureOverride the sampling temperature to use for this call, replacing
     *                          any temperature value held by the provider's own configuration
     * @return the generated text; never {@code null}, but may be blank if the model
     *         produced no tokens
     * @throws IOException if the underlying provider fails
     */
    default String generate(final AiGenerationRequest request, final float temperatureOverride) throws IOException {
        return generate(request);
    }

    @Override
    default void close() throws IOException {
    }
}