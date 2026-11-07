// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import java.util.OptionalLong;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.support.ConvertToRecord;
import org.jspecify.annotations.Nullable;

/**
 * What a GGUF file says about itself, read from its header without loading the model.
 *
 * <p>Deliberately framework-free: {@link GgufModelInspector} produces it inside the {@code provider}
 * package (the only package the {@code jniConfinedToProvider} architecture rule lets touch the
 * llama binding), and callers in {@code engine} consume plain Java out of it.</p>
 *
 * <p>Record-shaped value type marked {@link ConvertToRecord} for the future Java&nbsp;17+ migration.</p>
 */
@ConvertToRecord
@ToString
@EqualsAndHashCode
public final class GgufModelInfo {

    private final @Nullable String modelName;
    private final @Nullable Long contextLength;
    private final @Nullable String failure;

    private GgufModelInfo(
            final @Nullable String modelName, final @Nullable Long contextLength, final @Nullable String failure) {
        this.modelName = modelName;
        this.contextLength = contextLength;
        this.failure = failure;
    }

    /**
     * What a file that parsed as a GGUF declares.
     *
     * @param modelName     the declared model name, {@code null} when it declares none (an empty one is
     *                      none)
     * @param contextLength the declared context length in tokens, {@code null} when it declares none
     * @return the readable result
     */
    public static GgufModelInfo parsed(final @Nullable String modelName, final @Nullable Long contextLength) {
        return new GgufModelInfo(modelName == null || modelName.isEmpty() ? null : modelName, contextLength, null);
    }

    /**
     * A file that did not parse as a GGUF: the reason, and no claims about the model.
     *
     * @param failure why the file could not be read
     * @return the unreadable result
     */
    public static GgufModelInfo unreadable(final String failure) {
        return new GgufModelInfo(null, null, failure);
    }

    /**
     * Returns whether the file parsed as a GGUF.
     *
     * @return {@code true} when the header was read successfully
     */
    public boolean readable() {
        return failure == null;
    }

    /**
     * Returns the model name the file declares.
     *
     * @return the model name, {@code null} when it declares none or is unreadable
     */
    public @Nullable String modelName() {
        return modelName;
    }

    /**
     * Returns the context length in tokens the file declares.
     *
     * @return the declared context length, empty when it declares none or is unreadable
     */
    public OptionalLong contextLength() {
        return contextLength == null ? OptionalLong.empty() : OptionalLong.of(contextLength);
    }

    /**
     * Returns why the file could not be read.
     *
     * @return the failure description, {@code null} when the file was readable
     */
    public @Nullable String failure() {
        return failure;
    }
}
