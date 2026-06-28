// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiFieldGenerationConfigTest {

    @Test
    public void fileExtensions_defaultsToNull() {
        assertThat(new AiFieldGenerationConfig().getFileExtensions(), is(nullValue()));
    }

    @Test
    public void setFileExtensions_null_getterReturnsNull() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setFileExtensions(null);
        assertThat(config.getFileExtensions(), is(nullValue()));
    }

    @Test
    public void setFileExtensions_storesValuesInOrder() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setFileExtensions(Arrays.asList(".java", ".sql"));
        assertThat(config.getFileExtensions(), is(equalTo(Arrays.asList(".java", ".sql"))));
    }

    @Test
    public void setFileExtensions_defensivelyCopiesSource() {
        // arrange
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        final List<String> source = new ArrayList<>();
        source.add(".java");
        config.setFileExtensions(source);

        // act: mutating the source after the set must not affect the stored value
        source.add(".sql");

        // assert
        assertThat(config.getFileExtensions(), is(equalTo(Arrays.asList(".java"))));
    }

    @Test
    public void getFileExtensions_returnedListIsUnmodifiable() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setFileExtensions(Arrays.asList(".java"));
        assertThrows(
                UnsupportedOperationException.class,
                () -> config.getFileExtensions().add(".sql"));
    }

    @Test
    public void minFileSizeBytes_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.getMinFileSizeBytes(), is(equalTo(0L)));
        config.setMinFileSizeBytes(16384L);
        assertThat(config.getMinFileSizeBytes(), is(equalTo(16384L)));
    }

    @Test
    public void maxFileSizeBytes_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.getMaxFileSizeBytes(), is(equalTo(0L)));
        config.setMaxFileSizeBytes(49152L);
        assertThat(config.getMaxFileSizeBytes(), is(equalTo(49152L)));
    }

    @Test
    public void minLines_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.getMinLines(), is(equalTo(0)));
        config.setMinLines(100);
        assertThat(config.getMinLines(), is(equalTo(100)));
    }

    @Test
    public void maxLines_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.getMaxLines(), is(equalTo(0)));
        config.setMaxLines(500);
        assertThat(config.getMaxLines(), is(equalTo(500)));
    }

    @Test
    public void priority_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.getPriority(), is(equalTo(0)));
        config.setPriority(10);
        assertThat(config.getPriority(), is(equalTo(10)));
    }

    @Test
    public void fallback_defaultsFalseAndTogglesTrue() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.isFallback(), is(false));
        config.setFallback(true);
        assertThat(config.isFallback(), is(true));
    }

    @Test
    public void skip_defaultsFalseAndTogglesTrue() {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        assertThat(config.isSkip(), is(false));
        config.setSkip(true);
        assertThat(config.isSkip(), is(true));
    }
}
