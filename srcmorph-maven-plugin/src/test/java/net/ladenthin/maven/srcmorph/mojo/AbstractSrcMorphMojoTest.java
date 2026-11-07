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
import java.util.Map;
import java.util.Properties;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import net.ladenthin.srcmorph.support.GeneratorVersion;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link AbstractSrcMorphMojo#buildConfiguration(RuleSection)}: the configuration file is the
 * configuration, and Maven fills only what it leaves unset. Its failure mode is silent: a Maven value
 * that overrides the file, or one that is not filled, runs another tree or another model with nothing
 * failing.
 */
public class AbstractSrcMorphMojoTest {

    @TempDir
    Path project;

    /** A goal that does nothing of its own, for the base class alone. */
    private static final class TestMojo extends AbstractSrcMorphMojo {

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
        // arrange
        final TestMojo mojo = mojo();

        // act
        final MojoExecutionException thrown =
                assertThrows(MojoExecutionException.class, () -> mojo.buildConfiguration(RuleSection.FILES));

        // assert
        assertThat(thrown.getMessage(), containsString("No srcmorph configuration file at "));
        assertThat(thrown.getMessage(), containsString("srcmorph.yaml"));
    }

    @Test
    public void buildConfiguration_anUnknownKey_namesTheFileAndTheKey() throws IOException {
        // arrange
        writeConfiguration(project, "srcMorph:\n  subtree: [\"src\"]\n");
        final TestMojo mojo = mojo();

        // act
        final MojoExecutionException thrown =
                assertThrows(MojoExecutionException.class, () -> mojo.buildConfiguration(RuleSection.FILES));

        // assert
        assertThat(thrown.getMessage(), containsString("Cannot read the srcmorph configuration file"));
        assertThat(thrown.getMessage(), containsString("subtree"));
    }

    @Test
    public void buildConfiguration_aFileWithAnotherExtension_isRefused() throws IOException {
        // arrange
        final Path file = Files.write(project.resolve("srcmorph.txt"), new byte[0]);
        final TestMojo mojo = mojo();
        mojo.configurationFile = file.toFile();

        // act, assert
        assertThrows(MojoExecutionException.class, () -> mojo.buildConfiguration(RuleSection.FILES));
    }

    @Test
    public void buildConfiguration_eachSection_takesItsRules() throws Exception {
        // arrange
        writeConfiguration(
                project,
                MOCK_CONFIGURATION.replace(
                        "projectRules:\n  - promptKey: file-body\n", "projectRules:\n  - promptKey: project-body\n"));

        // act, assert
        assertThat(
                mojo().buildConfiguration(RuleSection.FILES).getRules().get(0).isFallback(), is(true));
        assertThat(
                mojo().buildConfiguration(RuleSection.PACKAGE).getRules().get(0).isFallback(), is(false));
        assertThat(
                mojo().buildConfiguration(RuleSection.PROJECT).getRules().get(0).getPromptKey(),
                is(equalTo("project-body")));
    }

    @Test
    public void buildConfiguration_aSectionTheFileLacks_isNull() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");

