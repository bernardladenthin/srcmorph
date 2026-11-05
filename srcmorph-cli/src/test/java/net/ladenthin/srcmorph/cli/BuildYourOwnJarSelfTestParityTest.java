// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFile;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * {@code examples/build-your-own-jar/srcmorph.yaml} is the {@code srcmorph-selftest} configuration of
 * the plugin module ({@code srcmorph-maven-plugin/srcmorph-selftest.yaml}) for a CLI user, and stays
 * that: the models, the profile, the scope and every rule of all three phases are read from both and
 * compared. A change to one that is not made in the other fails here.
 *
 * <p>The self-test file is read as the plugin reads it, its {@code ${ai.*}} filled from the
 * properties of the plugin's pom, which are the defaults the example states (the model, and GPU
 * placement left to auto-fit). Left out on purpose: the paths of the run ({@code baseDirectory},
 * {@code subtrees}, {@code outputDirectory}, {@code modelDirectory}), which differ by nature, and the
 * CLI's {@code command}.</p>
 */
public class BuildYourOwnJarSelfTestParityTest {

    private static final Path EXAMPLE = Path.of("../examples/build-your-own-jar/srcmorph.yaml");
    private static final Path PLUGIN_DIRECTORY =
            Path.of("../srcmorph-maven-plugin").toAbsolutePath().normalize();

    private static final ObjectMapper JSON = SrcMorphConfigurationFileLoader.newJsonMapper();

    private static CConfiguration example;
    private static SrcMorphConfigurationFile selftest;

    @BeforeAll
    static void load() throws Exception {
        example = Main.loadConfiguration(EXAMPLE);
        selftest = SrcMorphConfigurationFileLoader.load(
                PLUGIN_DIRECTORY.resolve("srcmorph-selftest.yaml"), PLUGIN_DIRECTORY.toFile(), pomProperties());
    }

    /** The {@code <properties>} of the plugin's pom, {@code ${project.basedir}} filled. */
    private static Map<String, String> pomProperties() throws Exception {
        final Element project = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(PLUGIN_DIRECTORY.resolve("pom.xml").toFile())
                .getDocumentElement();
        final Map<String, String> properties = new HashMap<>();
        for (Node node = project.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && element.getTagName().equals("properties")) {
                for (Node property = element.getFirstChild(); property != null; property = property.getNextSibling()) {
                    if (property instanceof Element value) {
                        properties.put(
                                value.getTagName(),
                                value.getTextContent()
                                        .trim()
                                        .replace("${project.basedir}", PLUGIN_DIRECTORY.toString()));
                    }
                }
            }
        }
        return properties;
    }

    private static String json(final Object value) throws IOException {
        return JSON.writeValueAsString(value);
    }

    private static SrcMorphConfiguration exampleRun() {
        return example.srcMorph;
    }

    private static SrcMorphConfiguration selftestRun() {
        return selftest.getSrcMorph();
    }

    // <editor-fold defaultstate="collapsed" desc="command">
    @Test
    public void command_buildYourOwnJarExample_isThePlan() {
        // act, assert
        assertThat(example.command, is(CCommand.Plan));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="selftest">
    @Test
    public void selftest_pomPropertiesApplied_leavesNoPropertyUnfilled() throws IOException {
        // act, assert
        assertThat(json(selftest).contains("${"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="models">
    @Test
    public void models_buildYourOwnJarExample_matchTheSelfTest() throws IOException {
        // act, assert
        assertThat(json(exampleRun().getAiDefinitions()), is(json(selftestRun().getAiDefinitions())));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="profileDirectory">
    @Test
    public void profileDirectory_buildYourOwnJarExample_isTheSameSet() throws IOException {
        // act, assert
        assertThat(
                Objects.requireNonNull(exampleRun().getProfileDirectory()).getCanonicalFile(),
                is(Objects.requireNonNull(selftestRun().getProfileDirectory()).getCanonicalFile()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="promptsAndFacts">
    @Test
    public void promptsAndFacts_exampleAndSelfTest_comeFromTheProfileOnly() {
        // act, assert
        assertThat(exampleRun().getPromptDefinitions(), is(nullValue()));
        assertThat(exampleRun().getFactDefinitions(), is(nullValue()));
        assertThat(selftestRun().getPromptDefinitions(), is(nullValue()));
        assertThat(selftestRun().getFactDefinitions(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="scope">
    @Test
    public void scope_buildYourOwnJarExample_matchesTheSelfTest() {
        // act, assert
        assertThat(exampleRun().getFileExtensions(), is(selftestRun().getFileExtensions()));
        assertThat(exampleRun().getExcludes(), is(selftestRun().getExcludes()));
        assertThat(exampleRun().getGenerationProvider(), is(selftestRun().getGenerationProvider()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="fileRules">
    @Test
    public void fileRules_buildYourOwnJarExample_matchTheSelfTest() throws IOException {
        // act, assert
        assertThat(json(exampleRun().getRules()), is(json(selftestRun().getRules())));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="phaseRules">
    @Test
    public void phaseRules_buildYourOwnJarExample_matchTheSelfTest() throws IOException {
        // act, assert
        assertThat(json(example.packageRules), is(json(selftest.getPackageRules())));
        assertThat(json(example.projectRules), is(json(selftest.getProjectRules())));
    }
    // </editor-fold>
}
