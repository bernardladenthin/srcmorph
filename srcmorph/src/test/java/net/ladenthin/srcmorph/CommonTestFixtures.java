// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;

/**
 * Shared test fixture factory methods used across multiple test classes.
 *
 * <p>Methods in this class eliminate duplication of prompt definitions and field
 * generation configs that appear identically in multiple test classes.</p>
 */
public class CommonTestFixtures {

    /**
     * Prompt key for the file-level body prompt, used in file summarizer and source file indexer tests.
     */
    public static final String PROMPT_KEY_FILE_BODY = "file-body";

    /**
     * AI definition key used for the default model definition in tests.
     * Matches the prompt key convention so that a single definition suffices for most tests.
     */
    public static final String AI_DEFINITION_KEY_DEFAULT = "default";

    /**
     * Prompt key for the package-level body prompt, used in package indexer and summarizer tests.
     */
    public static final String PROMPT_KEY_PACKAGE_BODY = "package-body";

    /**
     * Creates the standard file-level prompt definitions used in file summarizer,
     * source file indexer, and llama JNI provider tests.
     *
     * <p>Provides a single {@code file-body} prompt template that produces the complete
     * body text (summary and any keywords naturally embedded in the text).</p>
     *
     * @return list with one prompt definition for file-level summarization
     */
    public static List<PromptDefinition> createFilePromptDefinitions() {
        final PromptDefinition bodyPrompt = new PromptDefinition();
        bodyPrompt.setKey(PROMPT_KEY_FILE_BODY);
        bodyPrompt.setTemplate("Summarize this Java file and include relevant keywords in your response.\n" + "\n"
                + "File: %s\n"
                + "\n"
                + "Source:\n"
                + "%s\n");

        return Arrays.asList(bodyPrompt);
    }

    /**
     * Creates the standard file-level rules.
     *
     * <p>Used in file summarizer and source file indexer tests.</p>
     *
     * @return list with one rule for the body
     */
    public static List<RoutingRule> createFileRules() {
        return Arrays.asList(createFileFieldGeneration());
    }

    /**
     * Creates the one file-level rule of {@link #createFileRules()}.
     *
     * @return the {@code file-body} rule on the default model
     */
    public static RoutingRule createFileFieldGeneration() {
        return createFieldConfig(PROMPT_KEY_FILE_BODY);
    }

    /**
     * Creates the standard package-level prompt definitions used in package indexer and summarizer tests.
     *
     * <p>Provides a single {@code package-body} prompt template that produces the complete
     * body text (summary and any keywords naturally embedded in the text).</p>
     *
     * @return list with one prompt definition for package-level summarization
     */
    public static List<PromptDefinition> createPackagePromptDefinitions() {
        final PromptDefinition bodyPrompt = new PromptDefinition();
        bodyPrompt.setKey(PROMPT_KEY_PACKAGE_BODY);
        bodyPrompt.setTemplate("Summarize this Java package and include relevant keywords in your response.\n" + "\n"
                + "File: %s\n"
                + "\n"
                + "Source:\n"
                + "%s\n");

        return Arrays.asList(bodyPrompt);
    }

    /**
     * Creates the standard package-level rules.
     *
     * <p>Used in package indexer and summarizer tests.</p>
     *
     * @return list with one rule for the body
     */
    public static List<RoutingRule> createPackageRules() {
        return Arrays.asList(createPackageFieldGeneration());
    }

    /**
     * Creates the one package-level rule of {@link #createPackageRules()}.
     *
     * @return the {@code package-body} rule on the default model
     */
    public static RoutingRule createPackageFieldGeneration() {
        return createFieldConfig(PROMPT_KEY_PACKAGE_BODY);
    }

    /**
     * Creates the standard default {@link ModelDefinition} list for tests.
     *
     * <p>Provides a single definition keyed {@link #AI_DEFINITION_KEY_DEFAULT} that uses
     * all {@link ModelDefinition} default values. Tests that need custom values
     * (e.g. different {@code temperature}) should build their own list.</p>
     *
     * @return list with one default model definition
     */
    public static List<ModelDefinition> createDefaultAiModelDefinitions() {
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey(AI_DEFINITION_KEY_DEFAULT);
        // Disable automatic maxInputChars calculation in tests so that test assertions
        // about log message counts and content are not affected by the calculation log output.
        definition.setCharsPerToken(0);
        return Arrays.asList(definition);
    }

    /**
     * Creates an {@link ModelRegistry} backed by the default definition list.
     *
     * @return model definition support using the default test definitions
     * @see #createDefaultAiModelDefinitions()
     */
    public static ModelRegistry createDefaultAiModelDefinitionSupport() {
        return new ModelRegistry(createDefaultAiModelDefinitions());
    }

    /**
     * Creates a single {@link RoutingRule} with the given prompt key and the
     * default AI definition key {@link #AI_DEFINITION_KEY_DEFAULT}.
     *
     * @param promptKey key that identifies the prompt template in the prompt support
     * @return a fully configured {@link RoutingRule}
     */
    private static RoutingRule createFieldConfig(final String promptKey) {
        final RoutingRule field = new RoutingRule();
        field.setPromptKey(promptKey);
        field.setAiDefinitionKey(AI_DEFINITION_KEY_DEFAULT);
        return field;
    }

    private CommonTestFixtures() {
        // utility class — not instantiable
    }

    /**
     * Takes the permission to list a directory away until the returned handle is closed: on POSIX its
     * mode bits (the test does not run as root), on Windows an access control entry that denies the
     * current user the listing.
     */
    public static AutoCloseable unlistable(final Path directory) throws Exception {
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            Files.setPosixFilePermissions(directory, Collections.<PosixFilePermission>emptySet());
            return () -> Files.setPosixFilePermissions(directory, PosixFilePermissions.fromString("rwx------"));
        }
        final AclFileAttributeView view = Files.getFileAttributeView(directory, AclFileAttributeView.class);
        assumeTrue(view != null, "the file system supports neither POSIX permissions nor access control lists");
        final List<AclEntry> before = view.getAcl();
        final UserPrincipal user = directory
                .getFileSystem()
                .getUserPrincipalLookupService()
                .lookupPrincipalByName(System.getProperty("user.name"));
        final List<AclEntry> denied = new ArrayList<>();
        denied.add(AclEntry.newBuilder()
                .setType(AclEntryType.DENY)
                .setPrincipal(user)
                .setPermissions(AclEntryPermission.LIST_DIRECTORY)
                .build());
        denied.addAll(before);
        view.setAcl(denied);
        return () -> view.setAcl(before);
    }
}
