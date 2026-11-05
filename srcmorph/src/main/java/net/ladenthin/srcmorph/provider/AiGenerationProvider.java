// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.io.IOException;
import java.util.OptionalInt;
import net.ladenthin.srcmorph.document.AiGenerationRequest;

/**
 * Pluggable AI backend that produces text for an {@link net.ladenthin.srcmorph.document.AiGenerationRequest}.
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
     * Generates text and returns it together with the model's measured timing, for the
     * {@code srcmorph:calibrate} goal. The default implementation delegates to {@link #generate} and
     * reports no timings (rates {@code 0}); providers that expose timings override this.
     *
     * @param request the generation request
     * @return the generated text plus timing (rates {@code 0} when unavailable)
     * @throws IOException if the underlying provider fails
     */
    default AiGenerationTimings generateWithTimings(final AiGenerationRequest request) throws IOException {
        return new AiGenerationTimings(generate(request), 0, 0, 0.0d, 0, 0.0d);
    }

    /**
     * Returns the tokens and the finish reason of this provider's last {@link #generate} call, also when it
     * threw (an incomplete answer still cost its tokens); {@link AiGenerationUsage#NONE} when the provider
     * does not know them. A provider serves one thread at a time, so "last" is the caller's own call.
     *
     * @return one call's usage, or {@link AiGenerationUsage#NONE}
     */
    default AiGenerationUsage lastUsage() {
        return AiGenerationUsage.NONE;
    }

    /**
     * Returns how many tokens the prompt of {@code request} takes in the model, as its chat template renders
     * it, counted by the model's own tokenizer without generating; empty when the provider cannot count
     * them (the default; a remote API answers the count only after the call). A caller sizes its windows
     * by the count instead of the {@code charsPerToken} estimate when there is one.
     *
     * @param request the request whose prompt is counted
     * @return the prompt's tokens, or empty
     * @throws IOException if the underlying provider fails
     */
    default OptionalInt promptTokens(final AiGenerationRequest request) throws IOException {
        return OptionalInt.empty();
    }

    /**
     * Returns the nanoseconds this provider spent loading its model since the last call of this method,
     * and starts counting again: a local model loads on its first use, so the caller tells that time from
     * the first file's. {@code 0} for a provider that loads nothing (the default: a remote model).
     *
     * @return the load time since the last call, {@code 0} when nothing was loaded
     */
    default long takeLoadNanos() {
        return 0L;
    }

    @Override
    default void close() throws IOException {}
}