        // act, assert
        assertThat(mojo().buildConfiguration(RuleSection.PACKAGE).getRules(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="what Maven fills">
    /**
     * A file outside the project directory tells the two defaults apart: the base directory is the
     * project's (Maven's), while the file's own relative paths resolve against the file's directory.
     */
    @Test
    public void buildConfiguration_aFileInASubdirectory_theBaseIsTheProjectAndItsPathsAreTheFiles() throws Exception {
        // arrange
        final Path configDirectory = project.resolve("config");
        writeConfiguration(configDirectory, "srcMorph:\n  generationProvider: mock\n  profileDirectory: profile\n");
        final TestMojo mojo = mojo();
        mojo.configurationFile = configDirectory.resolve("srcmorph.yaml").toFile();

        // act
        final SrcMorphConfiguration config = mojo.buildConfiguration(RuleSection.FILES);

        // assert
        assertThat(config.getBaseDirectory(), is(equalTo(project.toFile())));
        assertThat(
                config.getOutputDirectory(),
                is(equalTo(project.resolve("src/site/ai").toFile())));
        assertThat(
                config.getProfileDirectory(),
                is(equalTo(configDirectory.resolve("profile").toFile())));
    }

    @Test
    public void buildConfiguration_whatTheFileLeavesUnset_comesFromTheProject() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final Path sources = Files.createDirectories(project.resolve("src/main/java"));
        final Path resources = Files.createDirectories(project.resolve("src/main/resources"));
        final TestMojo mojo = mojo();
        mojo.resources = Arrays.asList(
                resource(resources),
                resource(resources), // twice: listed once
                resource(project.resolve("src/main/missing")), // missing: left out
                resource(project.getParent())); // outside the base directory: left out

        // act
        final SrcMorphConfiguration config = mojo.buildConfiguration(RuleSection.FILES);

        // assert
        assertThat(config.getBaseDirectory(), is(equalTo(project.toFile())));
        assertThat(
                config.getOutputDirectory(),
                is(equalTo(project.resolve("src/site/ai").toFile())));
        assertThat(
                config.getSubtrees(),
                is(equalTo(Arrays.asList(
                        sources.toFile().getAbsolutePath(), resources.toFile().getAbsolutePath()))));
        assertThat(config.getProjectName(), is(equalTo("fixture-project")));
        // the generator is srcmorph itself, not the project
        assertThat(config.getGeneratorVersion(), is(equalTo(new GeneratorVersion().version())));
        assertThat(config.isForce(), is(false));
    }

    @Test
    public void buildConfiguration_aResourceDirectoryHoldingTheSources_isNoSubtree() throws Exception {
        // arrange
        // a POM that packs files of the project directory itself: indexing it would index the whole project
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final Path sources = Files.createDirectories(project.resolve("src/main/java"));
        final Path resources = Files.createDirectories(project.resolve("src/main/resources"));
        final TestMojo mojo = mojo();
        final CapturingLog log = new CapturingLog();
        mojo.setLog(log);
        mojo.resources = Arrays.asList(resource(project), resource(resources), resource(project.resolve("src")));

        // act, assert
        assertThat(
                mojo.buildConfiguration(RuleSection.FILES).getSubtrees(),
                is(equalTo(Arrays.asList(
                        sources.toFile().getAbsolutePath(), resources.toFile().getAbsolutePath()))));
        assertThat(
                log.infoMessages(),
                is(equalTo(Arrays.asList(
                        "Resource directory " + project.toFile() + " is or holds the source directory "
                                + sources.toFile() + "; it is no subtree of its own",
                        "Resource directory " + project.resolve("src").toFile()
                                + " is or holds the source directory " + sources.toFile()
                                + "; it is no subtree of its own"))));
    }

    @Test
    public void buildConfiguration_noSourceDirectory_theResourcesAreTheSubtrees() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final Path resources = Files.createDirectories(project.resolve("src/main/resources"));
        final TestMojo mojo = mojo();
        mojo.sourceDirectory = null;
        mojo.resources = Collections.singletonList(resource(resources));

        // act, assert
        assertThat(
                mojo.buildConfiguration(RuleSection.FILES).getSubtrees(),
                is(equalTo(Collections.singletonList(resources.toFile().getAbsolutePath()))));
    }

    @Test
    public void buildConfiguration_noSourceDirectoryAndNoResources_noSubtrees() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  generationProvider: mock\n");
        final TestMojo mojo = mojo();
        mojo.sourceDirectory = null;
        mojo.resources = null;

