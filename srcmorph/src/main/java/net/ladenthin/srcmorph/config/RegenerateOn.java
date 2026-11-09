// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import lombok.ToString;

/**
 * Which changes rewrite an existing summary ({@code regenerateOn} under {@code srcMorph}): one switch per
 * cause, each {@code true} by default, each compared against a field of the summary's header.
 *
 * <ul>
 *   <li>{@link #isSource() source} ({@code C}): the represented state changed: a file's source, the
 *       children of a package, the packages of the project;</li>
 *   <li>{@link #isPrompt() prompt} ({@code P}): the rule's prompts changed, map and reduce prompts
 *       included;</li>
 *   <li>{@link #isFacts() facts} ({@code N}): the rule's fact counters changed;</li>
 *   <li>{@link #isModel() model} ({@code M}): the rule names another model definition, or its definition
 *       another model;</li>
 *   <li>{@link #isChildren() children} ({@code K}): a summary a package or the project reads changed
 *       (a file summary rewritten for a new prompt leaves its source, and so the package's {@code C}, as
 *       it was).</li>
 * </ul>
 *
 * <p>What no switch turns off: a missing summary, an empty one, another header format ({@code H}), node
 * ({@code X}) or title, and another {@code aiVersion} ({@code A}), the manual lever. {@code force} rewrites
 * every summary whatever the switches say. A field a summary does not record (written before it existed)
 * counts as changed.</p>
 *
 * <p>Mutable JavaBean, bound through its setters from the configuration file.</p>
 */
@ToString
public class RegenerateOn {

    private boolean source = true;
    private boolean prompt = true;
    private boolean facts = true;
    private boolean model = true;
    private boolean children = true;

    /** Creates the switches, every one on. */
    public RegenerateOn() {
        // no-op
    }

    /**
     * Returns whether a changed source ({@code C}) rewrites a summary.
     *
     * @return {@code true} (the default) to rewrite it
     */
    public boolean isSource() {
        return source;
    }

    /**
     * Sets whether a changed source ({@code C}) rewrites a summary; {@code false} freezes the index against
     * changed sources (a kept file summary keeps its {@code C}, so its package sees no change either).
     *
     * @param source {@code false} to keep the summaries of changed sources
     */
    public void setSource(final boolean source) {
        this.source = source;
    }

    /**
     * Returns whether changed prompts ({@code P}) rewrite a summary.
     *
     * @return {@code true} (the default) to rewrite it
     */
    public boolean isPrompt() {
        return prompt;
    }

    /**
     * Sets whether changed prompts ({@code P}) rewrite a summary.
     *
     * @param prompt {@code false} to keep the summaries of an older prompt
     */
    public void setPrompt(final boolean prompt) {
        this.prompt = prompt;
    }

    /**
     * Returns whether changed fact counters ({@code N}) rewrite a summary.
     *
     * @return {@code true} (the default) to rewrite it
     */
    public boolean isFacts() {
        return facts;
    }

    /**
     * Sets whether changed fact counters ({@code N}) rewrite a summary.
     *
     * @param facts {@code false} to keep the summaries of older counters
     */
    public void setFacts(final boolean facts) {
        this.facts = facts;
    }

    /**
     * Returns whether another model ({@code M}) rewrites a summary.
     *
     * @return {@code true} (the default) to rewrite it
     */
    public boolean isModel() {
        return model;
    }

    /**
     * Sets whether another model ({@code M}) rewrites a summary.
     *
     * @param model {@code false} to keep the summaries of another model
     */
    public void setModel(final boolean model) {
        this.model = model;
    }

    /**
     * Returns whether changed child summaries ({@code K}) rewrite a package or the project.
     *
     * @return {@code true} (the default) to rewrite it
     */
    public boolean isChildren() {
        return children;
    }

    /**
     * Sets whether changed child summaries ({@code K}) rewrite a package or the project.
     *
     * @param children {@code false} to keep a package whose files were only re-summarized
     */
    public void setChildren(final boolean children) {
        this.children = children;
    }
}
