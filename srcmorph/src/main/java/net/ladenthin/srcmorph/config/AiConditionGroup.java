// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * A list of child {@link AiCondition}s, used as the body of an {@code and} / {@code or} combinator
 * in {@link AiCondition}.
 *
 * <p>In the configuration file the children are the list under {@code conditions}
 * ({@code and: {conditions: [...]}}), not a list given to {@code and} or {@code or} directly.</p>
 */
@ToString
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
