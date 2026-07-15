// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Resolves reusable {@link AiFactDefinition} groups by key and applies them to routing rules.
 *
 * <p>Built from the goal's {@code <factDefinitions>} list, this is the fact-set equivalent of
 * {@code AiModelDefinitionSupport}: a rule references a shared fact group via
 * {@link AiFieldGenerationConfig#getFactsKey() factsKey} instead of repeating the same {@code <facts>}
 * block inline. {@link #resolveFactsKeys(Iterable)} copies the referenced group's counters onto each
 * rule (setting {@link AiFieldGenerationConfig#setFacts(List)}) so the rest of the pipeline reads the
 * already-resolved {@code getFacts()} uniformly, whether the counters were inline or shared.</p>
 *
 * <p>Every entry must have a non-null {@code key} (a null key throws {@link NullPointerException} naming
 * the index); a {@code factsKey} that matches no group throws {@link IllegalArgumentException}. Callers
 * (e.g. the llamacpp-ai-index-maven-plugin module's Mojos) wrap these in
 * {@code org.apache.maven.plugin.MojoExecutionException} so a misconfigured POM fails fast as a user
 * error. This library itself has no Maven dependency, so that type is referenced here only in prose.</p>
 *
 * @see AiFactDefinition
 * @see AiFieldGenerationConfig#getFactsKey()
 */
@ToString
public final class AiFactDefinitionSupport {

    /** Error message prefix used when a rule's {@code factsKey} matches no registered definition. */
    private static final String MISSING_DEFINITION_MESSAGE_PREFIX = "Missing fact definition for factsKey: ";

    private final Map<String, List<AiFactCounter>> factsByKey;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Builds a new {@code AiFactDefinitionSupport} from the supplied definitions list.
     *
     * @param definitions list of fact definitions; may be {@code null} or empty (no shared groups);
     *                    every entry must have a non-null {@code key}
     * @throws NullPointerException if any entry has a {@code null} {@code key}
     */
    public AiFactDefinitionSupport(final @Nullable List<AiFactDefinition> definitions) {
        if (definitions == null) {
            this.factsByKey = new HashMap<>(compatibilityHelper.hashMapCapacityFor(0));
            return;
        }
        final int count = definitions.size();
        this.factsByKey = new HashMap<>(compatibilityHelper.hashMapCapacityFor(count));
        for (int i = 0; i < count; i++) {
            final AiFactDefinition definition = definitions.get(i);
            final int index = i;
            Objects.requireNonNull(
                    definition.getKey(),
                    () -> "factDefinitions[" + index + "].key is required (bad entry: " + definition + ")");
            final List<AiFactCounter> facts = definition.getFacts();
            // Normalize a group that declared no counters to an empty list, so the map value is never null
            // (a group that references no facts is a no-op, same as an empty <facts>).
            factsByKey.put(definition.getKey(), facts != null ? facts : Collections.emptyList());
        }
    }

    /**
     * Returns the fact counters registered under {@code key} (empty when the group declared none).
     *
     * @param key the fact-definition key to look up
     * @return the counters for that group; never {@code null}
     * @throws IllegalArgumentException if no group is registered for {@code key}
     */
    public List<AiFactCounter> facts(final String key) {
        final List<AiFactCounter> facts = factsByKey.get(key);
        if (facts == null) {
            throw new IllegalArgumentException(MISSING_DEFINITION_MESSAGE_PREFIX + key);
        }
        return facts;
    }

    /**
     * Resolves each rule's {@link AiFieldGenerationConfig#getFactsKey() factsKey} to the referenced shared
     * counters, overwriting the rule's inline {@code facts}. Rules without a {@code factsKey} are left
     * untouched (their inline {@code <facts>}, if any, stand).
     *
     * @param rules the routing rules to resolve in place; {@code null} entries are skipped
     * @throws IllegalArgumentException if a rule's {@code factsKey} matches no registered group
     */
    public void resolveFactsKeys(final Iterable<AiFieldGenerationConfig> rules) {
        for (final AiFieldGenerationConfig rule : rules) {
            if (rule == null) {
                continue;
            }
            final String factsKey = rule.getFactsKey();
            if (factsKey != null) {
                rule.setFacts(facts(factsKey));
            }
        }
    }
}
