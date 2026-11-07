// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

public class GgufModelInfoTest {

    // <editor-fold defaultstate="collapsed" desc="parsed">
    @Test
    public void parsed_aNameAndAContextLength_areWhatTheAccessorsReturn() {
        // act
        final GgufModelInfo info = GgufModelInfo.parsed("Qwen3-0.6B", 32768L);

        // assert
        assertThat(info.readable(), is(true));
        assertThat(info.modelName(), is("Qwen3-0.6B"));
        assertThat(info.contextLength(), is(OptionalLong.of(32768L)));
        assertThat(info.failure(), is(nullValue()));
    }

    /** A readable GGUF that declares no context length and no name is still readable. */
    @Test
    public void parsed_nothingDeclared_isReadableWithoutANameOrAContextLength() {
        // act
        final GgufModelInfo info = GgufModelInfo.parsed(null, null);

        // assert
        assertThat(info.readable(), is(true));
        assertThat(info.modelName(), is(nullValue()));
        assertThat(info.contextLength(), is(OptionalLong.empty()));
    }

    /** An empty declared name is no name: a caller falls back to the file's own name. */
    @Test
    public void parsed_anEmptyName_isNoName() {
        // act
        final GgufModelInfo info = GgufModelInfo.parsed("", 4096L);

        // assert
        assertThat(info.modelName(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="unreadable">
    /** The unreadable shape: a reason, and no claims about the model. */
    @Test
    public void unreadable_aReason_carriesItAndNoClaimsAboutTheModel() {
        // act
        final GgufModelInfo info = GgufModelInfo.unreadable("bad magic");

        // assert
        assertThat(info.readable(), is(false));
        assertThat(info.failure(), is("bad magic"));
        assertThat(info.modelName(), is(nullValue()));
        assertThat(info.contextLength(), is(OptionalLong.empty()));
    }
    // </editor-fold>
}
