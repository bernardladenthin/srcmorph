// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.io.File;
import java.util.List;
import lombok.ToString;
import net.ladenthin.srcmorph.support.AiGeneratorVersion;
import org.jspecify.annotations.Nullable;

/**
 * Root mutable JavaBean holding every parameter a {@code srcmorph} run needs: the {@code srcMorph}
 * section of the configuration file the CLI and the Maven plugin read
 * ({@link SrcMorphConfigurationFileLoader}), or built in plain Java code. Its field names are the keys
 * of that file.
 *
 * <p>The plugin fills what the file leaves unset from the Maven project ({@code baseDirectory},
 * {@code subtrees}, {@code projectName}, {@code pluginVersion}); see the plugin module's
 * {@code AbstractSrcMorphMojo}.</p>
 *
 * <p>Deliberately excluded: the per-goal {@code skip}/{@code skipFile}/{@code skipPackage}/
 * {@code skipProject} flags. Those are a Maven lifecycle concern (whether an execution runs at all) and
 * stay mojo-side; an engine constructed from this configuration always executes when asked.</p>
 *
 * <p><strong>Note:</strong> this class must remain a mutable JavaBean with setters so that the
 * configuration file (a Jackson {@code ObjectMapper}/{@code YAMLMapper}) and a plain caller populate it
 * the same way.</p>
 */
@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
@ToString
public class SrcMorphConfiguration {

    /** Default output directory (relative to {@link #baseDirectory}) when none is configured. */
    public static final String DEFAULT_OUTPUT_DIRECTORY = "src/site/ai";

    /**
     * How many characters of a file a {@code contentMatches} condition reads unless
     * {@link #getContentHeadChars() contentHeadChars} says otherwise: enough for the header of a Kubernetes manifest, an OpenAPI
     * document or a Camel route, and cheap for a large file.
     */
    public static final int DEFAULT_CONTENT_HEAD_CHARS = 16_384;

    /** Default AI generation provider name when none is configured. */
    public static final String DEFAULT_GENERATION_PROVIDER = "mock";

    /** Default AI summarisation logic version when none is configured. */
    public static final String DEFAULT_AI_VERSION = "0.0.0";

    /** Creates a new {@link SrcMorphConfiguration} with every default applied. */
    public SrcMorphConfiguration() {
        // no-op
    }

    private File baseDirectory;
    private File outputDirectory = new File(DEFAULT_OUTPUT_DIRECTORY);
    private boolean force;
    private AiRegenerateOn regenerateOn = new AiRegenerateOn();
    private @Nullable List<String> subtrees;
    private @Nullable List<String> excludes;
    private @Nullable List<String> fileExtensions;
    private @Nullable List<String> fileNames;
    private @Nullable List<String> fileInterpreters;
    private int contentHeadChars = DEFAULT_CONTENT_HEAD_CHARS;
    private long minFileSizeBytes;
    private long maxFileSizeBytes;
    private boolean planOnly;
    private String generationProvider = DEFAULT_GENERATION_PROVIDER;
    private @Nullable List<AiPromptDefinition> promptDefinitions;
    private @Nullable List<AiModelDefinition> aiDefinitions;
    private @Nullable File modelDirectory;
    private @Nullable File profileDirectory;
    private @Nullable File runRecordFile;
    private @Nullable List<AiFieldGenerationConfig> fieldGenerations;
    private @Nullable List<AiFactDefinition> factDefinitions;
    private String pluginVersion = new AiGeneratorVersion().version();
    private String aiVersion = DEFAULT_AI_VERSION;
    private @Nullable String projectName;

