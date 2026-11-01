// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinition;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProviderFactory;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfig;
import net.ladenthin.srcmorph.provider.LlamaCppJniConfigFactory;
import net.ladenthin.srcmorph.provider.OpenAiCompatibleAiGenerationProvider;
import net.ladenthin.srcmorph.provider.RemoteModel;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lists the models the configured OpenAI-compatible servers serve, so a configuration can name them:
 * {@code GET <baseUrl>/models} once per server, rendered by {@link ModelListReport} as a table and a
 * paste-ready {@code aiDefinitions} block.
 *
 * <p>Every {@code aiDefinition} whose provider (its own, or the run's {@code generationProvider}) is
 * {@code openai-compatible} names a server: its {@code baseUrl}, with the key its {@code apiKeyEnv}
 * names. A second definition on the same server with the same key is not asked again. The first
 * definition of a server is the pattern of that server's template entries. Definitions of the other
 * providers are skipped; when no definition names a server, the run fails.</p>
 *
 * <p>Sends no prompt and loads no model; a filter keeps the models whose id contains it, ignoring
 * case.</p>
 */
@ToString
public final class ListModelsEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListModelsEngine.class);

    /** Asks one server for its models; {@link OpenAiCompatibleAiGenerationProvider#listModels()} by default. */
    @FunctionalInterface
    interface ModelLister {

        /**
         * Lists the models of the server {@code config} names.
         *
         * @param config        the pattern definition's configuration ({@code baseUrl}, {@code apiKeyEnv})
         * @param promptSupport the run's prompt definitions
         * @return the server's models
         * @throws IOException if the server cannot be asked or its answer cannot be read
         */
        List<RemoteModel> list(LlamaCppJniConfig config, AiPromptSupport promptSupport) throws IOException;
    }

    private final SrcMorphConfiguration config;
    private final String filter;

    @ToString.Exclude
    private final ModelLister lister;

    /**
     * Creates the engine.
     *
     * @param config the run configuration; its {@code aiDefinitions} name the servers
     * @param filter keeps the models whose id contains it (ignoring case); blank keeps every model
     */
    public ListModelsEngine(final SrcMorphConfiguration config, final String filter) {
        this(config, filter, ListModelsEngine::askServer);
    }

    /**
     * Creates the engine with an explicit lister (tests).
     *
     * @param config the run configuration
     * @param filter the model filter
     * @param lister asks one server for its models
     */
    ListModelsEngine(final SrcMorphConfiguration config, final String filter, final ModelLister lister) {
        this.config = Objects.requireNonNull(config, "config");
        this.filter = Objects.requireNonNull(filter, "filter");
        this.lister = Objects.requireNonNull(lister, "lister");
    }

    /**
     * Asks every configured server for its models.
     *
     * @return one listing per server, in the order of the definitions that name them
     * @throws SrcMorphException if no definition names an OpenAI-compatible server, a definition is
     *                           invalid, or a server cannot be asked
     */
    public ModelListReport execute() throws SrcMorphException {
        final @Nullable List<AiModelDefinition> configured = config.getAiDefinitions();
        final List<AiModelDefinition> definitions = configured == null ? new ArrayList<>() : configured;
        final AiModelDefinitionSupport modelDefinitionSupport =
                EngineSupport.buildAiModelDefinitionSupport(definitions, config.getModelDirectory());
        final AiPromptSupport promptSupport = EngineSupport.buildPromptSupport(
                config.getPromptDefinitions(), config.getProfileDirectory(), config.getFieldGenerations());
        final List<ModelListReport.Listing> listings = new ArrayList<>(definitions.size());
        final Set<String> servers = new HashSet<>(definitions.size());
        for (final AiModelDefinition definition : definitions) {
            final String key = definition.getKey();
            final AiGenerationConfig pattern = modelDefinitionSupport.getConfig(key);
            final String provider = AiGenerationProviderFactory.effectiveProvider(
                    config.getGenerationProvider(), pattern.getProvider());
            if (!AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE.equals(provider)
                    || !servers.add(pattern.getBaseUrl().trim()
                            + '\n'
                            + pattern.getApiKeyEnv().trim())) {
                continue;
            }
            LOGGER.info("Listing the models of aiDefinition '{}' at {}", key, pattern.getBaseUrl());
            final List<RemoteModel> models;
            try {
                models = lister.list(LlamaCppJniConfigFactory.fromGenerationConfig(pattern), promptSupport);
            } catch (final IOException | IllegalArgumentException e) {
                throw new SrcMorphException(
                        "Listing the models of aiDefinition '" + key + "' at " + pattern.getBaseUrl() + " failed: "
                                + e.getMessage(),
                        e);
            }
            listings.add(ModelListReport.Listing.of(key, pattern, models.size(), matching(models)));
        }
        if (listings.isEmpty()) {
            throw new SrcMorphException("None of the " + definitions.size() + " aiDefinitions has the provider '"
                    + AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE + "'; ListModels lists the models of"
                    + " such a definition's baseUrl.");
        }
        return new ModelListReport(filter, listings);
    }

    private List<RemoteModel> matching(final List<RemoteModel> models) {
        final String needle = filter.trim().toLowerCase(Locale.ROOT);
        final List<RemoteModel> kept = new ArrayList<>(models.size());
        for (final RemoteModel model : models) {
            if (model.id().toLowerCase(Locale.ROOT).contains(needle)) {
                kept.add(model);
            }
        }
        return kept;
    }

    private static List<RemoteModel> askServer(final LlamaCppJniConfig config, final AiPromptSupport promptSupport)
            throws IOException {
        try (OpenAiCompatibleAiGenerationProvider provider =
                new OpenAiCompatibleAiGenerationProvider(config, promptSupport)) {
            return provider.listModels();
        }
    }
}
