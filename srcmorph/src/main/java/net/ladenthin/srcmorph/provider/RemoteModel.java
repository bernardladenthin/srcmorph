// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.support.ConvertToRecord;
import org.jspecify.annotations.Nullable;

/**
 * One model an OpenAI-compatible server lists under {@code GET <baseUrl>/models}: its id (what
 * {@code remoteModel} names), its context length and, where the server says, its price.
 */
@ConvertToRecord
@ToString
@EqualsAndHashCode
public final class RemoteModel {

    /** The {@link #contextLength()} of a model whose server names none. */
    public static final int UNKNOWN_CONTEXT_LENGTH = 0;

    private final String id;
    private final int contextLength;
    private final @Nullable BigDecimal inputUsdPerMillion;
    private final @Nullable BigDecimal outputUsdPerMillion;

    /**
     * Creates a new {@link RemoteModel}.
     *
     * @param id                  the model id
     * @param contextLength       the token limit the server names, {@link #UNKNOWN_CONTEXT_LENGTH} when none
     * @param inputUsdPerMillion  USD per million input tokens, {@code null} when the server names no price
     * @param outputUsdPerMillion USD per million output tokens, {@code null} when the server names no price
     */
    public RemoteModel(
            final String id,
            final int contextLength,
            final @Nullable BigDecimal inputUsdPerMillion,
            final @Nullable BigDecimal outputUsdPerMillion) {
        this.id = Objects.requireNonNull(id, "id");
        this.contextLength = contextLength;
        this.inputUsdPerMillion = inputUsdPerMillion;
        this.outputUsdPerMillion = outputUsdPerMillion;
    }

    /**
     * Returns the model id.
     *
     * @return the id
     */
    public String id() {
        return id;
    }

    /**
     * Returns the token limit the server names for the model: the context length (OpenRouter,
     * llama-server) or the input limit (the GitHub Copilot API Gateway).
     *
     * @return the limit, {@link #UNKNOWN_CONTEXT_LENGTH} when the server names none
     */
    public int contextLength() {
        return contextLength;
    }

    /**
     * Returns the price of a million input tokens.
     *
     * @return USD, {@code null} when the server names no price
     */
    public @Nullable BigDecimal inputUsdPerMillion() {
        return inputUsdPerMillion;
    }

    /**
     * Returns the price of a million output tokens.
     *
     * @return USD, {@code null} when the server names no price
     */
    public @Nullable BigDecimal outputUsdPerMillion() {
        return outputUsdPerMillion;
    }
}
