// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.Calibration;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFileLoader;
import net.ladenthin.srcmorph.indexer.CalibrationMeasurement;
import org.junit.jupiter.api.Test;

public class CalibrationReportTest {

    // <editor-fold defaultstate="collapsed" desc="ModelMeasurement">
    @Test
    public void modelMeasurement_constructorArgumentsGiven_accessorsReflectThem() {
        // arrange
        final CalibrationMeasurement measurement = new CalibrationMeasurement(1.5d, 100.0d, 50.0d, 4.0d, 90.0d, 16);

        // act
        final CalibrationReport.ModelMeasurement m = new CalibrationReport.ModelMeasurement("my-model", measurement);

        // assert
        assertThat(m.modelKey(), is("my-model"));
        assertThat(m.measurement(), is(measurement));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="measurements">
    @Test
    public void measurements_sourceMutatedAfterConstruction_returnsDefensiveCopyInOrder() {
        // arrange
        final CalibrationReport.ModelMeasurement m1 = new CalibrationReport.ModelMeasurement(
                "a", new CalibrationMeasurement(1.0d, 1.0d, 1.0d, 1.0d, 1.0d, 1));
        final CalibrationReport.ModelMeasurement m2 = new CalibrationReport.ModelMeasurement(
                "b", new CalibrationMeasurement(2.0d, 2.0d, 2.0d, 2.0d, 2.0d, 2));
        final List<CalibrationReport.ModelMeasurement> source = new ArrayList<>(Arrays.asList(m1, m2));
        final CalibrationReport report = new CalibrationReport(source);

        // act
        // Mutating the source list after construction must not affect the report (defensive copy).
        source.clear();

        // assert
        assertThat(report.measurements(), is(equalTo(Arrays.asList(m1, m2))));
    }

    @Test
    public void measurements_addAttempted_isUnmodifiable() {
        // arrange
        final CalibrationReport report = new CalibrationReport(new ArrayList<CalibrationReport.ModelMeasurement>());

        // act, assert
        try {
            report.measurements()
                    .add(new CalibrationReport.ModelMeasurement(
                            "x", new CalibrationMeasurement(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0)));
            fail("expected UnsupportedOperationException");
        } catch (final UnsupportedOperationException expected) {
            // expected: measurements() must not allow external mutation
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="renderPasteBlock">
    @Test
    public void renderPasteBlock_noMeasurements_isEmpty() {
        // arrange
        final CalibrationReport report = new CalibrationReport(new ArrayList<CalibrationReport.ModelMeasurement>());

        // act, assert
        assertThat(report.renderPasteBlock(), is(""));
    }

    @Test
    public void renderPasteBlock_twoModels_rendersOneBlockPerModelInOrder() {
        // arrange
        final CalibrationMeasurement measurementA =
                new CalibrationMeasurement(1.234d, 1000.5d, 200.3d, 4.57d, 900.0d, 128);
        final CalibrationMeasurement measurementB = new CalibrationMeasurement(2.0d, 500.0d, 100.0d, 3.0d, 450.0d, 64);
        final CalibrationReport report = new CalibrationReport(Arrays.asList(
                new CalibrationReport.ModelMeasurement("model-a", measurementA),
                new CalibrationReport.ModelMeasurement("model-b", measurementB)));

        final String expected = "# calibration of the aiDefinitions entry \"model-a\" (this machine)\n"
                + "calibration:\n"
                + "  prefillTokensPerSecond: 1000.5\n"
                + "  decodeTokensPerSecond: 200.3\n"
                + "  charsPerToken: 4.57\n"
                + "# calibration of the aiDefinitions entry \"model-b\" (this machine)\n"
                + "calibration:\n"
                + "  prefillTokensPerSecond: 500.0\n"
                + "  decodeTokensPerSecond: 100.0\n"
                + "  charsPerToken: 3.00\n";

        // act, assert
        assertThat(report.renderPasteBlock(), is(expected));
    }

    /** The block is meant to be pasted: indented into a model's entry, the strict loader binds it as written. */
    @Test
    public void renderPasteBlock_pastedIntoAModelsEntry_bindsThroughTheStrictLoader() throws IOException {
        // arrange
        final CalibrationReport report = new CalibrationReport(Arrays.asList(new CalibrationReport.ModelMeasurement(
                "model-a", new CalibrationMeasurement(1.234d, 1000.5d, 200.3d, 4.57d, 900.0d, 128))));
        final StringBuilder yaml =
                new StringBuilder("srcMorph:\n  aiDefinitions:\n    - key: model-a\n      modelPath: a.gguf\n");
        for (final String line : report.renderPasteBlock().split("\n")) {
            yaml.append("      ").append(line).append('\n');
        }

        // act
        final Calibration calibration = SrcMorphConfigurationFileLoader.parse(yaml.toString(), true)
                .getSrcMorph()
                .getAiDefinitions()
                .get(0)
                .getCalibration();

        // assert
        assertThat(calibration.getPrefillTokensPerSecond(), is(1000.5d));
        assertThat(calibration.getDecodeTokensPerSecond(), is(200.3d));
        assertThat(calibration.getCharsPerToken(), is(4.57d));
    }

    @Test
    public void renderPasteBlock_crLfPlatform_endsEveryLineWithLineFeed() throws IOException, InterruptedException {
        // arrange
        // String.format's %n is the platform line separator, fixed when the JVM starts, so only a JVM
        // whose separator is CRLF (Windows) shows a mixed block; on Linux this test starts one.
        final List<String> command = new ArrayList<>();
        command.add(Paths.get(System.getProperty("java.home"), "bin", "java").toString());
        if (!"\r\n".equals(System.lineSeparator())) {
            command.add("-Dline.separator=\r\n");
        }
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(RenderPasteBlockInThisJvm.class.getName());

        // act
        final Process process = new ProcessBuilder(command).start();
        final byte[] stdout = readAll(process.getInputStream());
        final byte[] stderr = readAll(process.getErrorStream());
        final int exit = process.waitFor();

        // assert
        assertThat(new String(stderr, StandardCharsets.UTF_8), exit, is(0));
        final String block = new String(stdout, StandardCharsets.UTF_8);
        assertThat(block, not(containsString("\r")));
        assertThat(block, is(RenderPasteBlockInThisJvm.EXPECTED));
    }

    private static byte[] readAll(final InputStream in) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];
        for (int n; (n = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /** Renders one block in a JVM started with a CRLF line separator; fails when it is not one. */
    static final class RenderPasteBlockInThisJvm {

        static final String EXPECTED = "# calibration of the aiDefinitions entry \"model-a\" (this machine)\n"
                + "calibration:\n"
                + "  prefillTokensPerSecond: 1000.5\n"
                + "  decodeTokensPerSecond: 200.3\n"
                + "  charsPerToken: 4.57\n";

        private RenderPasteBlockInThisJvm() {}

        public static void main(final String[] args) throws IOException {
            if (!"\r\n".equals(System.lineSeparator())) {
                // Without the CRLF separator the test would pass vacuously; the exception exits the
                // child with a failure status and its message reaches the parent on stderr.
                throw new IllegalStateException("the line separator of this JVM is not CRLF");
            }
            final CalibrationReport report =
                    new CalibrationReport(Collections.singletonList(new CalibrationReport.ModelMeasurement(
                            "model-a", new CalibrationMeasurement(1.234d, 1000.5d, 200.3d, 4.57d, 900.0d, 128))));
            System.out.write(report.renderPasteBlock().getBytes(StandardCharsets.UTF_8));
            System.out.flush();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_oneModel_containsModelKey() {
        // arrange
        final CalibrationReport report = new CalibrationReport(Arrays.asList(new CalibrationReport.ModelMeasurement(
                "unique-model-key", new CalibrationMeasurement(0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0))));

        // act, assert
        assertThat(report.toString(), containsString("unique-model-key"));
    }
    // </editor-fold>

    private static CalibrationReport reportOf(final CalibrationReport.ModelMeasurement... entries) {
        return new CalibrationReport(Arrays.asList(entries));
    }

    private static CalibrationReport.ModelMeasurement entry(final String key) {
        return new CalibrationReport.ModelMeasurement(
                key, new CalibrationMeasurement(1.2345d, 1234.56d, 45.67d, 3.214d, 1200.4d, 128));
    }

    // <editor-fold defaultstate="collapsed" desc="renderJson">
    /**
     * Pinned as a whole document, not field by field. A per-field assertion would pass on output that
     * is not valid JSON at all -- a missing brace, a stray comma -- which is precisely the failure
     * mode a hand-rolled writer has.
     */
    @Test
    public void renderJson_singleModel_isExactlyThisDocument() {
        // act, assert
        assertThat(
                reportOf(entry("qwen35-4b")).renderJson(),
                is(equalTo("{\n" + "  \"models\": [\n"
                        + "    {\n"
                        + "      \"modelKey\": \"qwen35-4b\",\n"
                        + "      \"loadSeconds\": 1.235,\n"
                        + "      \"prefillTokensPerSecond\": 1234.6,\n"
                        + "      \"decodeTokensPerSecond\": 45.7,\n"
                        + "      \"charsPerToken\": 3.21,\n"
                        + "      \"midPrefillTokensPerSecond\": 1200.4,\n"
                        + "      \"cachedPromptTokens\": 128\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}\n")));
    }

    /** The separator between entries is the one thing a single-model test cannot see. */
    @Test
    public void renderJson_twoModels_separatesEntriesWithACommaAndNoTrailingComma() {
        // act
        final String json = reportOf(entry("a"), entry("b")).renderJson();

        // assert
        assertThat(json, containsString("    },\n    {\n"));
        assertThat(json, not(containsString("    },\n  ]")));
    }

    /** An empty run must still produce a parseable document, not a dangling array. */
    @Test
    public void renderJson_noModels_isAnEmptyArray() {
        // act, assert
        assertThat(
                new CalibrationReport(Collections.emptyList()).renderJson(), is(equalTo("{\n  \"models\": []\n}\n")));
    }

    /**
     * The numbers the two documents share must be byte-identical to the ones {@link
     * CalibrationReport#renderPasteBlock()} pastes, or a user comparing the JSON against the paste block
     * would see two different measurements of the same run.
     */
    @Test
    public void renderJson_oneModel_reusesTheExactNumberFormattingOfThePasteBlock() {
        // arrange
        final CalibrationReport report = reportOf(entry("m"));

        // act
        final String block = report.renderPasteBlock();
        final String json = report.renderJson();

        // assert
        assertThat(block, containsString("  prefillTokensPerSecond: 1234.6\n"));
        assertThat(json, containsString("\"prefillTokensPerSecond\": 1234.6,"));
        assertThat(block, containsString("  charsPerToken: 3.21\n"));
        assertThat(json, containsString("\"charsPerToken\": 3.21,"));
    }

    /**
     * A model key comes from user configuration. Unescaped, a single quote in it produces a file
     * neither parser accepts -- so every escape branch is exercised, including the numeric fallback
     * for control characters and the boundary just above it.
     */
    @Test
    public void renderJson_modelKeyWithSpecialCharacters_isEscapedForBothFormats() {
        // arrange
        final String key = "a\"b\\c\nd\re\tf\u0001g h";

        // act
        final String json = reportOf(entry(key)).renderJson();
        final String yaml = reportOf(entry(key)).renderYaml();

        // assert
        final String expected = "\"a\\\"b\\\\c\\nd\\re\\tf\\u0001g h\"";
        assertThat(json, containsString("\"modelKey\": " + expected + ","));
        assertThat(yaml, containsString("  - modelKey: " + expected + "\n"));
    }

    /** The space is the boundary of the {@code c < ' '} control-character branch: it must pass through. */
    @Test
    public void renderJson_modelKeyWithSpace_isNotEscaped() {
        // act, assert
        assertThat(reportOf(entry("two words")).renderJson(), containsString("\"modelKey\": \"two words\","));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="renderYaml">
    @Test
    public void renderYaml_singleModel_isExactlyThisDocument() {
        // act, assert
        assertThat(
                reportOf(entry("qwen35-4b")).renderYaml(),
                is(equalTo("models:\n" + "  - modelKey: \"qwen35-4b\"\n"
                        + "    loadSeconds: 1.235\n"
                        + "    prefillTokensPerSecond: 1234.6\n"
                        + "    decodeTokensPerSecond: 45.7\n"
                        + "    charsPerToken: 3.21\n"
                        + "    midPrefillTokensPerSecond: 1200.4\n"
                        + "    cachedPromptTokens: 128\n")));
    }

    @Test
    public void renderYaml_twoModels_emitsOneSequenceItemPerModel() {
        // act
        final String yaml = reportOf(entry("a"), entry("b")).renderYaml();

        // assert
        assertThat(yaml, containsString("  - modelKey: \"a\"\n"));
        assertThat(yaml, containsString("  - modelKey: \"b\"\n"));
    }

    @Test
    public void renderYaml_noModels_isAnEmptySequence() {
        // act, assert
        assertThat(new CalibrationReport(Collections.emptyList()).renderYaml(), is(equalTo("models: []\n")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JSON_FILE_NAME / YAML_FILE_NAME">
    /** The two file names are part of the contract with CalibrateEngine and its documentation. */
    @Test
    public void fileNames_jsonAndYaml_areTheDocumentedOnes() {
        // act, assert
        assertThat(CalibrationReport.JSON_FILE_NAME, is("srcmorph-calibration.json"));
        assertThat(CalibrationReport.YAML_FILE_NAME, is("srcmorph-calibration.yaml"));
    }
    // </editor-fold>
}
