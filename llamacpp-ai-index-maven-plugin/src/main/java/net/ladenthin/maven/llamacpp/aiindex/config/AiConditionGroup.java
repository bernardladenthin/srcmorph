// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A list of child {@link AiCondition}s, used as the body of an {@code <and>} / {@code <or>} combinator
 * in {@link AiCondition}.
 *
 * <p>The list field is named {@code conditions} so the Maven plugin framework binds the items as
 * {@code <conditions><condition>…</condition></conditions>} — Maven derives a collection's item element
 * name from the field name's singular, which is why the combinators wrap their children in this group
 * rather than holding a {@code List<AiCondition>} directly (a field named {@code and}/{@code or} would
 * require the items to be {@code <and>}/{@code <or>}).</p>
 */
@ToString
@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
public class AiConditionGroup {

    private @Nullable List<AiCondition> conditions;

    /** Creates a new {@link AiConditionGroup}. */
    public AiConditionGroup() {
        // no-op
    }

    /**
     * Returns the child conditions, or {@code null} when none were configured.
     *
     * @return the child conditions, or {@code null}
     */
    public @Nullable List<AiCondition> getConditions() {
        return conditions;
    }

    /**
     * Sets the child conditions (defensively copied).
     *
     * @param conditions the child conditions
     */
    public void setConditions(final @Nullable Collection<AiCondition> conditions) {
        this.conditions = conditions != null ? new ArrayList<>(conditions) : null;
    }
}
