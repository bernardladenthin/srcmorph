// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

/** Maven plugin configuration POJO that pairs a lookup key with a prompt template string. */
public class AiPromptDefinition {

    /** Creates a new {@link AiPromptDefinition}. */
    public AiPromptDefinition() {
        // no-op
    }

    private String key;
    private String template;

    /**
     * Returns the prompt template lookup key.
     *
     * @return prompt template lookup key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the prompt template lookup key.
     *
     * @param key lookup key
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Returns the prompt template string.
     *
     * @return prompt template string
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Sets the prompt template string.
     *
     * @param template prompt template string
     */
    public void setTemplate(final String template) {
        this.template = template;
    }
}
