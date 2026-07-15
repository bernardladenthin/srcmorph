// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

/**
 * Round-trips every shipped {@code examples/config_*.{json,yaml}} fixture (the public, documented
 * configuration examples referenced from the root and per-module {@code README.md} files) through
 * the CLI's actual Jackson mappers ({@link Main#fromJson(String)} / {@link Main#fromYaml(String)})
 * and asserts a minimally sane {@link CConfiguration}/{@link SrcMorphConfiguration} object graph.
 *
 * <p>Unlike {@code configuration.ConfigBindingTest} (which pins the exact object graph of one
 * ad-hoc private fixture pair), this test is a <em>coverage sweep</em> over the public examples
 * directory: every {@code config_*.json} and {@code config_*.yaml} file under the repository-root
 * {@code examples/} directory must parse without error under the CLI's strict
 * ({@code FAIL_ON_UNKNOWN_PROPERTIES}) mappers, so an example can never silently drift out of sync
 * with {@link SrcMorphConfiguration}'s actual field set. Maven runs tests with the working directory
 * set to the module's own base directory ({@code srcmorph-cli/}), so the examples directory is
 * addressed relative to that as {@code ../examples}.</p>
 */
public class ExamplesConfigBindingTest {

    /**
     * The repository-root {@code examples/} directory, addressed relative to this module's own base
     * directory (Maven Surefire's working directory during a test run).
     */
    private static final Path EXAMPLES_DIRECTORY = Path.of("../examples");

    /**
     * Builds one {@link DynamicTest} per {@code config_*.json}/{@code config_*.yaml} file found under
     * {@link #EXAMPLES_DIRECTORY}, each asserting that the file parses cleanly and yields a sane
     * configuration object graph.
     *
     * @return one dynamic test per example configuration file
     * @throws IOException if {@link #EXAMPLES_DIRECTORY} cannot be listed
     */
    @TestFactory
    public List<DynamicTest> everyExampleConfig_bindsSuccessfully() throws IOException {
        final List<Path> exampleConfigs = listExampleConfigFiles();
        assertThat(
                "expected at least one example config fixture under " + EXAMPLES_DIRECTORY,
                exampleConfigs.size(),
                greaterThan(0));

        final List<DynamicTest> tests = new ArrayList<>(exampleConfigs.size());
        for (final Path exampleConfig : exampleConfigs) {
            tests.add(DynamicTest.dynamicTest(
                    exampleConfig.getFileName().toString(), () -> assertBindsSuccessfully(exampleConfig)));
        }
        return tests;
    }

    private static List<Path> listExampleConfigFiles() throws IOException {
        final List<Path> found = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(EXAMPLES_DIRECTORY, "config_*.{json,yaml}")) {
            for (final Path candidate : stream) {
                found.add(candidate);
            }
        }
        return found;
    }

    private static void assertBindsSuccessfully(final Path exampleConfig) throws IOException {
        final String fileName = exampleConfig.getFileName().toString();
        final String content = Main.readString(exampleConfig);

        final CConfiguration configuration =
                fileName.endsWith(".yaml") ? Main.fromYaml(content) : Main.fromJson(content);

        assertThat(fileName + ": command must be set", configuration.command, is(notNullValue()));

        final SrcMorphConfiguration srcMorph = configuration.srcMorph;
        assertThat(fileName + ": srcMorph must be set", srcMorph, is(notNullValue()));
        assertThat(fileName + ": baseDirectory must be set", srcMorph.getBaseDirectory(), is(notNullValue()));
        assertThat(
                fileName + ": generationProvider must be the safe 'mock' default",
                srcMorph.getGenerationProvider(),
                is(equalTo("mock")));

        // Every shipped example configures at least the fallback routing rule, so the run has
        // something to do (a config with an empty <fieldGenerations> fails GenerateEngine/
        // CalibrateEngine at run time, so an example silently missing it would be useless).
        assertThat(
                fileName + ": fieldGenerations must be configured", srcMorph.getFieldGenerations(), is(notNullValue()));
        assertThat(
                fileName + ": fieldGenerations must not be empty",
                srcMorph.getFieldGenerations().size(),
                greaterThan(0));
    }
}
