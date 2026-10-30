// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

/**
 * Answers of {@code GET <baseUrl>/models} as real servers sent them on 2026-10-30, cut to a few
 * entries: what {@link OpenAiCompatibleProviderSupport#parseModels(String)} has to read.
 */
public final class ModelListFixtures {

    /**
     * The GitHub Copilot API Gateway extension 2.15.3: the limit in {@code max_input_tokens}, no price,
     * a Cursor alias ({@code aliased_model_id}) and the id {@code auto} twice (the second one of the
     * Copilot CLI, with a limit of 0).
     */
    public static final String COPILOT_GATEWAY = "{\"object\":\"list\",\"data\":["
            + "{\"id\":\"mai-code-1.1-flash\",\"object\":\"model\",\"created\":1793320421,\"owned_by\":\"copilot\","
            + "\"name\":\"MAI-Code-1.1-Flash\",\"family\":\"oswe-vscode-modelD\",\"version\":\"mai-code-1.1-flash\","
            + "\"max_input_tokens\":127790,\"capabilities\":{\"chat_completion\":true,\"text_completion\":true,"
            + "\"streaming\":true,\"token_counting\":true}},"
            + "{\"id\":\"claude-haiku-4.5\",\"object\":\"model\",\"created\":1793320421,\"owned_by\":\"copilot\","
            + "\"name\":\"Claude Haiku 4.5\",\"family\":\"claude-haiku-4.5\",\"version\":\"claude-haiku-4.5\","
            + "\"max_input_tokens\":127790,\"capabilities\":{\"chat_completion\":true,\"text_completion\":true,"
            + "\"streaming\":true,\"token_counting\":true}},"
            + "{\"id\":\"auto\",\"object\":\"model\",\"created\":1793320421,\"owned_by\":\"copilot\",\"name\":\"Auto\","
            + "\"family\":\"claude-haiku-5.5\",\"version\":\"claude-haiku-5.5\",\"max_input_tokens\":871793,"
            + "\"capabilities\":{\"chat_completion\":true,\"text_completion\":true,\"streaming\":true,"
            + "\"token_counting\":true}},"
            + "{\"id\":\"auto\",\"object\":\"model\",\"created\":1793320421,\"owned_by\":\"copilotcli\",\"name\":\"Auto\","
            + "\"family\":\"\",\"version\":\"\",\"max_input_tokens\":0,\"capabilities\":{\"chat_completion\":true,"
            + "\"text_completion\":true,\"streaming\":true,\"token_counting\":true}},"
            + "{\"id\":\"copilot-oswe-vscode-modeld\",\"object\":\"model\",\"created\":1793320421,\"owned_by\":\"copilot\","
            + "\"name\":\"MAI-Code-1.1-Flash (Cursor alias)\",\"family\":\"oswe-vscode-modelD\","
            + "\"version\":\"mai-code-1.1-flash\",\"aliased_model_id\":\"mai-code-1.1-flash\",\"max_input_tokens\":127790,"
            + "\"capabilities\":{\"chat_completion\":true,\"text_completion\":true,\"streaming\":true,"
            + "\"token_counting\":true}}]}";

    /**
     * OpenRouter: {@code context_length}, the price per token as a string, and {@code -1} for the price
     * of its router model, whose top provider names no limit.
     */
    public static final String OPENROUTER = "{\"data\":["
            + "{\"id\":\"x-ai/grok-4.7\",\"name\":\"SpaceXAI: Grok 4.7\",\"context_length\":500000,"
            + "\"pricing\":{\"prompt\":\"0.000002\",\"completion\":\"0.000006\"},"
            + "\"top_provider\":{\"context_length\":500000,\"max_completion_tokens\":450000,\"is_moderated\":false}},"
            + "{\"id\":\"openrouter/auto\",\"name\":\"Auto Router\",\"context_length\":2000000,"
            + "\"pricing\":{\"prompt\":\"-1\",\"completion\":\"-1\"},"
            + "\"top_provider\":{\"context_length\":null,\"max_completion_tokens\":null,\"is_moderated\":false}}]}";

    /**
     * llama.cpp's {@code llama-server} b11946 serving the test GGUF with {@code -c 8192}: an Ollama-style
     * {@code models} array next to the OpenAI {@code data}, the window in {@code meta}.
     */
    public static final String LLAMA_SERVER = "{\"models\":[{\"name\":\"/models/SmolLM2-135M-Instruct-Q3_K_M.gguf\","
            + "\"model\":\"/models/SmolLM2-135M-Instruct-Q3_K_M.gguf\",\"modified_at\":\"\",\"size\":\"\",\"digest\":\"\","
            + "\"type\":\"model\",\"description\":\"\",\"tags\":[\"\"],\"capabilities\":[\"completion\"],"
            + "\"parameters\":\"\",\"details\":{\"parent_model\":\"\",\"format\":\"gguf\",\"family\":\"\","
            + "\"families\":[\"\"],\"parameter_size\":\"\",\"quantization_level\":\"\"}}],\"object\":\"list\","
            + "\"data\":[{\"id\":\"/models/SmolLM2-135M-Instruct-Q3_K_M.gguf\","
            + "\"aliases\":[\"/models/SmolLM2-135M-Instruct-Q3_K_M.gguf\"],\"tags\":[],\"object\":\"model\","
            + "\"architecture\":{\"input_modalities\":[\"text\"],\"output_modalities\":[\"text\"]},"
            + "\"created\":1793320369,\"owned_by\":\"llamacpp\",\"meta\":{\"vocab_type\":2,\"n_vocab\":49152,"
            + "\"n_ctx\":8192,\"n_ctx_train\":8192,\"n_embd\":576,\"n_params\":134515008,\"size\":91724544,"
            + "\"ftype\":\"Q3_K - Medium\"}}]}";

    /** jllama's {@code OpenAiCompatServer} ({@code OpenAiSseFormatter.modelsJson}): no limit, no price. */
    public static final String JLLAMA = "{\"object\":\"list\",\"data\":[{\"id\":\"smollm2\",\"object\":\"model\","
            + "\"created\":1793320369,\"owned_by\":\"llama.cpp\",\"ftype\":\"Q3_K - Medium\"}]}";

    private ModelListFixtures() {}
}
