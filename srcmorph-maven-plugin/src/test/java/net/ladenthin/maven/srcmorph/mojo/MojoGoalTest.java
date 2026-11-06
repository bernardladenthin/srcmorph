// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.MOCK_CONFIGURATION;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.configured;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.setField;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.writeConfiguration;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.writeSource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Every goal run against the {@code mock} configuration file: each takes its own section of the file
 * and writes (or, for {@code check} and {@code planOnly}, does not write) what it should.
 */
public class MojoGoalTest {

    @TempDir
    Path project;

    private Path output() {
        return project.resolve("src/site/ai");
    }

    private void generate() throws Exception {
        configured(new GenerateMojo(), project).execute();
    }

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_mockConfiguration_summarizesTheProjectsSources() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION);
        writeSource(project);

        // act
        generate();

        // assert
        assertThat(Files.exists(output().resolve("main/java/com/example/Foo.java.ai.md")), is(true));
    }

    @Test
    public void generate_planOnly_writesNothingWhateverTheFileSays() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION.replace("srcMorph:\n", "srcMorph:\n  planOnly: false\n"));
        writeSource(project);
        final GenerateMojo mojo = configured(new GenerateMojo(), project);
        setField(mojo, "planOnly", true);

        // act
        mojo.execute();

        // assert
        assertThat(Files.exists(output()), is(false));
    }

    @Test
    public void generate_aConfigurationError_failsTheGoalWithTheEnginesMessage() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        writeSource(project);

        // act
        final MojoExecutionException thrown = assertThrows(MojoExecutionException.class, this::generate);

        // assert
        assertThat(thrown.getMessage(), containsString("No rules configured"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aggregate-packages">
    @Test
    public void aggregatePackages_mockConfiguration_takesThePackageRule() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION);
        writeSource(project);
        generate();

        // act
        configured(new AggregatePackagesMojo(), project).execute();

        // assert
        assertThat(Files.exists(output().resolve("main/java/com/example/package.ai.md")), is(true));
    }

    @Test
    public void aggregatePackages_planOnly_writesNoPackageSummaryWhateverTheFileSays() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION.replace("srcMorph:\n", "srcMorph:\n  planOnly: false\n"));
        writeSource(project);
        generate();
        final AggregatePackagesMojo mojo = configured(new AggregatePackagesMojo(), project);
        setField(mojo, "planOnly", true);

        // act
        mojo.execute();

        // assert
        assertThat(Files.exists(output().resolve("main/java/com/example/package.ai.md")), is(false));
    }

    @Test
    public void aggregatePackages_withoutAPackageRule_failsNamingIt() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION.substring(MOCK_CONFIGURATION.indexOf("projectRules:")));
        writeSource(project);
        generate();
        final AggregatePackagesMojo mojo = configured(new AggregatePackagesMojo(), project);

        // act
        final MojoExecutionException thrown = assertThrows(MojoExecutionException.class, mojo::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("aggregate-packages takes exactly one rule"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="aggregate-project">
    @Test
    public void aggregateProject_planOnly_writesNoProjectSummaryWhateverTheFileSays() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION.replace("srcMorph:\n", "srcMorph:\n  planOnly: false\n"));
        writeSource(project);
        generate();
        configured(new AggregatePackagesMojo(), project).execute();
        final AggregateProjectMojo mojo = configured(new AggregateProjectMojo(), project);
        setField(mojo, "planOnly", true);

        // act
        mojo.execute();

        // assert
        assertThat(Files.exists(output().resolve("project.ai.md")), is(false));
    }

    @Test
    public void aggregateProject_mockConfiguration_takesTheProjectRule() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION);
        writeSource(project);
        generate();
        configured(new AggregatePackagesMojo(), project).execute();

        // act
        configured(new AggregateProjectMojo(), project).execute();

        // assert
        final String index = new String(Files.readAllBytes(output().resolve("project.ai.md")), "UTF-8");
        assertThat(index, containsString("#### Overview"));
        assertThat(index, containsString("fixture-project"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="check">
    @Test
    public void check_aSoundConfiguration_passesAndWritesNothing() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION);
        writeSource(project);

        // act
        configured(new CheckMojo(), project).execute();

        // assert
        assertThat(Files.exists(output()), is(false));
    }

    @Test
    public void check_aPackageRuleOnAnUnknownModel_fails() throws Exception {
        // arrange
        writeConfiguration(
                project,
                MOCK_CONFIGURATION.replaceFirst("aiDefinitionKey: mock-model", "aiDefinitionKey: no-such-model"));
        writeSource(project);
        final CheckMojo mojo = configured(new CheckMojo(), project);

        // act
        final MojoExecutionException thrown = assertThrows(MojoExecutionException.class, mojo::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("no-such-model"));
    }

    @Test
    public void check_withoutAPackageRule_checksTheOtherPhases() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION.substring(MOCK_CONFIGURATION.indexOf("projectRules:")));
        writeSource(project);

        // act
        configured(new CheckMojo(), project).execute();

        // assert
        assertThat(Files.exists(output()), is(false));
    }

    @Test
    public void check_aProjectRuleOnAnUnknownModel_fails() throws Exception {
        // arrange
        writeConfiguration(
                project,
                MOCK_CONFIGURATION.replace(
                        "projectRules:\n  - promptKey: file-body\n    aiDefinitionKey: mock-model\n",
                        "projectRules:\n  - promptKey: file-body\n    aiDefinitionKey: another-model\n"));
        writeSource(project);
        final CheckMojo mojo = configured(new CheckMojo(), project);

        // act
        final MojoExecutionException thrown = assertThrows(MojoExecutionException.class, mojo::execute);

        // assert
        assertThat(thrown.getMessage(), containsString("another-model"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calibrate output">

    /**
     * {@code CalibrateMojo.execute()} exists to hand the user a paste-ready {@code <calibration>}
     * block; the surrounding {@code getLog().info(...)} calls are that output, not decoration.
     *
     * @throws Exception if the goal fails
     */
    @Test
    public void calibrate_mockConfiguration_logsTheInstructionsAndTheRenderedReport() throws Exception {
        // arrange
        writeConfiguration(project, MOCK_CONFIGURATION);
        final CalibrateMojo mojo = configured(new CalibrateMojo(), project);
        final CapturingLog log = new CapturingLog();
        mojo.setLog(log);

        // act
        mojo.execute();

        // assert
        final String all = String.join("\n", log.infoMessages());
        // the blank line that separates the block, asserted by position (contains cannot see it)
        assertThat(log.infoMessages().get(0), is(equalTo("")));
        assertThat(all.contains("Paste each calibration: block into its model's entry"), is(true));
        assertThat(all.contains("measured prefill/decode throughput"), is(true));
        assertThat(all.contains("\ncalibration:\n"), is(true));
        assertThat(all.contains("  decodeTokensPerSecond: "), is(true));
        assertThat(all.contains("mock-model"), is(true));
    }
    // </editor-fold>
}
