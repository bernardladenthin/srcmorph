// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiFieldGenerationSelectorTest {

    private final AiFieldGenerationSelector selector = new AiFieldGenerationSelector();

    private static AiFieldGenerationConfig config(final String promptKey, final List<String> extensions) {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setPromptKey(promptKey);
        config.setFileExtensions(extensions);
        return config;
    }

    // <editor-fold defaultstate="collapsed" desc="specific match">
    @Test
    public void selectForFileName_extensionMatch_returnsSpecificEntry() {
        final AiFieldGenerationConfig java = config("java", Arrays.asList(".java"));
        final AiFieldGenerationConfig fallback = config("fallback", null);

        final AiFieldGenerationConfig selected = selector.selectForFileName(Arrays.asList(java, fallback), "Foo.java");

        assertThat(selected.getPromptKey(), is(equalTo("java")));
    }

    @Test
    public void selectForFileName_specificWinsOverEarlierFallback() {
        final AiFieldGenerationConfig fallback = config("fallback", null);
        final AiFieldGenerationConfig java = config("java", Arrays.asList(".java"));

        final AiFieldGenerationConfig selected = selector.selectForFileName(Arrays.asList(fallback, java), "Foo.java");

        assertThat(selected.getPromptKey(), is(equalTo("java")));
    }

    @Test
    public void selectForFileName_firstSpecificMatchWins() {
        final AiFieldGenerationConfig first = config("first", Arrays.asList(".java"));
        final AiFieldGenerationConfig second = config("second", Arrays.asList(".java"));

        final AiFieldGenerationConfig selected = selector.selectForFileName(Arrays.asList(first, second), "Foo.java");

        assertThat(selected.getPromptKey(), is(equalTo("first")));
    }

    @Test
    public void selectForFileName_anyOfMultipleExtensionsMatches() {
        final AiFieldGenerationConfig sql = config("sql", Arrays.asList(".sql", ".ddl"));

        final AiFieldGenerationConfig selected =
                selector.selectForFileName(Collections.singletonList(sql), "schema.ddl");

        assertThat(selected.getPromptKey(), is(equalTo("sql")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fallback">
    @Test
    public void selectForFileName_noSpecificMatch_returnsFallback() {
        final AiFieldGenerationConfig java = config("java", Arrays.asList(".java"));
        final AiFieldGenerationConfig fallback = config("fallback", null);

        final AiFieldGenerationConfig selected = selector.selectForFileName(Arrays.asList(java, fallback), "data.json");

        assertThat(selected.getPromptKey(), is(equalTo("fallback")));
    }

    @Test
    public void selectForFileName_emptyExtensionListIsFallback() {
        final AiFieldGenerationConfig empty = config("empty", Collections.<String>emptyList());

        final AiFieldGenerationConfig selected =
                selector.selectForFileName(Collections.singletonList(empty), "anything.xyz");

        assertThat(selected.getPromptKey(), is(equalTo("empty")));
    }

    @Test
    public void selectForFileName_firstFallbackWins() {
        final AiFieldGenerationConfig first = config("first", null);
        final AiFieldGenerationConfig second = config("second", Collections.<String>emptyList());

        final AiFieldGenerationConfig selected = selector.selectForFileName(Arrays.asList(first, second), "data.json");

        assertThat(selected.getPromptKey(), is(equalTo("first")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="no match / null entries">
    @Test
    public void selectForFileName_noMatchNoFallback_returnsNull() {
        final AiFieldGenerationConfig java = config("java", Arrays.asList(".java"));

        final AiFieldGenerationConfig selected =
                selector.selectForFileName(Collections.singletonList(java), "data.json");

        assertThat(selected, is(nullValue()));
    }

    @Test
    public void selectForFileName_nullEntrySkipped() {
        final AiFieldGenerationConfig java = config("java", Arrays.asList(".java"));

        final AiFieldGenerationConfig selected =
                selector.selectForFileName(Arrays.<AiFieldGenerationConfig>asList(null, java), "Foo.java");

        assertThat(selected.getPromptKey(), is(equalTo("java")));
    }
    // </editor-fold>
}
