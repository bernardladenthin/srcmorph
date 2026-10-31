// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The absolute-path requirement ({@link AbsolutePathRequirement}) for every path a configuration
 * resolves against a directory, through the engines' own entry points: a model's {@code modelPath},
 * the fallback {@code llamaModelPath} (both against {@code modelDirectory}) and a prompt's
 * {@code templateFile} (against {@code promptDirectory}). Each case runs once per kind of directory:
 * unset, relative, absolute, and absolute but missing. An absolute path must win in all of them, and
 * an absolute directory must make a relative path below it absolute.
 */
@AbsolutePathRequirement
public class ConfiguredPathResolutionTest {

    private static final String PROMPT = "You are a Java code indexer.\n";

    @TempDir
    Path tempDir;

    /** An existing prompt file outside every directory a case configures. */
    private Path absolutePrompt;

    @BeforeEach
    void writeTheAbsolutePrompt() throws IOException {
        final Path elsewhere = Files.createDirectories(tempDir.resolve("elsewhere"));
        absolutePrompt = Files.write(elsewhere.resolve("file-body-java.md"), PROMPT.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * The kinds of directory an absolute path must ignore, each built from the test's temp directory.
     *
     * @return one function per kind, from the temp directory to the directory (or {@code null})
     */
    static List<Function<Path, @Nullable File>> directories() {
        return Arrays.<Function<Path, @Nullable File>>asList(
                temp -> null,
                temp -> new File("relative-directory"),
                temp -> temp.resolve("directory").toFile(),
                temp -> temp.resolve("missing").resolve("directory").toFile());
    }

    private static AiModelDefinition model(final String modelPath) {
        final AiModelDefinition definition = new AiModelDefinition();
        definition.setKey("model");
        definition.setModelPath(modelPath);
        return definition;
    }

    private static AiPromptDefinition promptFile(final String templateFile) {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey("file-body-java");
        definition.setTemplateFile(templateFile);
        return definition;
    }

    // <editor-fold defaultstate="collapsed" desc="absolute paths win over every directory">
    @ParameterizedTest
    @MethodSource("directories")
    public void modelPath_absolute_isUsedAsItIs(final Function<Path, @Nullable File> directory) throws Exception {
        final String absolute =
                tempDir.resolve("elsewhere").resolve("model.gguf").toString();

        final AiModelDefinitionSupport support = EngineSupport.buildAiModelDefinitionSupport(
                Collections.singletonList(model(absolute)), directory.apply(tempDir));

        assertThat(support.getConfig("model").getModelPath(), is(absolute));
    }

    @ParameterizedTest
    @MethodSource("directories")
    public void llamaModelPath_absolute_isUsedAsItIs(final Function<Path, @Nullable File> directory) throws Exception {
        final String absolute =
                tempDir.resolve("elsewhere").resolve("fallback.gguf").toString();
        final SrcMorphConfiguration config = new SrcMorphConfiguration();
        config.setLlamaModelPath(absolute);
        config.setModelDirectory(directory.apply(tempDir));

        final AiModelDefinitionSupport support =
                EngineSupport.buildAiModelDefinitionSupport(null, config.getModelDirectory());

        assertThat(EngineSupport.resolveLlamaCppJniConfig(config, support).modelPath(), is(absolute));
    }

    @ParameterizedTest
    @MethodSource("directories")
    public void templateFile_absolute_isReadAsItIs(final Function<Path, @Nullable File> directory) throws Exception {
        final List<AiPromptDefinition> prompts = Collections.singletonList(promptFile(absolutePrompt.toString()));

        assertThat(
                EngineSupport.buildPromptSupport(prompts, directory.apply(tempDir))
                        .systemPrompt("file-body-java"),
                is(PROMPT));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="an absolute directory makes relative paths absolute">
    @Test
    public void modelPath_relativeBelowAnAbsoluteDirectory_becomesAbsolute() throws Exception {
        final File modelDirectory = tempDir.resolve("models").toFile();

        final String resolved = EngineSupport.buildAiModelDefinitionSupport(
                        Collections.singletonList(model("sub/model.gguf")), modelDirectory)
                .getConfig("model")
                .getModelPath();

        assertThat(resolved, is(new File(modelDirectory, "sub/model.gguf").getPath()));
        assertThat(new File(resolved).isAbsolute(), is(true));
    }

    @Test
    public void templateFile_relativeBelowAnAbsoluteDirectory_isReadFromThere() throws Exception {
        final List<AiPromptDefinition> prompts = Collections.singletonList(promptFile("file-body-java.md"));

        assertThat(
                EngineSupport.buildPromptSupport(
                                prompts, absolutePrompt.getParent().toFile())
                        .systemPrompt("file-body-java"),
                is(PROMPT));
    }
    // </editor-fold>
}
