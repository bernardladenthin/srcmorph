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
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.support.AbsolutePathRequirement;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The absolute-path requirement ({@link AbsolutePathRequirement}) for every path a configuration
 * resolves against a directory, through the engines' own entry points: a model's {@code modelPath}
 * (against {@code modelDirectory}) and a prompt's
 * {@code templateFile} (against {@code profileDirectory}). Each case runs once per kind of directory:
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

    private static ModelDefinition model(final String modelPath) {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("model");
        definition.setModelPath(modelPath);
        return definition;
    }

    private static PromptDefinition promptFile(final String templateFile) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey("file-body-java");
        definition.setTemplateFile(templateFile);
        return definition;
    }

    // <editor-fold defaultstate="collapsed" desc="modelPath (buildAiModelDefinitionSupport)">
    @ParameterizedTest
    @MethodSource("directories")
    public void modelPath_absolute_isUsedAsItIs(final Function<Path, @Nullable File> directory) throws Exception {
        // arrange
        final String absolute =
                tempDir.resolve("elsewhere").resolve("model.gguf").toString();

        // act
        final ModelRegistry support = EngineSupport.buildAiModelDefinitionSupport(
                Collections.singletonList(model(absolute)), directory.apply(tempDir));

        // assert
        assertThat(support.definition("model").getModelPath(), is(absolute));
    }

    @Test
    public void modelPath_relativeBelowAnAbsoluteDirectory_becomesAbsolute() throws Exception {
        // arrange
        final File modelDirectory = tempDir.resolve("models").toFile();

        // act
        final String resolved = EngineSupport.buildAiModelDefinitionSupport(
                        Collections.singletonList(model("sub/model.gguf")), modelDirectory)
                .definition("model")
                .getModelPath();

        // assert
        assertThat(resolved, is(new File(modelDirectory, "sub/model.gguf").getPath()));
        assertThat(new File(resolved).isAbsolute(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="templateFile (buildPromptSupport)">
    @ParameterizedTest
    @MethodSource("directories")
    public void templateFile_absolute_isReadAsItIs(final Function<Path, @Nullable File> directory) throws Exception {
        // arrange
        final List<PromptDefinition> prompts = Collections.singletonList(promptFile(absolutePrompt.toString()));

        // act, assert
        assertThat(
                EngineSupport.buildPromptSupport(prompts, directory.apply(tempDir), null)
                        .systemPrompt("file-body-java"),
                is(PROMPT));
    }

    @Test
    public void templateFile_relativeBelowAnAbsoluteDirectory_isReadFromThere() throws Exception {
        // arrange
        final List<PromptDefinition> prompts = Collections.singletonList(promptFile("file-body-java.md"));

        // act, assert
        assertThat(
                EngineSupport.buildPromptSupport(
                                prompts, absolutePrompt.getParent().toFile(), null)
                        .systemPrompt("file-body-java"),
                is(PROMPT));
    }
    // </editor-fold>
}
