// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import net.ladenthin.srcmorph.cli.configuration.CCommand;
import net.ladenthin.srcmorph.cli.configuration.CConfiguration;
import net.ladenthin.srcmorph.config.AiCondition;
import net.ladenthin.srcmorph.config.AiConditionGroup;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiRangeCondition;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * {@code examples/build-your-own-jar/srcmorph.yaml} is the {@code srcmorph-selftest} profile of
 * {@code srcmorph-maven-plugin/pom.xml} as one CLI configuration, and stays that: the models, the
 * profile directory, the file rules (with their conditions) and the package and project rules are
 * read from both and compared. A change to one that is not made in the other fails here.
 *
 * <p>Left out on purpose: the pom's GPU knobs ({@code ${ai.gpuLayers}} etc., defaulting to auto-fit,
 * which the YAML gets by leaving them unset), its paths ({@code baseDirectory}, {@code subtrees}, the
 * output directory, the model directory, which differ by nature) and the comments. The profile
 * directory is compared by where it points: both name the same set under the repository's
 * {@code profiles/}, and neither lists a prompt or a fact counter itself.</p>
 */
public class BuildYourOwnJarSelfTestParityTest {

    private static final Path EXAMPLE_DIRECTORY = Path.of("../examples/build-your-own-jar");
    private static final Path PLUGIN_DIRECTORY = Path.of("../srcmorph-maven-plugin");

    private static CConfiguration yaml;
    private static Element plugin;
    private static @Nullable String aiModel;
    private static @Nullable String aiProfileDir;

    @BeforeAll
    static void load() throws Exception {
        yaml = Main.fromYaml(Main.readString(EXAMPLE_DIRECTORY.resolve("srcmorph.yaml")));
        final Element project = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(PLUGIN_DIRECTORY.resolve("pom.xml").toFile())
                .getDocumentElement();
        aiModel = Objects.requireNonNull(text(child(project, "properties"), "ai.model"));
        aiProfileDir = Objects.requireNonNull(text(child(project, "properties"), "ai.profileDir"));
        Element selftest = null;
        for (final Element profile : children(child(project, "profiles"), "profile")) {
            if ("srcmorph-selftest".equals(text(profile, "id"))) {
                selftest = profile;
            }
        }
        plugin = children(child(child(Objects.requireNonNull(selftest), "build"), "plugins"), "plugin")
                .get(0);
        // the module's own plugin, named through its own coordinates
        assertThat(text(plugin, "artifactId"), is("${project.artifactId}"));
    }

