// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.ladenthin.srcmorph.config.ModelDefinition;
import org.junit.jupiter.api.Test;

public class InputWindowCalculatorTest {

    private static ModelDefinition config(final int contextSize, final int charsPerToken, final int maxOutputTokens) {
        final ModelDefinition config = new ModelDefinition();
        config.setContextSize(contextSize);
        config.setCharsPerToken(charsPerToken);
        config.setMaxOutputTokens(maxOutputTokens);
        return config;
    }

    // <editor-fold defaultstate="collapsed" desc="maxInputChars">
    @Test
    public void maxInputChars_charsPerTokenZero_usesStaticFallback() {
        // arrange
        final ModelDefinition config = config(2048, 0, 128);

        // act, assert
        // charsPerToken <= 0 -> the static fallback is returned verbatim.
        assertThat(InputWindowCalculator.maxInputChars(config), is(ModelDefinition.DEFAULT_MAX_INPUT_CHARS));
    }

    @Test
    public void maxInputChars_concreteWindow_isTheWindowLessOutputAndMargin() {
        // arrange
        // total = 1000 * 4 = 4000; overhead = output 100*4=400 + safety 500 = 900; the whole rendered prompt
        // (template + source) may take the rest: 3100, a 100-multiple already. The template is not taken
        // out here: preparePrompt compares the whole prompt with this budget.
        final ModelDefinition config = config(1000, 4, 100);

        // act, assert
        assertThat(InputWindowCalculator.maxInputChars(config), is(3100));
    }

    @Test
    public void maxInputChars_budgetNotAHundredMultiple_roundsDownAndCountsEveryOverheadTerm() {
        // arrange
        // total = 1000*4 = 4000; overhead = output 99*4=396 + safety 500 = 896; available = 3104 (NOT a
        // 100-multiple) -> rounded down to 3100. Dropping the margin gives 3600, dropping the output 3500,
        // so every term changes the result.
        final ModelDefinition config = config(1000, 4, 99);

        // act, assert
        assertThat(InputWindowCalculator.maxInputChars(config), is(3100));
    }

    @Test
    public void maxInputChars_overheadExceedsWindow_neverNegative() {
        // arrange
        // Tiny context: overhead dwarfs the window, so the budget clamps to 0 (not negative).
        final ModelDefinition config = config(10, 4, 100);

        // act, assert
        assertThat(InputWindowCalculator.maxInputChars(config), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="availableSourceChars">
    @Test
    public void availableSourceChars_concreteWindow_isBudgetMinusBasePromptOnce() {
        // arrange
        // maxInputChars 3100 - basePromptLength 165 = 2935: the template takes its room once.
        final ModelDefinition config = config(1000, 4, 100);

        // act, assert
        assertThat(InputWindowCalculator.availableSourceChars(config, 165), is(2935L));
    }

    @Test
    public void availableSourceChars_overheadExceedsWindow_neverNegative() {
        // arrange
        // maxInputChars clamps to 0; subtracting basePromptLength still clamps to 0 (not negative).
        final ModelDefinition config = config(10, 4, 100);

        // act, assert
        assertThat(InputWindowCalculator.availableSourceChars(config, 165), is(0L));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="exceedsWindow">
    @Test
    public void exceedsWindow_atAndAboveTheSourceBudget_isTrueOnlyAbove() {
        // arrange
        // availableSourceChars = 2935 here.
        final ModelDefinition config = config(1000, 4, 100);

        // act, assert
        assertThat(InputWindowCalculator.exceedsWindow(config, 165, 2935L), is(false));
        assertThat(InputWindowCalculator.exceedsWindow(config, 165, 2936L), is(true));
    }
    // </editor-fold>
}
