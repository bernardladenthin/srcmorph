// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class SourceLinesTest {

    // <editor-fold defaultstate="collapsed" desc="normalize">
    @Test
    public void normalize_everyLineEnding_becomesALineFeed() {
        assertThat(SourceLines.normalize("a\r\nb\rc\nd"), is(equalTo("a\nb\nc\nd")));
    }

    @Test
    public void normalize_crCrLf_isTwoLineEndings() {
        // a lone \r, then a \r\n: two line endings, not one and not three
        assertThat(SourceLines.normalize("a\r\r\nb"), is(equalTo("a\n\nb")));
    }

    @Test
    public void normalize_withoutACarriageReturn_returnsTheSameText() {
        final String text = "a\nb\n";

        assertThat(SourceLines.normalize(text), is(sameInstance(text)));
    }

    @Test
    public void normalize_aCarriageReturnAtTheStart_isSeen() {
        // indexOf('\r') == 0 must not count as "none"
        assertThat(SourceLines.normalize("\rx"), is(equalTo("\nx")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="count">
    @Test
    public void count_emptyText_isZero() {
        assertThat(SourceLines.count(""), is(0));
    }

    @Test
    public void count_aFinalLineEnding_addsNoLine() {
        assertThat(SourceLines.count("a\nb\n"), is(2));
        assertThat(SourceLines.count("a\nb"), is(2));
        assertThat(SourceLines.count("a"), is(1));
        assertThat(SourceLines.count("\n"), is(1));
    }

    @Test
    public void count_crLfAndCr_countLikeLf() {
        assertThat(SourceLines.count("a\r\nb\r\n"), is(2));
        assertThat(SourceLines.count("a\rb\rc"), is(3));
        assertThat(SourceLines.count("a\r\n\r\nb"), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="split">
    @Test
    public void split_atEveryLineEnding_keepingTheTrailingEmptyLine() {
        assertThat(SourceLines.split("a\r\nb\rc\nd\n"), is(equalTo(new String[] {"a", "b", "c", "d", ""})));
    }

    @Test
    public void split_emptyText_isOneEmptyLine() {
        assertThat(SourceLines.split(""), is(equalTo(new String[] {""})));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="normalize(byte[])">
    @Test
    public void normalizeBytes_crLfAndLoneCr_becomeLf() {
        final byte[] bytes = {'a', '\r', '\n', 'b', '\r', 'c', '\n', '\r'};

        assertThat(SourceLines.normalize(bytes), is(equalTo(new byte[] {'a', '\n', 'b', '\n', 'c', '\n', '\n'})));
    }

    @Test
    public void normalizeBytes_aCrAtTheStart_becomesLf() {
        assertThat(SourceLines.normalize(new byte[] {'\r', '\n', 'x'}), is(equalTo(new byte[] {'\n', 'x'})));
        assertThat(SourceLines.normalize(new byte[] {'\r'}), is(equalTo(new byte[] {'\n'})));
    }

    @Test
    public void normalizeBytes_withoutCr_isTheSameArray() {
        final byte[] bytes = {'a', '\n', 'b'};

        assertThat(SourceLines.normalize(bytes), is(sameInstance(bytes)));
    }

    @Test
    public void normalizeBytes_ofValidUtf8_areTheBytesOfTheNormalizedText() {
        final String text = "Grüße\r\n€\r";
        final byte[] utf8 = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThat(
                SourceLines.normalize(utf8),
                is(equalTo(SourceLines.normalize(text).getBytes(java.nio.charset.StandardCharsets.UTF_8))));
    }

    @Test
    public void normalizeBytes_keepAnEditOfATextThatIsNotUtf8() {
        // "aä" and "aö" in ISO-8859-1: both decode to "a�" as UTF-8, their bytes differ
        final byte[] before = {'a', (byte) 0xE4};
        final byte[] after = {'a', (byte) 0xF6};
        final ChecksumSupport checksums = new ChecksumSupport();

        assertThat(
                new String(before, java.nio.charset.StandardCharsets.UTF_8),
                is(equalTo(new String(after, java.nio.charset.StandardCharsets.UTF_8))));
        assertThat(
                checksums
                        .calculateCrc32Hex(SourceLines.normalize(before))
                        .equals(checksums.calculateCrc32Hex(SourceLines.normalize(after))),
                is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="decode">
    private static byte[] bytes(final int... values) {
        final byte[] out = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            out[i] = (byte) values[i];
        }
        return out;
    }

    @Test
    public void decode_utf8WithoutAMark_isTheText() {
        assertThat(
                SourceLines.decode("class Ä {}".getBytes(java.nio.charset.StandardCharsets.UTF_8)), is("class Ä {}"));
        assertThat(SourceLines.decode(new byte[0]), is(""));
    }

    @Test
    public void decode_utf8ByteOrderMark_isDropped() {
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 0xBF, 'a', 'b')), is("ab"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 0xBF)), is(""));
    }

    @Test
    public void decode_utf16ByteOrderMarks_decodeAsUtf16() {
        assertThat(SourceLines.decode(bytes(0xFF, 0xFE, 'a', 0, 0xC4, 0, '\n', 0)), is("aÄ\n"));
        assertThat(SourceLines.decode(bytes(0xFE, 0xFF, 0, 'a', 0, 0xC4)), is("aÄ"));
        assertThat(SourceLines.decode(bytes(0xFF, 0xFE)), is(""));
        assertThat(SourceLines.decode(bytes(0xFE, 0xFF)), is(""));
    }

    @Test
    public void decode_partOfAMark_isUtf8Text() {
        // two of the three bytes of the UTF-8 mark: invalid UTF-8, read as replacement characters
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB)), is("�"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 'a')), is("�a"));
        assertThat(SourceLines.decode(bytes(0xFF)), is("�"));
        assertThat(SourceLines.decode(bytes(0xFE, 'a')), is("�a"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBC, 0xBF)), is("＿"));
    }
    // </editor-fold>
}