    // <editor-fold defaultstate="collapsed" desc="DOM helpers">
    private static List<Element> children(final Element parent, final String name) {
        final List<Element> found = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element && element.getTagName().equals(name)) {
                found.add(element);
            }
        }
        return found;
    }

    private static Element child(final Element parent, final String name) {
        final List<Element> found = children(parent, name);
        assertThat(parent.getTagName() + " has one <" + name + ">", found.size(), is(1));
        return found.get(0);
    }

    private static @Nullable String text(final Element parent, final String name) {
        final List<Element> found = children(parent, name);
        if (found.isEmpty()) {
            return null;
        }
        final String value = found.get(0).getTextContent().trim();
        // ai.model is read with this method too, before it is known
        final String model = aiModel;
        return model == null ? value : value.replace("${ai.model}", model);
    }

    private static Element execution(final String id) {
        for (final Element execution : children(child(plugin, "executions"), "execution")) {
            if (id.equals(text(execution, "id"))) {
                return child(execution, "configuration");
            }
        }
        throw new AssertionError("no execution " + id);
    }

    private static String number(final @Nullable String pomValue) {
        return pomValue == null ? "unset" : String.valueOf(Float.parseFloat(pomValue));
    }
    // </editor-fold>

    @Test
    public void command_isThePlan() {
        assertThat(yaml.command, is(CCommand.Plan));
    }

    @Test
    public void models_matchTheSelfTestProfile() {
        final List<String> pom = new ArrayList<>();
        for (final Element model : children(child(child(plugin, "configuration"), "aiDefinitions"), "aiDefinition")) {
            pom.add(String.join(
                    " | ",
                    text(model, "key"),
                    text(model, "modelPath"),
                    number(text(model, "contextSize")),
                    number(text(model, "maxOutputTokens")),
                    number(text(model, "temperature")),
                    number(text(model, "threads")),
                    number(text(model, "charsPerToken")),
                    String.valueOf(text(model, "warnOnTrim")),
                    number(text(model, "topP")),
                    number(text(model, "topK")),
                    number(text(model, "minP")),
                    number(text(model, "repeatPenalty")),
                    String.valueOf(text(model, "reasoningEffort"))));
        }
        final List<String> example = new ArrayList<>();
        for (final AiModelDefinition model : Objects.requireNonNull(yaml.srcMorph.getAiDefinitions())) {
            example.add(String.join(
                    " | ",
                    model.getKey(),
                    model.getModelPath(),
                    String.valueOf((float) model.getContextSize()),
                    String.valueOf((float) model.getMaxOutputTokens()),
                    String.valueOf(model.getTemperature()),
                    String.valueOf((float) model.getThreads()),
                    String.valueOf((float) model.getCharsPerToken()),
                    String.valueOf(model.isWarnOnTrim()),
                    String.valueOf(model.getTopP()),
                    String.valueOf((float) model.getTopK()),
                    String.valueOf(model.getMinP()),
                    String.valueOf(model.getRepeatPenalty()),
                    model.getReasoningEffort()));
        }
        assertThat(example, is(pom));
    }

    @Test
    public void profileDirectory_matchesTheSelfTestProfile() throws IOException {
        final Element configuration = child(plugin, "configuration");
        assertThat(text(configuration, "profileDirectory"), is("${ai.profileDir}"));
        final String pomDirectory =
                Objects.requireNonNull(aiProfileDir).replace("${project.basedir}", PLUGIN_DIRECTORY.toString());
        final File exampleDirectory = EXAMPLE_DIRECTORY
                .resolve(Objects.requireNonNull(yaml.srcMorph.getProfileDirectory())
                        .getPath())
                .toFile();
        assertThat(exampleDirectory.getCanonicalFile(), is(new File(pomDirectory).getCanonicalFile()));
    }

    @Test
    public void promptsAndFacts_comeFromTheProfileOnly() {
        assertThat(children(child(plugin, "configuration"), "promptDefinitions").isEmpty(), is(true));
        assertThat(children(execution("ai-generate"), "factDefinitions").isEmpty(), is(true));
        assertThat(yaml.srcMorph.getPromptDefinitions(), is(nullValue()));
        assertThat(yaml.srcMorph.getFactDefinitions(), is(nullValue()));
    }

    @Test
    public void scope_matchesTheSelfTestProfile() {
        final Element generate = execution("ai-generate");
        final Element configuration = child(plugin, "configuration");
        final List<String> extensions = new ArrayList<>();
        for (final Element extension : children(child(generate, "fileExtensions"), "fileExtension")) {
            extensions.add(extension.getTextContent().trim());
        }
        final List<String> excludes = new ArrayList<>();
        for (final Element exclude : children(child(configuration, "excludes"), "exclude")) {
            excludes.add(exclude.getTextContent().trim());
        }
        assertThat(yaml.srcMorph.getFileExtensions(), is(extensions));
        assertThat(yaml.srcMorph.getExcludes(), is(excludes));
        assertThat(yaml.srcMorph.getGenerationProvider(), is(text(configuration, "generationProvider")));
    }

    @Test
    public void fileRules_matchTheSelfTestProfile() {
        final List<String> pom = new ArrayList<>();
        for (final Element rule : children(child(execution("ai-generate"), "fieldGenerations"), "fieldGeneration")) {
            final List<Element> condition = children(rule, "condition");
            pom.add(String.join(
                    " | ",
                    text(rule, "id"),
                    text(rule, "promptKey"),
                    text(rule, "aiDefinitionKey"),
                    String.valueOf(text(rule, "priority") == null ? "0" : text(rule, "priority")),
                    String.valueOf(text(rule, "onOversize")),
                    String.valueOf(text(rule, "maxChunks") == null ? "0" : text(rule, "maxChunks")),
                    String.valueOf(text(rule, "factsKey")),
                    String.valueOf("true".equals(text(rule, "fallback"))),
                    condition.isEmpty() ? "-" : describe(condition.get(0))));
        }
        final List<String> example = new ArrayList<>();
        for (final AiFieldGenerationConfig rule : Objects.requireNonNull(yaml.srcMorph.getFieldGenerations())) {
            final AiCondition condition = rule.getCondition();
            example.add(String.join(
                    " | ",
                    rule.getId(),
                    rule.getPromptKey(),
                    rule.getAiDefinitionKey(),
                    String.valueOf(rule.getPriority()),
                    String.valueOf(rule.getOnOversize()),
                    String.valueOf(rule.getMaxChunks()),
                    String.valueOf(rule.getFactsKey()),
                    String.valueOf(rule.isFallback()),
                    condition == null ? "-" : describe(condition)));
        }
        assertThat(example, is(pom));
    }

    @Test
    public void phaseRules_matchTheAggregateExecutions() {
        assertThat(describeRules(yaml.packageFieldGenerations), is(describeRules(execution("ai-aggregate-packages"))));
        assertThat(describeRules(yaml.projectFieldGenerations), is(describeRules(execution("ai-aggregate-project"))));
    }

    // <editor-fold defaultstate="collapsed" desc="rule and condition descriptions">
    private static List<String> describeRules(final Element configuration) {
        final List<String> rules = new ArrayList<>();
        for (final Element rule : children(child(configuration, "fieldGenerations"), "fieldGeneration")) {
            rules.add(text(rule, "promptKey") + " | " + text(rule, "aiDefinitionKey"));
        }
        return rules;
    }

    private static List<String> describeRules(final @Nullable List<AiFieldGenerationConfig> configured) {
        assertThat(configured, is(notNullValue()));
        final List<String> rules = new ArrayList<>();
        for (final AiFieldGenerationConfig rule : Objects.requireNonNull(configured)) {
            rules.add(rule.getPromptKey() + " | " + rule.getAiDefinitionKey());
        }
        return rules;
    }

    private static String describe(final Element condition) {
        final List<String> parts = new ArrayList<>();
        for (final Element and : children(condition, "and")) {
            final List<String> inner = new ArrayList<>();
            for (final Element nested : children(child(and, "conditions"), "condition")) {
                inner.add(describe(nested));
            }
            parts.add("and" + inner);
        }
        for (final Element extensions : children(condition, "extensions")) {
            final List<String> values = new ArrayList<>();
            for (final Element extension : children(extensions, "extension")) {
                values.add(extension.getTextContent().trim());
            }
            parts.add("extensions" + values);
        }
        for (final Element size : children(condition, "size")) {
            parts.add("size(min=" + text(size, "min") + ", max=" + text(size, "max") + ")");
        }
        return String.join(" ", parts);
    }

    private static String describe(final AiCondition condition) {
        final List<String> parts = new ArrayList<>();
        final AiConditionGroup and = condition.getAnd();
        if (and != null) {
            final List<String> inner = new ArrayList<>();
            for (final AiCondition nested : Objects.requireNonNull(and.getConditions())) {
                inner.add(describe(nested));
            }
            parts.add("and" + inner);
        }
        final List<String> extensions = condition.getExtensions();
        if (extensions != null) {
            parts.add("extensions" + extensions);
        }
        final AiRangeCondition size = condition.getSize();
        if (size != null) {
            parts.add("size(min=" + (size.getMin() > 0 ? String.valueOf(size.getMin()) : "null") + ", max="
                    + (size.getMax() > 0 ? String.valueOf(size.getMax()) : "null") + ")");
        }
        return String.join(" ", parts);
    }
    // </editor-fold>

    /** The YAML's own model checks, beside the parity: the safe start and a fallback for every file. */
    @Test
    public void example_startsWithThePlanAndHasExactlyOneFallback() {
        final SrcMorphConfiguration srcMorph = yaml.srcMorph;
        int fallbacks = 0;
        for (final AiFieldGenerationConfig rule : Objects.requireNonNull(srcMorph.getFieldGenerations())) {
            fallbacks += rule.isFallback() ? 1 : 0;
        }
        assertThat(fallbacks, is(1));
        assertThat(srcMorph.getGenerationProvider(), is("llamacpp-jni"));
    }
}