        // act, assert
        assertThat(mojo.buildConfiguration(RuleSection.FILES).getSubtrees(), is(equalTo(Collections.emptyList())));
    }

    @Test
    public void buildConfiguration_whatTheFileSets_staysAsItIs() throws Exception {
        // arrange
        Files.createDirectories(project.resolve("src/main/java"));
        writeConfiguration(
                project,
                "srcMorph:\n  subtrees: [\"lib\"]\n  projectName: \"from-the-file\"\n"
                        + "  generatorVersion: \"9.9.9\"\n  force: true\n");

        // act
        final SrcMorphConfiguration config = mojo().buildConfiguration(RuleSection.FILES);

        // assert
        assertThat(config.getSubtrees(), is(equalTo(Collections.singletonList("lib"))));
        assertThat(config.getProjectName(), is(equalTo("from-the-file")));
        assertThat(config.getGeneratorVersion(), is(equalTo("9.9.9")));
        // srcmorph.force unset (false) does not undo the file's force
        assertThat(config.isForce(), is(true));
    }

    @Test
    public void buildConfiguration_srcmorphForce_overridesTheFile() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  force: false\n");
        final TestMojo mojo = mojo();
        mojo.force = true;

        // act, assert
        assertThat(mojo.buildConfiguration(RuleSection.FILES).isForce(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="properties">
    @Test
    public void properties_propertiesFillTheFile_commandLineOverProjectOverSystem() throws Exception {
        // arrange
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

        // act, assert
        assertThat(mojo.properties().get("fixture.a"), is(equalTo("user-a")));
        assertThat(mojo.properties().get("fixture.b"), is(equalTo("project-b")));
        assertThat(mojo.properties().get("java.version"), is(equalTo("project-java")));
        assertThat(mojo.properties().get("os.name"), is(equalTo(System.getProperty("os.name"))));
        assertThat(mojo.properties().containsKey("fixture.c"), is(false));
    }

    @Test
    public void properties_theProjectsValues_areNamedAsInAPom() {
        // arrange
        final TestMojo mojo = mojo();
        mojo.buildDirectory = project.resolve("target").toFile();
        mojo.groupId = "com.example";
        mojo.artifactId = "fixture";

        // act
        final Map<String, String> properties = mojo.properties();

        // assert
        final String basedir = project.toFile().getAbsolutePath();
        assertThat(properties.get("basedir"), is(equalTo(basedir)));
        assertThat(properties.get("project.basedir"), is(equalTo(basedir)));
        assertThat(
                properties.get("project.build.directory"),
                is(equalTo(project.resolve("target").toFile().getAbsolutePath())));
        assertThat(
                properties.get("project.build.sourceDirectory"),
                is(equalTo(project.resolve("src/main/java").toFile().getAbsolutePath())));
        assertThat(properties.get("project.groupId"), is(equalTo("com.example")));
        assertThat(properties.get("project.artifactId"), is(equalTo("fixture")));
        assertThat(properties.get("project.version"), is(equalTo("1.0.0-fixture")));
        assertThat(properties.get("project.name"), is(equalTo("fixture-project")));
    }

    @Test
    public void properties_aProjectValueMavenLeavesUnset_isNoProperty() {
        // arrange
        final TestMojo mojo = mojo();
        mojo.sourceDirectory = null;

        // act
        final Map<String, String> properties = mojo.properties();

        // assert
        assertThat(properties.containsKey("project.build.directory"), is(false));
        assertThat(properties.containsKey("project.build.sourceDirectory"), is(false));
        assertThat(properties.containsKey("project.groupId"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="${...} from the Maven properties">
    @Test
    public void buildConfiguration_projectBasedirProperty_reachesAPath() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  outputDirectory: \"${project.basedir}/docs/ai\"\n");

        // act, assert
        assertThat(
                mojo().buildConfiguration(RuleSection.FILES).getOutputDirectory(),
                is(equalTo(project.resolve("docs/ai").toFile())));
    }

    @Test
    public void buildConfiguration_aUserProperty_reachesTheFile() throws Exception {
        // arrange
        writeConfiguration(project, "srcMorph:\n  projectName: \"${fixture.name}\"\n");
        final TestMojo mojo = mojo();
        final Properties userProperties = new Properties();
        userProperties.setProperty("fixture.name", "named-on-the-command-line");
        mojo.userProperties = userProperties;
        mojo.projectProperties = null;

        // act, assert
        assertThat(
                mojo.buildConfiguration(RuleSection.FILES).getProjectName(), is(equalTo("named-on-the-command-line")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="messageOf">
    @Test
    public void messageOf_exceptionWithMessage_returnsThatMessage() {
        // act, assert
        assertThat(AbstractSrcMorphMojo.messageOf(new SrcMorphException("boom")), is(equalTo("boom")));
    }

    @Test
    public void messageOf_exceptionWithNullMessage_fallsBackToToString() {
        // arrange
        final SrcMorphException noMessage = new SrcMorphException((String) null);

        // act, assert
        assertThat(AbstractSrcMorphMojo.messageOf(noMessage), is(equalTo(noMessage.toString())));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ioMessage">
    @Test
    public void ioMessage_anIOException_namesWhatFailedAndWhere() {
        // arrange
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setBaseDirectory(new File("base"));
        config.setOutputDirectory(new File("out"));

        // act, assert
        assertThat(
                AbstractSrcMorphMojo.ioMessage("write it", config, new IOException("disk full")),
                is(equalTo("Failed to write it under out from base base: disk full")));
    }
    // </editor-fold>
}
