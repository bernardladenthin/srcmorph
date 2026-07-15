// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.indexer.AiCalibrationMeasurement;

/**
 * The outcome of one {@link CalibrateEngine#execute()} run: one {@link ModelMeasurement} per distinct
 * routed model, in calibration order.
 *
 * <p>{@link #renderXml()} is the pure, paste-ready {@code <calibration>} XML block renderer — the same
 * text the {@code ai-index:calibrate} goal has always printed, extracted here so it is unit-testable
 * without a Maven {@code Log} and reusable by any future caller (e.g. a CLI).</p>
 */
@ToString
@EqualsAndHashCode
public final class CalibrationReport {

    private final List<ModelMeasurement> measurements;

    /**
     * Creates a new {@link CalibrationReport}.
     *
     * @param measurements one measurement per distinct routed model, in calibration order
     */
    public CalibrationReport(final List<ModelMeasurement> measurements) {
        this.measurements = new ArrayList<>(measurements);
    }

    /**
     * Returns an unmodifiable view of the per-model measurements, in calibration order.
     *
     * @return the measurements
     */
    public List<ModelMeasurement> measurements() {
        return Collections.unmodifiableList(measurements);
    }

    /**
     * Renders one paste-ready {@code <calibration>} XML block per measured model, each preceded by a
     * comment naming the model key, in calibration order.
     *
     * @return the rendered blocks, or an empty string when no model was measured
     */
    public String renderXml() {
        final StringBuilder out = new StringBuilder();
        for (final ModelMeasurement entry : measurements) {
            appendPasteBlock(out, entry.modelKey(), entry.measurement());
        }
        return out.toString();
    }

    /**
     * Appends a copy-pasteable {@code <calibration>} block (with a comment naming the model) to the buffer.
     *
     * @param out      the buffer
     * @param modelKey the model key (for the comment)
     * @param m        the measurement
     */
    private static void appendPasteBlock(
            final StringBuilder out, final String modelKey, final AiCalibrationMeasurement m) {
        out.append("<!-- calibration for aiDefinition '").append(modelKey).append("' (this machine) -->\n");
        out.append("<calibration>\n");
        out.append(String.format(
                Locale.ROOT,
                "    <prefillTokensPerSecond>%.1f</prefillTokensPerSecond>%n",
                m.prefillTokensPerSecond()));
        out.append(String.format(
                Locale.ROOT, "    <decodeTokensPerSecond>%.1f</decodeTokensPerSecond>%n", m.decodeTokensPerSecond()));
        out.append(String.format(Locale.ROOT, "    <charsPerToken>%.2f</charsPerToken>%n", m.charsPerToken()));
        out.append("</calibration>\n");
    }

    /** One model's key paired with its {@link AiCalibrationMeasurement}. */
    @ToString
    @EqualsAndHashCode
    public static final class ModelMeasurement {

        private final String modelKey;
        private final AiCalibrationMeasurement measurement;

        /**
         * Creates a new {@link ModelMeasurement}.
         *
         * @param modelKey    the {@code aiDefinitionKey} that was calibrated
         * @param measurement the measurement
         */
        public ModelMeasurement(final String modelKey, final AiCalibrationMeasurement measurement) {
            this.modelKey = modelKey;
            this.measurement = measurement;
        }

        /**
         * Returns the calibrated model's key.
         *
         * @return the {@code aiDefinitionKey}
         */
        public String modelKey() {
            return modelKey;
        }

        /**
         * Returns the measurement.
         *
         * @return the calibration measurement
         */
        public AiCalibrationMeasurement measurement() {
            return measurement;
        }
    }
}
