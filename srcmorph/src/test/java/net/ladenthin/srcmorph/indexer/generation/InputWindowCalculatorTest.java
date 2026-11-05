// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import org.junit.jupiter.api.Test;

public class InputWindowCalculatorTest {

    private static ModelDefinition config(final int contextSize, final int charsPerToken, final int maxOutputTokens) {
        final ModelDefinition config = new ModelDefinition();
        config.setContextSize(contextSize);
        config.setCharsPerToken(charsPerToken);
        config.setMaxOutputTokens(maxOutputTokens);
        return config;
    }

    @Test
    public void eofMarkerLengthAssumptionForConcreteCases() {
        // The concrete numbers in maxInputChars_computesFromContextWindow assume this length; this guard
        // makes any future EOF-marker change fail loudly here rather than silently shift the math.
        assertThat(PromptPreparer.EOF_MARKER_LENGTH, is(35));
    }

    @Test
    public void maxInputChars_charsPerTokenZero_usesStaticFallback() {
        final ModelDefinition config = config(2048, 0, 128);
        // charsPerToken <= 0 -> the static fallback is returned verbatim.
        assertThat(InputWindowCalculator.maxInputChars(config, 999), is(ModelDefinition.DEFAULT_MAX_INPUT_CHARS));
    }

    @Test
    public void maxInputChars_computesFromContextWindow() {
        // total = 1000 * 4 = 4000; overhead = base 165 + EOF 35 + output 100*4=400 + safety 500 = 1100;
        // available = 2900; rounded down to a 100-multiple = 2900.
        final ModelDefinition config = config(1000, 4, 100);
        assertThat(InputWindowCalculator.maxInputChars(config, 165), is(2900));
    }

    @Test
    public void maxInputChars_roundsDownAndCountsEveryOverheadTerm() {
        // total = 1000*4 = 4000; overhead = base 105 + EOF 35 + output 400 + safety 500 = 1040;
        // available = 2960 (NOT a 100-multiple) -> rounded down to 2900. The non-multiple value makes the
        // rounding observable, and 2960 sits so that dropping/flipping the 35-char EOF term (a +-70 shift)
        // crosses the 100-boundary to 3000 -> any such mutation changes the result and is killed.
        final ModelDefinition config = config(1000, 4, 100);
        assertThat(InputWindowCalculator.maxInputChars(config, 105), is(2900));
    }

    @Test
    public void maxInputChars_neverNegative_whenOverheadExceedsWindow() {
        // Tiny context: overhead dwarfs the window, so the budget clamps to 0 (not negative).
        final ModelDefinition config = config(10, 4, 100);
        assertThat(InputWindowCalculator.maxInputChars(config, 165), is(0));
    }

    @Test
    public void availableSourceChars_isBudgetMinusBasePrompt() {
        // maxInputChars 2900 - basePromptLength 165 = 2735.
        final ModelDefinition config = config(1000, 4, 100);
        assertThat(InputWindowCalculator.availableSourceChars(config, 165), is(2735L));
    }

    @Test
    public void availableSourceChars_neverNegative() {
        // maxInputChars clamps to 0; subtracting basePromptLength still clamps to 0 (not negative).
        final ModelDefinition config = config(10, 4, 100);
        assertThat(InputWindowCalculator.availableSourceChars(config, 165), is(0L));
    }

    @Test
    public void exceedsWindow_isTrueOnlyAboveTheSourceBudget() {
        // availableSourceChars = 2735 here.
        final ModelDefinition config = config(1000, 4, 100);
        assertThat(InputWindowCalculator.exceedsWindow(config, 165, 2735L), is(false));
        assertThat(InputWindowCalculator.exceedsWindow(config, 165, 2736L), is(true));
    }
}
