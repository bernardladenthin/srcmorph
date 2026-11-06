// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class SourceLinesTest {

    // <editor-fold defaultstate="collapsed" desc="normalize">
    @Test
    public void normalize_everyLineEnding_becomesALineFeed() {
        // act, assert
        assertThat(SourceLines.normalize("a\r\nb\rc\nd"), is(equalTo("a\nb\nc\nd")));
    }

    @Test
    public void normalize_crCrLf_isTwoLineEndings() {
        // act, assert
        // a lone \r, then a \r\n: two line endings, not one and not three
        assertThat(SourceLines.normalize("a\r\r\nb"), is(equalTo("a\n\nb")));
    }

    @Test
    public void normalize_withoutACarriageReturn_returnsTheSameText() {
        // arrange
        final String text = "a\nb\n";

        // act, assert
        assertThat(SourceLines.normalize(text), is(sameInstance(text)));
    }

    @Test
    public void normalize_aCarriageReturnAtTheStart_isSeen() {
        // act, assert
        // indexOf('\r') == 0 must not count as "none"
        assertThat(SourceLines.normalize("\rx"), is(equalTo("\nx")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="count">
    @Test
    public void count_emptyText_isZero() {
        // act, assert
        assertThat(SourceLines.count(""), is(0));
    }

    @Test
    public void count_aFinalLineEnding_addsNoLine() {
        // act, assert
        assertThat(SourceLines.count("a\nb\n"), is(2));
        assertThat(SourceLines.count("a\nb"), is(2));
        assertThat(SourceLines.count("a"), is(1));
        assertThat(SourceLines.count("\n"), is(1));
    }

    @Test
    public void count_crLfAndCr_countLikeLf() {
        // act, assert
        assertThat(SourceLines.count("a\r\nb\r\n"), is(2));
        assertThat(SourceLines.count("a\rb\rc"), is(3));
        assertThat(SourceLines.count("a\r\n\r\nb"), is(3));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="split">
    @Test
    public void split_atEveryLineEnding_keepingTheTrailingEmptyLine() {
        // act, assert
        assertThat(SourceLines.split("a\r\nb\rc\nd\n"), is(equalTo(new String[] {"a", "b", "c", "d", ""})));
    }

    @Test
    public void split_emptyText_isOneEmptyLine() {
        // act, assert
        assertThat(SourceLines.split(""), is(equalTo(new String[] {""})));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="normalize(byte[])">
    @Test
    public void normalizeBytes_crLfAndLoneCr_becomeLf() {
        // arrange
        final byte[] bytes = {'a', '\r', '\n', 'b', '\r', 'c', '\n', '\r'};

        // act, assert
        assertThat(SourceLines.normalize(bytes), is(equalTo(new byte[] {'a', '\n', 'b', '\n', 'c', '\n', '\n'})));
    }

    @Test
    public void normalizeBytes_aCrAtTheStart_becomesLf() {
        // act, assert
        assertThat(SourceLines.normalize(new byte[] {'\r', '\n', 'x'}), is(equalTo(new byte[] {'\n', 'x'})));
        assertThat(SourceLines.normalize(new byte[] {'\r'}), is(equalTo(new byte[] {'\n'})));
    }

    @Test
    public void normalizeBytes_withoutCr_isTheSameArray() {
        // arrange
        final byte[] bytes = {'a', '\n', 'b'};

        // act, assert
        assertThat(SourceLines.normalize(bytes), is(sameInstance(bytes)));
    }

    @Test
    public void normalizeBytes_ofValidUtf8_areTheBytesOfTheNormalizedText() {
        // arrange
        final String text = "Grüße\r\n€\r";
        final byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);

        // act, assert
        assertThat(
                SourceLines.normalize(utf8),
                is(equalTo(SourceLines.normalize(text).getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    public void normalizeBytes_anEditOfATextThatIsNotUtf8_isKept() {
        // arrange
        // "aä" and "aö" in ISO-8859-1: both decode to "a�" as UTF-8, their bytes differ
        final byte[] before = {'a', (byte) 0xE4};
        final byte[] after = {'a', (byte) 0xF6};
        final ChecksumSupport checksums = new ChecksumSupport();

        // pre-assert
        assertThat(new String(before, StandardCharsets.UTF_8), is(equalTo(new String(after, StandardCharsets.UTF_8))));

        // act, assert
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
        // act, assert
        assertThat(SourceLines.decode("class Ä {}".getBytes(StandardCharsets.UTF_8)), is("class Ä {}"));
        assertThat(SourceLines.decode(new byte[0]), is(""));
    }

    @Test
    public void decode_utf8ByteOrderMark_isDropped() {
        // act, assert
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 0xBF, 'a', 'b')), is("ab"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 0xBF)), is(""));
    }

    @Test
    public void decode_utf16ByteOrderMarks_decodeAsUtf16() {
        // act, assert
        assertThat(SourceLines.decode(bytes(0xFF, 0xFE, 'a', 0, 0xC4, 0, '\n', 0)), is("aÄ\n"));
        assertThat(SourceLines.decode(bytes(0xFE, 0xFF, 0, 'a', 0, 0xC4)), is("aÄ"));
        assertThat(SourceLines.decode(bytes(0xFF, 0xFE)), is(""));
        assertThat(SourceLines.decode(bytes(0xFE, 0xFF)), is(""));
    }

    @Test
    public void decode_partOfAMark_isUtf8Text() {
        // act, assert
        // two of the three bytes of the UTF-8 mark: invalid UTF-8, read as replacement characters
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB)), is("�"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBB, 'a')), is("�a"));
        assertThat(SourceLines.decode(bytes(0xFF)), is("�"));
        assertThat(SourceLines.decode(bytes(0xFE, 'a')), is("�a"));
        assertThat(SourceLines.decode(bytes(0xEF, 0xBC, 0xBF)), is("＿"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="size">
    @Test
    public void size_crlfAndLf_areEqual() {
        // act, assert
        assertThat(SourceLines.size("a\r\nb\r\n".getBytes(StandardCharsets.UTF_8)), is(4L));
        assertThat(SourceLines.size("a\nb\n".getBytes(StandardCharsets.UTF_8)), is(4L));
    }

    @Test
    public void size_utf8ByteOrderMark_isNotCounted() {
        // act, assert
        assertThat(SourceLines.size(new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF, 'a', '\r', '\n'}), is(2L));
    }

    @Test
    public void size_latin1Bytes_countThemselves() {
        // act, assert: two bytes that are no valid UTF-8, not the six bytes of two replacement characters
        assertThat(SourceLines.size(new byte[] {'a', (byte) 0xE4, (byte) 0xFC}), is(3L));
    }

    @Test
    public void size_utf16_isTheUtf8LengthOfItsText() {
        // arrange: "\u00e4\r\n" in UTF-16LE with its byte order mark, eight bytes on disk
        final byte[] bytes = {(byte) 0xFF, (byte) 0xFE, (byte) 0xE4, 0, '\r', 0, '\n', 0};

        // act, assert: two bytes for the umlaut, one for the line feed
        assertThat(SourceLines.size(bytes), is(3L));
    }

    @Test
    public void size_empty_isZero() {
        // act, assert
        assertThat(SourceLines.size(new byte[0]), is(0L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="checksumBytes">
    @Test
    public void checksumBytes_utf16CrlfAndLf_areEqual() {
        // arrange: the same UTF-16LE text, with a byte order mark, once with CRLF and once with LF
        final byte[] crlf = withUtf16LeBom("a\r\nb\r\n");
        final byte[] lf = withUtf16LeBom("a\nb\n");

        // act, assert
        assertThat(SourceLines.checksumBytes(crlf), is(equalTo(SourceLines.checksumBytes(lf))));
        assertThat(SourceLines.checksumBytes(lf), is(equalTo("a\nb\n".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    public void checksumBytes_utf16BigEndian_isItsNormalizedText() {
        // arrange
        final byte[] text = "a\r\n".getBytes(StandardCharsets.UTF_16BE);
        final byte[] bytes = new byte[text.length + 2];
        bytes[0] = (byte) 0xFE;
        bytes[1] = (byte) 0xFF;
        System.arraycopy(text, 0, bytes, 2, text.length);

        // act, assert
        assertThat(SourceLines.checksumBytes(bytes), is(equalTo("a\n".getBytes(StandardCharsets.UTF_8))));
    }

    @Test
    public void checksumBytes_utf8_isItsNormalizedBytes() {
        // act, assert: a byte that is not valid UTF-8 stays in what the checksum sees
        final byte[] bytes = {'a', '\r', '\n', (byte) 0xFF};
        assertThat(SourceLines.checksumBytes(bytes), is(equalTo(new byte[] {'a', '\n', (byte) 0xFF})));
    }

    private static byte[] withUtf16LeBom(final String text) {
        final byte[] encoded = text.getBytes(StandardCharsets.UTF_16LE);
        final byte[] bytes = new byte[encoded.length + 2];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xFE;
        System.arraycopy(encoded, 0, bytes, 2, encoded.length);
        return bytes;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cutIndex">
    /** A character outside the Basic Multilingual Plane: two {@code char}s, a surrogate pair. */
    private static final String EMOJI = new String(Character.toChars(0x1F600));

    @Test
    public void cutIndex_betweenTheTwoHalvesOfAPair_isBeforeThePair() {
        // arrange
        final String text = "ab" + EMOJI + "cd";

        // act, assert
        assertThat(SourceLines.cutIndex(text, 3), is(2));
    }

    @Test
    public void cutIndex_atACodePointBoundary_isTheIndex() {
        // arrange
        final String text = "ab" + EMOJI + "cd";

        // act, assert
        assertThat(SourceLines.cutIndex(text, 2), is(2));
        assertThat(SourceLines.cutIndex(text, 4), is(4));
        assertThat(SourceLines.cutIndex(text, 0), is(0));
        assertThat(SourceLines.cutIndex(text, text.length()), is(text.length()));
        assertThat(SourceLines.cutIndex(EMOJI, EMOJI.length()), is(EMOJI.length()));
    }

    @Test
    public void cutIndex_aLoneHighSurrogate_isNoPairToKeepWhole() {
        // arrange: a high surrogate followed by an ordinary character
        final String text = "a" + EMOJI.charAt(0) + "b";

        // act, assert
        assertThat(SourceLines.cutIndex(text, 2), is(2));
    }
    // </editor-fold>
}
