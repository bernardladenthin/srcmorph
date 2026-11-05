// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ShebangTest {

    // <editor-fold defaultstate="collapsed" desc="interpreter">
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource(
            delimiter = '|',
            value = {
                "#!/bin/bash|bash",
                "#!/bin/sh -e|sh",
                "#! /usr/bin/perl -w|perl",
                "#!/usr/bin/env bash|bash",
                "#!/usr/bin/env -S python3 -u|python3",
                "#!/usr/bin/env LANG=C perl|perl",
                "#!/usr/bin/env =x perl|perl",
                "#!/usr/bin/env -u LANG perl|perl",
                "#!/usr/bin/env --unset LANG perl|perl",
                "#!/usr/bin/env -C /tmp bash|bash",
                "#!/usr/bin/env --chdir /tmp bash|bash",
                "#!/usr/bin/env -P /usr/local/bin bash|bash",
                "#!/usr/bin/env -a sh bash|bash",
                "#!/usr/bin/env --argv0 sh bash|bash",
                "#!/usr/bin/env -U deploy perl|perl",
                "#!/usr/bin/env -L deploy perl|perl",
                "#!/usr/bin/env -i bash|bash",
                "#!/usr/bin/env -Spython3 -u|python3",
                "#!/usr/bin/env --split-string=/usr/bin/python3 -u|python3",
                "#!/usr/bin/env --split-string python3|python3",
                "#!/bin/sh\\n#!/bin/bash|sh",
                "#!/usr/bin/env\\nperl|",
                "#!/usr/bin/env\tksh93|ksh93",
                "#!bash|bash",
                "#!/usr/bin/python3.12\\nimport os|python3.12",
            })
    public void interpreter_shebangLine_namesTheProgramOrWhatEnvRuns(final String head, final String interpreter) {
        // act, assert
        assertThat(Shebang.interpreter(head.replace("\\n", "\n")), is(interpreter));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(
            strings = {
                "",
                "#",
                "# comment",
                "#!",
                "#!   ",
                "#!/usr/bin/env",
                "#!/usr/bin/env -S",
                "x#!/bin/sh",
                "#!/bin/",
                "#!/usr/bin/env /"
            })
    public void interpreter_withoutAnInterpreter_isNull(final String head) {
        // act, assert
        assertThat(Shebang.interpreter(head), is(nullValue()));
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(ints = {Shebang.MAX_LINE_CHARS - 4, Shebang.MAX_LINE_CHARS - 3})
    public void interpreter_paddedLine_readsTheFirstMaxLineCharsOfTheLine(final int padding) {
        // arrange
        // "#!" + padding blanks + "bash": the name ends at MAX_LINE_CHARS in the first case only
        final String head = "#!" + " ".repeat(padding - 2) + "bash";

        // act
        final String interpreter = Shebang.interpreter(head);

        // assert
        assertThat(interpreter, is(padding == Shebang.MAX_LINE_CHARS - 4 ? "bash" : "bas"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="matches">
    @ParameterizedTest(name = "{0} matches {1}")
    @CsvSource({
        "bash,bash",
        "python3,python",
        "python3.12,python",
        "python3.12,python3",
        "sh,' sh '",
        "python3.10,python",
        "python3.9,python",
        "python3.12,python3.12",
        "python3.1,python3.1",
        "python3.1.2,python3.1"
    })
    public void matches_theNameOrAVersionOfIt_isTrue(final String interpreter, final String name) {
        // act, assert
        assertThat(Shebang.matches(interpreter, Arrays.asList("other", name)), is(true));
    }

    @ParameterizedTest(name = "{0} does not match {1}")
    @CsvSource({
        "bash,sh",
        "zsh,sh",
        "python,python3",
        "pythonw,python",
        "bash,''",
        "bash,'  '",
        "perl5x,perl",
        "python3.12,python3.1",
        "python31,python3",
        "python3x,python3"
    })
    public void matches_anotherProgram_isFalse(final String interpreter, final String name) {
        // act, assert
        assertThat(Shebang.matches(interpreter, Collections.singletonList(name)), is(false));
    }

    @Test
    public void matches_nullEntryOrNoNames_isFalse() {
        // act, assert
        assertThat(Shebang.matches("bash", Collections.singletonList(null)), is(false));
        assertThat(Shebang.matches("bash", Collections.emptyList()), is(false));
    }
    // </editor-fold>
}
