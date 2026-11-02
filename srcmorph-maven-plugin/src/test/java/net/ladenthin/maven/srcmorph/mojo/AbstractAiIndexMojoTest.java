// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.MOCK_CONFIGURATION;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.configured;
import static net.ladenthin.maven.srcmorph.mojo.MojoFixtures.writeConfiguration;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link AbstractAiIndexMojo#buildConfiguration(RuleSection)}: the configuration file is the
 * configuration, and Maven fills only what it leaves unset. Its failure mode is silent: a Maven value
 * that overrides the file, or one that is not filled, runs another tree or another model with nothing
 * failing.
 */
public class AbstractAiIndexMojoTest {

    @TempDir
    Path project;

    /** A goal that does nothing of its own, for the base class alone. */
    private static final class TestMojo extends AbstractAiIndexMojo {

        @Override
        protected boolean isPhaseSkipped() {
            return false;
        }

        @Override
        public void execute() {
            // never called
        }
    }

    private TestMojo mojo() {
        return configured(new TestMojo(), project);
    }

    private static Resource resource(final Path directory) {
        final Resource resource = new Resource();
        resource.setDirectory(directory.toString());
        return resource;
    }

    // <editor-fold defaultstate="collapsed" desc="the file">
    @Test
    public void buildConfiguration_noFile_namesWhereItLooked() {
        final MojoExecutionException thrown =
                assertThrows(MojoExecutionException.class, () -> mojo().buildConfiguration(RuleSection.FILES));

        assertThat(thrown.getMessage(), containsString("No srcmorph configuration file at "));
        assertThat(thrown.getMessage(), containsString("srcmorph.yaml"));
    }

    @Test
    public void buildConfiguration_anUnknownKey_namesTheFileAndTheKey() throws IOException {
        writeConfiguration(project, "srcMorph:\n  subtree: [\"src\"]\n");

        final MojoExecutionException thrown =
                assertThrows(MojoExecutionException.class, () -> mojo().buildConfiguration(RuleSection.FILES));

        assertThat(thrown.getMessage(), containsString("Cannot read the srcmorph configuration file"));
        assertThat(thrown.getMessage(), containsString("subtree"));
    }

    @Test
    public void buildConfiguration_aFileWithAnotherExtension_isRefused() throws IOException {
        final Path file = Files.write(project.resolve("srcmorph.txt"), new byte[0]);
        final TestMojo mojo = mojo();
        mojo.configurationFile = file.toFile();

        assertThrows(MojoExecutionException.class, () -> mojo.buildConfiguration(RuleSection.FILES));
    }

    @Test
    public void buildConfiguration_eachSectionTakesItsRules() throws Exception {
        writeConfiguration(
                project,
                MOCK_CONFIGURATION.replace(
                        "projectFieldGenerations:\n  - promptKey: file-body\n",
                        "projectFieldGenerations:\n  - promptKey: project-body\n"));

        assertThat(
                mojo().buildConfiguration(RuleSection.FILES)
                        .getFieldGenerations()
                        .get(0)
                        .isFallback(),
                is(true));
        assertThat(
                mojo().buildConfiguration(RuleSection.PACKAGE)
                        .getFieldGenerations()
                        .get(0)
                        .isFallback(),
                is(false));
        assertThat(
                mojo().buildConfiguration(RuleSection.PROJECT)
                        .getFieldGenerations()
                        .get(0)
                        .getPromptKey(),
                is(equalTo("project-body")));
    }

