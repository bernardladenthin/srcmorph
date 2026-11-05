// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ladenthin.srcmorph.config.AiFactDefinitionLoader;
import net.ladenthin.srcmorph.config.AiFactDefinitionSupport;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiFieldGenerationSelector;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.config.AiOversizeStrategy;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.AiPromptDefinition;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.prompt.AiPromptTemplateLoader;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import net.ladenthin.srcmorph.provider.GgufModelInfo;
import net.ladenthin.srcmorph.provider.GgufModelInspector;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfig;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfigFactory;
import net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Package-private helpers shared by at least two of the {@code engine} package's per-phase engines.
 * Anything used by only one engine stays local to that engine class instead.
 */
final class EngineSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineSupport.class);

    /** Scheme prefix of a plain-HTTP {@code baseUrl} (a local OpenAI-compatible server). */
    private static final String HTTP_SCHEME = "http://";

    /** Scheme prefix of an HTTPS {@code baseUrl}. */
    private static final String HTTPS_SCHEME = "https://";

    private EngineSupport() {
        // utility class — not instantiable
    }

    /**
     * Resolves the configured subtree strings against {@code basePath}. Used by {@link GenerateEngine} and
     * {@link AggregatePackagesEngine}. A subtree that another one holds, or that is listed twice, is walked
     * by that one already and is left out, so no file is collected twice.
     *
     * @param basePath absolute, normalised project base directory
     * @param subtrees configured subtree strings, or {@code null}/empty for none
     * @return list of resolved subtree paths, none inside another; empty if none are configured
     * @throws SrcMorphException if a subtree does not exist, or lies outside {@code basePath} (another
     *                           drive included), where its output would leave the output directory
     */
    static List<Path> resolveSubtrees(final Path basePath, final @Nullable List<String> subtrees)
            throws SrcMorphException {
        if (subtrees == null || subtrees.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Path> resolved = new ArrayList<>(subtrees.size());

        for (final String subtree : subtrees) {
            final Path path = basePath.resolve(subtree).normalize();
            if (!path.startsWith(basePath)) {
                throw new SrcMorphException("Subtree '" + subtree + "' resolves to " + path
                        + ", outside the base directory " + basePath + "; a subtree must lie inside it");
            }
            if (!path.toFile().isDirectory()) {
                throw new SrcMorphException("Subtree '" + subtree + "' does not exist: " + path);
            }
            addOutermost(resolved, path);
        }

        return resolved;
    }

    /**
     * Adds a subtree unless one already added holds it, and drops those it holds.
     *
     * @param resolved the subtrees so far, changed in place
     * @param path     the subtree to add
     */
    private static void addOutermost(final List<Path> resolved, final Path path) {
        for (final Path other : resolved) {
            if (path.startsWith(other)) {
                LOGGER.info("Subtree {} lies in subtree {}; its files are collected once", path, other);
                return;
            }
        }
        for (final Iterator<Path> iterator = resolved.iterator(); iterator.hasNext(); ) {
            final Path other = iterator.next();
            if (other.startsWith(path)) {
                LOGGER.info("Subtree {} lies in subtree {}; its files are collected once", other, path);
                iterator.remove();
            }
        }
        resolved.add(path);
    }

    /**
     * Builds an {@link AiPromptSupport} from the configured prompt definitions, first reading every
     * {@code templateFile} ({@link AiPromptTemplateLoader}, relative to {@code profileDirectory}), and
     * translating a misconfiguration into a {@link SrcMorphException} so the caller reports it as a
     * configuration error: a missing {@code key}/{@code template} ({@link NullPointerException}), both
     * {@code template} and {@code templateFile} or an empty template file
     * ({@link IllegalArgumentException}), a template file that cannot be read ({@link IOException}).
     *
     * @param promptDefinitions the configured prompt definitions, or {@code null} for none
     * @param profileDirectory  directory relative template files and the prompts named by key are
     *                          resolved against, or {@code null} for the working directory and no lookup
     *                          by key
     * @param fieldGenerations  the rules whose prompt keys (and map and reduce prompt keys) are looked up in
     *                          the profile directory when no definition has them, or {@code null} for none
     * @return prompt support instance backed by the configured definitions
     * @throws SrcMorphException if any prompt definition is misconfigured or its file cannot be read
     */
    static AiPromptSupport buildPromptSupport(
            final @Nullable List<AiPromptDefinition> promptDefinitions,
            final @Nullable File profileDirectory,
            final @Nullable List<AiFieldGenerationConfig> fieldGenerations)
            throws SrcMorphException {
        final List<@Nullable String> usedKeys = new ArrayList<>();
        if (fieldGenerations != null) {
            for (final AiFieldGenerationConfig rule : fieldGenerations) {
                // a null key (a skip rule's prompt, an unset map or reduce prompt) is ignored by the loader
                usedKeys.add(rule.getPromptKey());
                usedKeys.add(rule.getMapPromptKey());
                usedKeys.add(rule.getReducePromptKey());
            }
        }
        try {
            return new AiPromptSupport(new AiPromptTemplateLoader(profileDirectory).load(promptDefinitions, usedKeys));
        } catch (final NullPointerException | IllegalArgumentException | IOException e) {
            throw new SrcMorphException("Invalid configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Builds an {@link AiModelDefinitionSupport} from the configured AI model definitions, translating a
     * misconfiguration ({@link NullPointerException} from a missing {@code key}) into a
     * {@link SrcMorphException} so the caller reports it as a configuration error.
     *
     * @param aiDefinitions  the configured AI model definitions, or {@code null} for none
     * @param modelDirectory directory relative model paths are resolved against, or {@code null}
     * @return model definition support instance backed by the configured definitions
     * @throws SrcMorphException if any AI definition is missing a required field
     */
    static AiModelDefinitionSupport buildAiModelDefinitionSupport(
            final @Nullable List<AiModelDefinition> aiDefinitions, final @Nullable File modelDirectory)
            throws SrcMorphException {
        try {
            return new AiModelDefinitionSupport(aiDefinitions, modelDirectory);
        } catch (final NullPointerException e) {
            throw new SrcMorphException("Invalid configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the one rule of the package phase: its prompt and model. A condition, priority or fallback
     * flag means nothing here, with no files to route.
     *
     * @param phase            the goal, named in the error
     * @param cliKey           the configuration file's key for the phase's rules, named in the error
     * @param fieldGenerations the phase's rules
     * @return the rule
     * @throws SrcMorphException if not exactly one rule is given, or it names no prompt or no model
     */
    static AiFieldGenerationConfig requiredPhaseRule(
            final String phase, final String cliKey, final @Nullable List<AiFieldGenerationConfig> fieldGenerations)
            throws SrcMorphException {
        return checkedPhaseRule(phase, cliKey, fieldGenerations, "exactly one rule");
    }

    /**
     * Returns the rule of the project phase, which runs without one (a deterministic index, no
     * overview) or with one.
     *
     * @param phase            the goal, named in the error
     * @param cliKey           the configuration file's key for the phase's rules, named in the error
     * @param fieldGenerations the phase's rules
     * @return the rule, or {@code null} when none is given
     * @throws SrcMorphException if more than one rule is given, or it names no prompt or no model
     */
    static @Nullable AiFieldGenerationConfig optionalPhaseRule(
            final String phase, final String cliKey, final @Nullable List<AiFieldGenerationConfig> fieldGenerations)
            throws SrcMorphException {
        if (fieldGenerations == null || fieldGenerations.isEmpty()) {
            return null;
        }
        return checkedPhaseRule(phase, cliKey, fieldGenerations, "at most one rule");
    }

    private static AiFieldGenerationConfig checkedPhaseRule(
            final String phase,
            final String cliKey,
            final @Nullable List<AiFieldGenerationConfig> fieldGenerations,
            final String takes)
            throws SrcMorphException {
        if (fieldGenerations == null || fieldGenerations.size() != 1) {
            throw new SrcMorphException(phase + " takes " + takes + " (its prompt and model), "
                    + (fieldGenerations == null ? 0 : fieldGenerations.size()) + " given; give the phase its own: "
                    + cliKey + " in the configuration file");
        }
        final AiFieldGenerationConfig rule = fieldGenerations.get(0);
        if (rule == null || isBlank(rule.getPromptKey()) || isBlank(rule.getAiDefinitionKey())) {
            throw new SrcMorphException("The rule of " + phase + " needs a promptKey and an aiDefinitionKey");
        }
        if (rule.isSkip()) {
            throw new SrcMorphException("The rule of " + phase + " cannot be a skip rule: " + rule);
        }
        // Unset, a phase reads all of an input over its window: the input is the child summaries, and a
        // part left out (or a failure) would drop a whole subtree from the summary above. A copy, so the
        // configuration stays as it was given.
        final AiFieldGenerationConfig effective = rule.copy();
        if (isBlank(effective.getOnOversize())) {
            effective.setOnOversize(AiOversizeStrategy.MAP_REDUCE.configValue());
        }
        try {
            new AiFieldGenerationSelector().validateRule(effective);
        } catch (final IllegalArgumentException e) {
            throw new SrcMorphException("Invalid rule of " + phase + ": " + e.getMessage(), e);
        }
        return effective;
    }

    /**
     * Resolves each rule's {@code factsKey} to its group (a definition or {@code <profileDirectory>/<key>.tsv}),
     * copying the counters onto the rule's facts, for the file rules and the phases' rule alike.
     *
     * @param config the run configuration (its fact definitions and profile directory)
     * @param rules  the rules, changed in place
     * @throws SrcMorphException if a key is found nowhere, a group is malformed, or a rule names its facts
     *                           twice
     */
    static void resolveFactsKeys(final SrcMorphConfiguration config, final List<AiFieldGenerationConfig> rules)
            throws SrcMorphException {
        try {
            final List<@Nullable String> usedKeys = new ArrayList<>(rules.size());
            for (final AiFieldGenerationConfig rule : rules) {
                usedKeys.add(rule.getFactsKey());
            }
            new AiFactDefinitionSupport(new AiFactDefinitionLoader(config.getProfileDirectory())
                            .load(config.getFactDefinitions(), usedKeys))
                    .resolveFactsKeys(rules);
        } catch (final IllegalArgumentException | NullPointerException | IOException e) {
            throw new SrcMorphException("Invalid factDefinitions/factsKey configuration: " + e.getMessage(), e);
        }
    }

    private static boolean isBlank(final @Nullable String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Resolves the {@link LlamaCppJniConfig} for one specific {@link AiModelDefinition}, identified by its
     * key. Used by {@link GenerateEngine} (one provider per routing group), {@link CalibrateEngine}
     * (one provider per calibrated model) and the aggregation engines (the model of their one rule).
     *
     * <p>Takes no {@link SrcMorphConfiguration}: everything this needs comes from the named model
     * definition. The run configuration used to contribute the (never-read) native library path; with
     * that gone, passing it here would be an unused parameter.</p>
     *
     * @param modelDefinitionSupport model lookup built from {@link SrcMorphConfiguration#getAiDefinitions()}
     * @param aiDefinitionKey        the {@link AiModelDefinition} key
     * @return the fully populated llama.cpp configuration for that definition
     * @throws IllegalArgumentException if {@code aiDefinitionKey} matches no registered definition
     */
    static LlamaCppJniConfig resolveLlamaCppJniConfig(
            final AiModelDefinitionSupport modelDefinitionSupport, final String aiDefinitionKey) {
        final AiGenerationConfig modelConfig = modelDefinitionSupport.getConfig(aiDefinitionKey);
        return LlamaCppJniConfigFactory.fromGenerationConfig(modelConfig);
    }

    /**
     * Fails the run when the model file of a routed {@code aiDefinitions} entry does not exist.
     *
     * <p>Without this, a typo in a {@code modelPath} survives the entire plan phase — the walk, the
     * classification, the rendered plan — and only dies inside the native loader. With several model
     * groups that happens <em>after</em> the earlier groups have already generated, so the failure can
     * arrive an hour into a run for a mistake that was visible before it started. This joins the other
     * fail-fast checks the engines already run (missing {@code fieldGenerations}, rule-set
     * validation, unmatched files, over-window files).
     *
     * <p><strong>Gated on the provider on purpose.</strong> Only {@code llamacpp-jni} loads a GGUF;
     * the {@code mock} provider ignores {@code modelPath} entirely, and every shipped example config
     * sets a deliberately non-existent {@code "unused-with-mock-provider.gguf"}. An ungated check
     * would red those examples, their binding tests, the CLI end-to-end test and the jar smoke
     * ({@code smoke-jar}) that gates every release. The gate is per model: a definition's own
     * {@code provider} overrides the run's, so an {@code openai-compatible} model needs no file even
     * in a {@code llamacpp-jni} run, and instead must name an {@code http(s)} {@code baseUrl}.
     *
     * @param generationProvider     the configured provider key
     * @param modelDefinitionSupport model lookup built from the configured {@code aiDefinitions}
     * @param fieldGenerations       the routing rules, already validated
     * @throws SrcMorphException if a routed model's file is missing or its path is unset
     */
    static void validateRoutedModelPaths(
            final String generationProvider,
            final AiModelDefinitionSupport modelDefinitionSupport,
            final List<AiFieldGenerationConfig> fieldGenerations)
            throws SrcMorphException {
        for (final AiFieldGenerationConfig rule : fieldGenerations) {
            if (rule == null || rule.isSkip()) {
                continue;
            }
            final String aiDefinitionKey = rule.getAiDefinitionKey();
            if (aiDefinitionKey == null) {
                continue;
            }
            final AiGenerationConfig modelConfig = definedModel(modelDefinitionSupport, rule, aiDefinitionKey);
            final String provider =
                    AiGenerationProviderFactory.effectiveProvider(generationProvider, modelConfig.getProvider());
            if (AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE.equals(provider)) {
                validateRemoteModel(aiDefinitionKey, modelConfig);
            } else if (AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI.equals(provider)) {
                validateLocalModel(aiDefinitionKey, modelConfig);
            } else if (provider != null
                    && !isBlank(provider)
                    && !AiGenerationProviderFactory.PROVIDER_MOCK.equals(provider)) {
                // otherwise the run would fail at this model's group, after the earlier groups generated
                throw new SrcMorphException("Model '" + aiDefinitionKey + "' has the unknown provider '" + provider
                        + "'; a provider is " + AiGenerationProviderFactory.PROVIDER_MOCK + ", "
                        + AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI + " or "
                        + AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE);
            }
        }
    }

    /**
     * Fails when a rule names a model no {@code aiDefinitions} entry defines, naming the rule and the key;
     * the lookup itself would end the run with an {@link IllegalArgumentException} the surfaces do not
     * report as a configuration error.
     *
     * @param modelDefinitionSupport model lookup built from the configured {@code aiDefinitions}
     * @param fieldGenerations       the rules
     * @throws SrcMorphException if a rule (other than a skip rule) names an undefined model
     */
    static void requireDefinedModels(
            final AiModelDefinitionSupport modelDefinitionSupport, final List<AiFieldGenerationConfig> fieldGenerations)
            throws SrcMorphException {
        for (final AiFieldGenerationConfig rule : fieldGenerations) {
            if (rule == null || rule.isSkip()) {
                continue;
            }
            final String aiDefinitionKey = rule.getAiDefinitionKey();
            if (aiDefinitionKey != null) {
                definedModel(modelDefinitionSupport, rule, aiDefinitionKey);
            }
        }
    }

    private static AiGenerationConfig definedModel(
            final AiModelDefinitionSupport modelDefinitionSupport,
            final AiFieldGenerationConfig rule,
            final String aiDefinitionKey)
            throws SrcMorphException {
        try {
            return modelDefinitionSupport.getConfig(aiDefinitionKey);
        } catch (final IllegalArgumentException e) {
            final String id = rule.getId();
            throw new SrcMorphException(
                    "The rule '" + (id != null ? id : rule.getPromptKey()) + "' names the model '" + aiDefinitionKey
                            + "', which no aiDefinition defines",
                    e);
        }
    }

    /**
     * Checks an {@code openai-compatible} model: its {@code baseUrl} must be an {@code http(s)} URL, a
     * named {@code apiKeyEnv} must be set, and its key must not go in cleartext to another machine.
     *
     * @param aiDefinitionKey the definition's key, named in the error
     * @param modelConfig     the definition's resolved configuration
     * @throws SrcMorphException if the URL is not {@code http(s)}, or the key is missing or would go out
     *                           in cleartext
     */
    private static void validateRemoteModel(final String aiDefinitionKey, final AiGenerationConfig modelConfig)
            throws SrcMorphException {
        final String baseUrl = modelConfig.getBaseUrl().trim();
        if (!baseUrl.startsWith(HTTP_SCHEME) && !baseUrl.startsWith(HTTPS_SCHEME)) {
            throw new SrcMorphException("aiDefinition '" + aiDefinitionKey + "' uses the "
                    + AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE
                    + " provider but its baseUrl is not an http(s) URL: '"
                    + OpenAiCompatibleAiGenerationProvider.withoutUserInfo(baseUrl) + "'");
        }
        final String keyProblem = OpenAiCompatibleAiGenerationProvider.keyProblem(baseUrl, modelConfig.getApiKeyEnv());
        if (keyProblem != null) {
            throw new SrcMorphException(
                    "aiDefinition '" + aiDefinitionKey + "': " + keyProblem + " (checked before any request is sent)");
        }
    }

    /**
     * Checks a {@code llamacpp-jni} model: the file must exist and be a GGUF that fits the configuration.
     *
     * @param aiDefinitionKey the definition's key, named in the error
     * @param modelConfig     the definition's resolved configuration
     * @throws SrcMorphException if the file is missing, not a GGUF, or not readable
     */
    private static void validateLocalModel(final String aiDefinitionKey, final AiGenerationConfig modelConfig)
            throws SrcMorphException {
        final String modelPath = modelConfig.getModelPath();
        if (modelPath == null || !new File(modelPath).isFile()) {
            throw new SrcMorphException("Model file for aiDefinition '" + aiDefinitionKey
                    + "' does not exist: " + modelPath
                    + " (checked before any model is loaded; fix the modelPath, or set"
                    + " generationProvider to mock for a model-free run)");
        }
        validateAgainstTheModelItself(aiDefinitionKey, modelConfig, Paths.get(modelPath));
    }

    /**
     * Checks the configuration against what the GGUF file itself declares, still without loading it.
     *
     * <p>Existing on disk is a weak check. The two things it misses are both silent:</p>
     *
     * <ul>
     *   <li><b>The file is not a GGUF.</b> A truncated download, a Git LFS pointer, or the wrong file
     *       entirely passes {@code isFile()} and dies much later inside the native loader — after the
     *       earlier model groups of a multi-model run have already generated. Fails here instead.</li>
     *   <li><b>{@code contextSize} exceeds what the model declares.</b> This one is worse than it looks,
     *       because <em>every</em> number the plan phase produces is derived from {@code contextSize}:
     *       {@code maxInputChars}, the oversize/chunking decision, and the time estimate. Point the
     *       default 32768 at a 4096-context model and the plan is wrong by 8&times; before a single
     *       token is generated. A warning rather than an error because the run still proceeds &mdash;
     *       but note what actually happens: llama.cpp <em>caps the slot</em> to the model's trained
     *       context, so a file the plan calls "fits" can still die mid-run once earlier files have
     *       generated. (An earlier version of this text offered RoPE scaling as the escape hatch. It is
     *       not one: srcmorph exposes no knob for it, so the only fix is to lower {@code contextSize}.)
     *       </li>
     * </ul>
     *
     * @param aiDefinitionKey the model key, for the message
     * @param modelConfig     the resolved model configuration
     * @param modelFile       the model file, already known to exist
     * @throws SrcMorphException if the file is not a readable GGUF
     */
    private static void validateAgainstTheModelItself(
            final String aiDefinitionKey, final AiGenerationConfig modelConfig, final Path modelFile)
            throws SrcMorphException {
        final GgufModelInfo info = new GgufModelInspector().inspect(modelFile);
        if (!info.readable()) {
            throw new SrcMorphException("Model file for aiDefinition '" + aiDefinitionKey + "' is not a readable"
                    + " GGUF: " + modelFile + " (" + info.failure() + "). A truncated download or a Git LFS"
                    + " pointer looks like a perfectly good file to an existence check.");
        }
        final long declared = info.contextLength();
        if (declared != GgufModelInfo.UNKNOWN_CONTEXT_LENGTH && modelConfig.getContextSize() > declared) {
            LOGGER.warn(
                    "aiDefinition '{}' sets contextSize {} but {} declares only {}. llama.cpp caps the"
                            + " slot to the model's trained context, so the runtime window really is {} --"
                            + " while the plan's maxInputChars, oversize handling and time estimate are all"
                            + " derived from the configured {}. A file the plan calls 'fits' can therefore"
                            + " still die mid-run. Lower contextSize to match.",
                    aiDefinitionKey,
                    modelConfig.getContextSize(),
                    info.modelName().isEmpty() ? modelFile.toString() : info.modelName(),
                    declared,
                    declared,
                    modelConfig.getContextSize());
        }
    }
}
