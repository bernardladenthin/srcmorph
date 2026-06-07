// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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

    @Test
    public void isBlankRecognisesEmptyAndWhitespace() {
        assertThat(helper.isBlank(""), is(true));
        assertThat(helper.isBlank("   "), is(true));
        assertThat(helper.isBlank("x"), is(false));
        assertThat(helper.isBlank("  x  "), is(false));
    }

    @Test
    public void formattedDelegatesToStringFormat() {
        assertThat(helper.formatted("a%sb%d", "X", 7), is("aXb7"));
    }

    @Test
    public void readStringReturnsFileContent() throws IOException {
        Path file = folder.resolve("r.txt");
        Files.write(file, "héllo".getBytes(StandardCharsets.UTF_8));
        // Asserting the exact content kills the empty-string return mutant on readString.
        assertThat(helper.readString(file), is("héllo"));
    }

    @Test
    public void writeStringDefaultsToUtf8WhenCharsetNull() throws IOException {
        Path file = folder.resolve("w.txt");
        // null charset must fall back to UTF-8 (kills the negate mutant on the charset guard,
        // which would pass null through to getBytes and NPE).
        helper.writeString(file, "héllo", null);
        assertThat(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), is("héllo"));
    }

    @Test
    public void writeStringHonoursExplicitCharset() throws IOException {
        Path file = folder.resolve("w2.txt");
        helper.writeString(file, "hi", StandardCharsets.UTF_8);
        assertThat(new String(Files.readAllBytes(file), StandardCharsets.UTF_8), is("hi"));
    }

    @Test
    public void toListCollectsStreamElements() {
        assertThat(helper.toList(Stream.of("a", "b", "c")), contains("a", "b", "c"));
    }

    @Test
    public void listOfWrapsElements() {
        List<String> list = helper.listOf("a", "b");
        // Asserting content kills the empty-collection return mutant.
        assertThat(list, contains("a", "b"));
    }

    @Test
    public void hashMapCapacityForAppliesLoadFactorFormula() {
        // (int)(n / 0.75f) + 1 : 0 -> 1, 3 -> 5. Pins arithmetic + the +1 (Math mutators) and
        // the non-zero return (primitive-returns mutant -> 0).
        assertThat(helper.hashMapCapacityFor(0), is(1));
        assertThat(helper.hashMapCapacityFor(3), is(5));
    }
}
