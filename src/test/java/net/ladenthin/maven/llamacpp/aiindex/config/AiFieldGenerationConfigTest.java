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
}
