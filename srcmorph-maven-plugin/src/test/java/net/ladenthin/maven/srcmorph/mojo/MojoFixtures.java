// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Configuration files and parameter injection for the mojo tests, as Maven would inject them. */
final class MojoFixtures {

    /**
     * A configuration on the {@code mock} provider: one inline prompt, one model, one fallback file
     * rule, and a package and a project rule on the same prompt and model.
     */
    static final String MOCK_CONFIGURATION = "packageRules:\n"
            + "  - promptKey: file-body\n"
            + "    aiDefinitionKey: mock-model\n"
            + "projectRules:\n"
            + "  - promptKey: file-body\n"
            + "    aiDefinitionKey: mock-model\n"
            + "srcMorph:\n"
            + "  generationProvider: mock\n"
            + "  promptDefinitions:\n"
            + "    - key: file-body\n"
            + "      template: \"Summarize:\\n%s\"\n"
            + "  aiDefinitions:\n"
            + "    - key: mock-model\n"
            + "      modelPath: mock.gguf\n"
            + "      charsPerToken: 0\n"
            + "  rules:\n"
            + "    - promptKey: file-body\n"
            + "      aiDefinitionKey: mock-model\n"
            + "      fallback: true\n";

    private MojoFixtures() {
        // no-op
    }

    /**
     * Writes {@code srcmorph.yaml} into a project directory.
     *
     * @param projectDirectory the directory
     * @param content          the file's content
     * @return the file
     * @throws IOException if it cannot be written
     */
    static Path writeConfiguration(final Path projectDirectory, final String content) throws IOException {
        Files.createDirectories(projectDirectory);
        return Files.write(projectDirectory.resolve("srcmorph.yaml"), content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Writes one Java source below {@code src/main/java} of a project directory.
     *
     * @param projectDirectory the directory
     * @throws IOException if it cannot be written
     */
    static void writeSource(final Path projectDirectory) throws IOException {
        final Path source = projectDirectory.resolve("src/main/java/com/example/Foo.java");
        Files.createDirectories(source.getParent());
        Files.write(source, "class Foo {}\n".getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Injects what Maven injects into every goal: the configuration file, the project directory and
     * its source directory, the project's name and version.
     *
     * @param mojo             the goal
     * @param projectDirectory the project directory
     * @param <T>              the goal's type
     * @return the goal
     */
    static <T extends AbstractSrcMorphMojo> T configured(final T mojo, final Path projectDirectory) {
        mojo.configurationFile = projectDirectory.resolve("srcmorph.yaml").toFile();
        mojo.projectDirectory = projectDirectory.toFile();
        mojo.sourceDirectory = projectDirectory.resolve("src/main/java").toFile();
        mojo.projectName = "fixture-project";
        mojo.projectVersion = "1.0.0-fixture";
        return mojo;
    }

    /**
     * Sets a field by name, walking up the class hierarchy, as Maven's plugin framework injects a
     * private {@code @Parameter}.
     *
     * @param target the goal
     * @param name   the field
     * @param value  the value
     */
    static void setField(final Object target, final String name, final Object value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                final Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (final NoSuchFieldException ignored) {
                type = type.getSuperclass();
            } catch (final IllegalAccessException e) {
                throw new IllegalStateException("cannot set " + name, e);
            }
        }
        throw new IllegalStateException("no such field: " + name);
    }
}