    /**
     * Returns the project base directory.
     *
     * @return the base directory
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Sets the project base directory.
     *
     * @param baseDirectory the base directory
     */
    public void setBaseDirectory(final File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns the directory into which {@code .ai.md} files are written.
     *
     * @return the output directory; defaults to {@link #DEFAULT_OUTPUT_DIRECTORY}
     */
    public File getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Sets the directory into which {@code .ai.md} files are written.
     *
     * @param outputDirectory the output directory
     */
    public void setOutputDirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Returns whether every summary is rewritten, whatever {@link #getRegenerateOn()} says.
     *
     * @return {@code true} to always regenerate
     */
    public boolean isForce() {
        return force;
    }

    /**
     * Sets whether every summary is rewritten, whatever {@link #getRegenerateOn()} says.
     *
     * @param force {@code true} to always regenerate
     */
    public void setForce(final boolean force) {
        this.force = force;
    }

    /**
     * Returns which changes rewrite an existing summary, one switch per header field
     * ({@link AiRegenerateOn}); {@link #isForce() force} rewrites every summary regardless.
     *
     * @return the switches, every one on by default
     */
    public AiRegenerateOn getRegenerateOn() {
        return regenerateOn;
    }

    /**
     * Sets which changes rewrite an existing summary.
     *
     * @param regenerateOn the switches; {@code null} (an empty {@code regenerateOn:}) turns every one on
     */
    public void setRegenerateOn(final @Nullable AiRegenerateOn regenerateOn) {
        this.regenerateOn = regenerateOn != null ? regenerateOn : new AiRegenerateOn();
    }

    /**
     * Returns the source subdirectory paths (relative to {@link #baseDirectory}) that restrict processing.
     *
     * @return the configured subtrees, or {@code null} when every discovered source root is in scope
     */
    public @Nullable List<String> getSubtrees() {
        return subtrees;
    }

    /**
     * Sets the source subdirectory paths that restrict processing.
     *
     * @param subtrees the subtrees, or {@code null} for every discovered source root
     */
    public void setSubtrees(final @Nullable List<String> subtrees) {
        this.subtrees = subtrees;
    }

    /**
     * Returns the glob patterns (base-relative, {@code /} separators) for source files to skip.
     *
     * @return the configured excludes, or {@code null} when nothing is excluded
     */
    public @Nullable List<String> getExcludes() {
        return excludes;
    }

    /**
     * Sets the glob patterns for source files to skip.
     *
     * @param excludes the exclude globs, or {@code null} for none
     */
    public void setExcludes(final @Nullable List<String> excludes) {
        this.excludes = excludes;
    }

    /**
     * Returns the file extensions to index.
     *
     * @return the configured extensions, or {@code null} to use the engine's own default (typically
     *         {@code .java})
     */
    public @Nullable List<String> getFileExtensions() {
        return fileExtensions;
    }

    /**
     * Sets the file extensions to index.
     *
     * @param fileExtensions the extensions, or {@code null} to use the engine's own default
     */
    public void setFileExtensions(final @Nullable List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    /**
     * Returns the file names to index besides the extensions: globs over the name without its
     * directory, matched in any case ({@code Dockerfile}, {@code Jenkinsfile}, {@code Dockerfile.*}), for
     * files with no extension or one that {@link #getFileExtensions()} does not list.
     *
     * @return the globs, or {@code null} for none
     */
    public @Nullable List<String> getFileNames() {
        return fileNames;
    }

    /**
     * Sets the file names to index besides the extensions.
     *
     * @param fileNames globs over the file name, or {@code null} for none
     */
    public void setFileNames(final @Nullable List<String> fileNames) {
        this.fileNames = fileNames;
    }

    /**
     * Returns the interpreters whose scripts are indexed although they have no extension: a file
     * without one whose {@code #!} line names one of them, or a version of one
     * ({@link net.ladenthin.srcmorph.support.AiShebang}).
     *
     * @return the interpreter names (e.g. {@code bash}, {@code perl}), or {@code null} for none
     */
    public @Nullable List<String> getFileInterpreters() {
        return fileInterpreters;
    }

    /**
     * Sets the interpreters whose scripts without an extension are indexed.
     *
     * @param fileInterpreters the interpreter names, or {@code null} for none
     */
    public void setFileInterpreters(final @Nullable List<String> fileInterpreters) {
        this.fileInterpreters = fileInterpreters;
    }

    /**
     * Returns how many characters of a file the {@code contentMatches} and {@code interpreters}
     * conditions read.
     *
     * @return the characters; {@value #DEFAULT_CONTENT_HEAD_CHARS} by default
     */
    public int getContentHeadChars() {
        return contentHeadChars;
    }

    /**
     * Sets how many characters of a file the {@code contentMatches} and {@code interpreters} conditions
     * read: more for a file whose marker comes after a long license header, at the cost of reading more
     * of every file while planning.
     *
     * @param contentHeadChars the characters, at least 256 (a whole {@code #!} line)
     */
    public void setContentHeadChars(final int contentHeadChars) {
        this.contentHeadChars = contentHeadChars;
    }

    /**
     * Returns the exclusive lower file-size bound in bytes.
     *
     * @return the lower bound; {@code <= 0} disables it
     */
    public long getMinFileSizeBytes() {
        return minFileSizeBytes;
    }

    /**
     * Sets the exclusive lower file-size bound in bytes.
     *
     * @param minFileSizeBytes the lower bound; {@code <= 0} disables it
     */
    public void setMinFileSizeBytes(final long minFileSizeBytes) {
        this.minFileSizeBytes = minFileSizeBytes;
    }

    /**
     * Returns the inclusive upper file-size bound in bytes.
     *
     * @return the upper bound; {@code <= 0} means unlimited
     */
    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    /**
     * Sets the inclusive upper file-size bound in bytes.
     *
     * @param maxFileSizeBytes the upper bound; {@code <= 0} means unlimited
     */
    public void setMaxFileSizeBytes(final long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    /**
     * Returns whether a {@code generate} run stops after printing the routing plan, without loading any
     * model or generating anything.
     *
     * @return {@code true} to stop after planning
     */
    public boolean isPlanOnly() {
        return planOnly;
    }

    /**
     * Sets whether a {@code generate} run stops after printing the routing plan.
     *
     * @param planOnly {@code true} to stop after planning
     */
    public void setPlanOnly(final boolean planOnly) {
        this.planOnly = planOnly;
    }

    /**
     * Returns the name of the AI generation provider to use.
     *
     * @return the provider name ({@code mock}, {@code llamacpp-jni} or {@code openai-compatible}); a
     *         model definition's own {@code provider} overrides it for that model; defaults to
     *         {@link #DEFAULT_GENERATION_PROVIDER}
     */
    public String getGenerationProvider() {
        return generationProvider;
    }

    /**
     * Sets the name of the AI generation provider to use.
     *
     * @param generationProvider the provider name ({@code mock}, {@code llamacpp-jni} or
     *                           {@code openai-compatible})
     */
    public void setGenerationProvider(final String generationProvider) {
        this.generationProvider = generationProvider;
    }

    /**
     * Returns the prompt template definitions referenced by field generation configurations.
     *
     * @return the prompt definitions, or {@code null} when none are configured
     */
    public @Nullable List<AiPromptDefinition> getPromptDefinitions() {
        return promptDefinitions;
    }

    /**
     * Sets the prompt template definitions.
     *
     * @param promptDefinitions the prompt definitions, or {@code null} for none
     */
    public void setPromptDefinitions(final @Nullable List<AiPromptDefinition> promptDefinitions) {
        this.promptDefinitions = promptDefinitions;
    }

    /**
     * Returns the AI model definitions that pair a lookup key with a complete set of model parameters.
     *
     * @return the AI model definitions, or {@code null} when none are configured
     */
    public @Nullable List<AiModelDefinition> getAiDefinitions() {
        return aiDefinitions;
    }

    /**
     * Sets the AI model definitions.
     *
     * @param aiDefinitions the AI model definitions, or {@code null} for none
     */
    public void setAiDefinitions(final @Nullable List<AiModelDefinition> aiDefinitions) {
        this.aiDefinitions = aiDefinitions;
    }

    /**
     * Returns the directory a relative model path is resolved against: every {@code modelPath} of
     * {@link #aiDefinitions}. An absolute model path is used as it is, so
     * models outside this directory stay possible. Without it, a relative model path is relative to
     * the working directory of the JVM, as before.
     *
     * @return the model directory, or {@code null} when none is configured
     */
    public @Nullable File getModelDirectory() {
        return modelDirectory;
    }

    /**
     * Sets the directory a relative model path is resolved against.
     *
     * @param modelDirectory the model directory, or {@code null} to resolve relative model paths
     *                       against the working directory
     */
    public void setModelDirectory(final @Nullable File modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    /**
     * Returns the directory a relative {@code templateFile} of {@link #promptDefinitions} is resolved
     * against; an absolute one is used as it is. So configurations can share one profile directory
     * instead of a copy of every prompt each. Without it, a relative template file is relative to the
     * working directory of the JVM, as a model path is without {@link #modelDirectory}.
     *
     * @return the profile directory, or {@code null} when none is configured
     */
    public @Nullable File getProfileDirectory() {
        return profileDirectory;
    }

    /**
     * Sets the directory a relative prompt template file is resolved against.
     *
     * @param profileDirectory the profile directory, or {@code null} to resolve relative template files
     *                        against the working directory
     */
    public void setProfileDirectory(final @Nullable File profileDirectory) {
        this.profileDirectory = profileDirectory;
    }

    /**
     * Returns the file the {@code generate} run writes its record to (one entry per routed file: rule,
     * prompt, model, outcome, calls, tokens, finish reason, seconds, prompt and facts hashes; see
     * {@code net.ladenthin.srcmorph.engine.RunRecord}), or {@code null} for no record. A relative path
     * resolves against the configuration file's directory.
     *
     * @return the record file, or {@code null}
     */
    public @Nullable File getRunRecordFile() {
        return runRecordFile;
    }

    /**
     * Sets the file the {@code generate} run writes its record to.
     *
     * @param runRecordFile the record file, or {@code null} for no record
     */
    public void setRunRecordFile(final @Nullable File runRecordFile) {
        this.runRecordFile = runRecordFile;
    }

    /**
     * Returns the per-field AI generation configurations (routing rules) controlling which prompt and AI
     * definition each matched file uses.
     *
     * @return the field generation rules, or {@code null} when none are configured
     */
    public @Nullable List<AiFieldGenerationConfig> getFieldGenerations() {
        return fieldGenerations;
    }

    /**
     * Sets the per-field AI generation configurations (routing rules).
     *
     * @param fieldGenerations the field generation rules, or {@code null} for none
     */
    public void setFieldGenerations(final @Nullable List<AiFieldGenerationConfig> fieldGenerations) {
        this.fieldGenerations = fieldGenerations;
    }

    /**
     * Returns the reusable, named {@code factDefinitions} groups referenced from a rule's
     * {@code factsKey}.
     *
     * @return the fact definitions, or {@code null} when none are configured
     */
    public @Nullable List<AiFactDefinition> getFactDefinitions() {
        return factDefinitions;
    }

    /**
     * Sets the reusable, named {@code factDefinitions} groups.
     *
     * @param factDefinitions the fact definitions, or {@code null} for none
     */
    public void setFactDefinitions(final @Nullable List<AiFactDefinition> factDefinitions) {
        this.factDefinitions = factDefinitions;
    }

    /**
     * Returns the generator version recorded in every written {@code .ai.md} header ({@code G}): srcmorph's
     * own version ({@link AiGeneratorVersion}) unless the configuration names another.
     *
     * @return the generator version
     */
    public String getPluginVersion() {
        return pluginVersion;
    }

    /**
     * Sets the generator (plugin) version recorded in every written {@code .ai.md} header.
     *
     * @param pluginVersion the generator version
     */
    public void setPluginVersion(final String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    /**
     * Returns the AI summarisation logic version recorded in every written {@code .ai.md} header.
     *
     * @return the AI version; defaults to {@link #DEFAULT_AI_VERSION}
     */
    public String getAiVersion() {
        return aiVersion;
    }

    /**
     * Sets the AI summarisation logic version recorded in every written {@code .ai.md} header.
     *
     * @param aiVersion the AI version
     */
    public void setAiVersion(final String aiVersion) {
        this.aiVersion = aiVersion;
    }

    /**
     * Returns the project title recorded as the {@code aggregate-project} index title.
     *
     * @return the project name, or {@code null} to fall back to the engine's own default title
     */
    public @Nullable String getProjectName() {
        return projectName;
    }

    /**
     * Sets the project title recorded as the {@code aggregate-project} index title.
     *
     * @param projectName the project name, or {@code null} to use the engine's own default title
     */
    public void setProjectName(final @Nullable String projectName) {
        this.projectName = projectName;
    }
}
