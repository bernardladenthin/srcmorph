// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import java.util.Arrays;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.StringLength;

public class AiMdHeaderCodecProperties {

    private final AiMdHeaderCodec codec = new AiMdHeaderCodec();

    // <editor-fold defaultstate="collapsed" desc="write">
    @Property
    boolean write_randomTitleAndChecksum_roundTripPreservesFields(
            @ForAll @StringLength(max = 30) @AlphaChars String title,
            @ForAll @StringLength(max = 10) @AlphaChars String checksum) {
        // arrange
        AiMdHeader original =
                new AiMdHeader(title, "1.0", checksum, "2026-01-01", "2026-01-01", "0.1.0", "1.0", "file");

        // act
        String encoded = codec.write(original);
        AiMdHeader decoded = codec.read(Arrays.asList(encoded.split("\n", -1)));

        // assert
        return original.equals(decoded);
    }
    // </editor-fold>
}