    @Test
    public void buildConfiguration_aSectionTheFileLacks_isNull() throws Exception {
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");

        assertThat(mojo().buildConfiguration(RuleSection.PACKAGE).getFieldGenerations(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="what Maven fills">
    @Test
    public void buildConfiguration_whatTheFileLeavesUnset_comesFromTheProject() throws Exception {
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final Path sources = Files.createDirectories(project.resolve("src/main/java"));
        final Path resources = Files.createDirectories(project.resolve("src/main/resources"));
        final TestMojo mojo = mojo();
        mojo.resources = Arrays.asList(
                resource(resources),
                resource(resources), // twice: listed once
                resource(project.resolve("src/main/missing")), // missing: left out
                resource(project.getParent())); // outside the base directory: left out

        final SrcMorphConfiguration config = mojo.buildConfiguration(RuleSection.FILES);

        assertThat(config.getBaseDirectory(), is(equalTo(project.toFile())));
        assertThat(
                config.getOutputDirectory(),
                is(equalTo(project.resolve("src/site/ai").toFile())));
        assertThat(
                config.getSubtrees(),
                is(equalTo(Arrays.asList(
                        sources.toFile().getAbsolutePath(), resources.toFile().getAbsolutePath()))));
        assertThat(config.getProjectName(), is(equalTo("fixture-project")));
        assertThat(config.getPluginVersion(), is(equalTo("1.0.0-fixture")));
        assertThat(config.isForce(), is(false));
    }

    @Test
    public void buildConfiguration_noSourceDirectoryAndNoResources_noSubtrees() throws Exception {
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final TestMojo mojo = mojo();
        mojo.sourceDirectory = null;
        mojo.resources = null;

        assertThat(mojo.buildConfiguration(RuleSection.FILES).getSubtrees(), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void buildConfiguration_whatTheFileSets_staysAsItIs() throws Exception {
        Files.createDirectories(project.resolve("src/main/java"));
        writeConfiguration(
                project,
                "srcMorph:\n  subtrees: [\"lib\"]\n  projectName: \"from-the-file\"\n"
                        + "  pluginVersion: \"9.9.9\"\n  force: true\n");

        final SrcMorphConfiguration config = mojo().buildConfiguration(RuleSection.FILES);

        assertThat(config.getSubtrees(), is(equalTo(Collections.singletonList("lib"))));
        assertThat(config.getProjectName(), is(equalTo("from-the-file")));
        assertThat(config.getPluginVersion(), is(equalTo("9.9.9")));
        // srcmorph.force unset (false) does not undo the file's force
        assertThat(config.isForce(), is(true));
    }

    @Test
    public void buildConfiguration_srcmorphForce_overridesTheFile() throws Exception {
        writeConfiguration(project, "srcMorph:\n  force: false\n");
        final TestMojo mojo = mojo();
        mojo.force = true;

        assertThat(mojo.buildConfiguration(RuleSection.FILES).isForce(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="${...} from the Maven properties">
    @Test
    public void buildConfiguration_propertiesFillTheFile_commandLineOverProjectOverSystem() throws Exception {
        writeConfiguration(project, "modelFilter: \"${fixture.a}|${fixture.b}|${fixture.c}|${java.version}\"\n");
        final TestMojo mojo = mojo();
        final Properties projectProperties = new Properties();
        projectProperties.setProperty("fixture.a", "project-a");
        projectProperties.setProperty("fixture.b", "project-b");
        projectProperties.setProperty("java.version", "project-java");
        final Properties userProperties = new Properties();
        userProperties.setProperty("fixture.a", "user-a");
        mojo.projectProperties = projectProperties;
        mojo.userProperties = userProperties;

        assertThat(mojo.properties().get("fixture.a"), is(equalTo("user-a")));
        assertThat(mojo.properties().get("fixture.b"), is(equalTo("project-b")));
        assertThat(mojo.properties().get("java.version"), is(equalTo("project-java")));
        assertThat(mojo.properties().get("os.name"), is(equalTo(System.getProperty("os.name"))));
        assertThat(mojo.properties().containsKey("fixture.c"), is(false));
    }

    @Test
    public void buildConfiguration_aPropertyReachesTheFile() throws Exception {
        writeConfiguration(project, "srcMorph:\n  projectName: \"${fixture.name}\"\n");
        final TestMojo mojo = mojo();
        final Properties userProperties = new Properties();
        userProperties.setProperty("fixture.name", "named-on-the-command-line");
        mojo.userProperties = userProperties;
        mojo.projectProperties = null;

        assertThat(
                mojo.buildConfiguration(RuleSection.FILES).getProjectName(), is(equalTo("named-on-the-command-line")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="messages">
    @Test
    public void messageOf_exceptionWithMessage_returnsThatMessage() {
        assertThat(AbstractAiIndexMojo.messageOf(new SrcMorphException("boom")), is(equalTo("boom")));
    }

    @Test
    public void messageOf_exceptionWithNullMessage_fallsBackToToString() {
        final SrcMorphException noMessage = new SrcMorphException((String) null);

        assertThat(AbstractAiIndexMojo.messageOf(noMessage), is(equalTo(noMessage.toString())));
    }

    @Test
    public void ioMessage_namesWhatFailedAndWhere() {
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(new File("base"));
        config.setOutputDirectory(new File("out"));

        assertThat(
                AbstractAiIndexMojo.ioMessage("write it", config, new IOException("disk full")),
                is(equalTo("Failed to write it under out from base base: disk full")));
    }
    // </editor-fold>
}
