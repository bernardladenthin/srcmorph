// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class Java8CompatibilityHelperTest {

    @TempDir
    public Path folder;

    private final Java8CompatibilityHelper helper = new Java8CompatibilityHelper();

    // <editor-fold defaultstate="collapsed" desc="isBlank">
    @Test
    public void isBlank_emptyWhitespaceOrText_recognisesEmptyAndWhitespace() {
        // act, assert
        assertThat(helper.isBlank(""), is(true));
        assertThat(helper.isBlank("   "), is(true));
        assertThat(helper.isBlank("x"), is(false));
        assertThat(helper.isBlank("  x  "), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="formatted">
    @Test
    public void formatted_formatAndArguments_delegatesToStringFormat() {
        // act, assert
        assertThat(helper.formatted("a%sb%d", "X", 7), is("aXb7"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="readString">
    @Test
    public void readString_existingFile_returnsFileContent() throws IOException {
        // arrange
        Path file = folder.resolve("r.txt");
        Files.write(file, "héllo".getBytes(StandardCharsets.UTF_8));

        // act, assert
        // Asserting the exact content kills the empty-string return mutant on readString.
        assertThat(helper.readString(file), is("héllo"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="writeString">
    @Test
    public void writeString_nullCharset_defaultsToUtf8() throws IOException {
        // arrange
        Path file = folder.resolve("w.txt");

        // act
        // null charset must fall back to UTF-8 (kills the negate mutant on the charset guard,
        // which would pass null through to getBytes and NPE).
        helper.writeString(file, "héllo", null);

        // assert
        assertThat(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), is("héllo"));
    }

    @Test
    public void writeString_explicitCharset_honoursExplicitCharset() throws IOException {
        // arrange
        Path file = folder.resolve("w2.txt");

        // act
        helper.writeString(file, "hi", StandardCharsets.UTF_8);

        // assert
        assertThat(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), is("hi"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toList">
    @Test
    public void toList_stream_collectsStreamElements() {
        // act, assert
        assertThat(helper.toList(Stream.of("a", "b", "c")), contains("a", "b", "c"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listOf">
    @Test
    public void listOf_elements_wrapsElements() {
        // act
        List<String> list = helper.listOf("a", "b");

        // assert
        // Asserting content kills the empty-collection return mutant.
        assertThat(list, contains("a", "b"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hashMapCapacityFor">
    @Test
    public void hashMapCapacityFor_expectedSizes_appliesLoadFactorFormula() {
        // act, assert
        // (int)(n / 0.75f) + 1 : 0 -> 1, 3 -> 5. Pins arithmetic + the +1 (Math mutators) and
        // the non-zero return (primitive-returns mutant -> 0).
        assertThat(helper.hashMapCapacityFor(0), is(1));
        assertThat(helper.hashMapCapacityFor(3), is(5));
    }
    // </editor-fold>
}
