// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiFactCounter;
import net.ladenthin.srcmorph.config.AiFactDefinition;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Resolves reusable {@link AiFactDefinition} groups by key and applies them to routing rules.
 *
 * <p>Built from the {@code factDefinitions} list of the configuration file, this is the fact-set
 * equivalent of {@code AiModelDefinitionSupport}: a rule references a shared fact group via
 * {@link AiFieldGenerationConfig#getFactsKey() factsKey} instead of repeating the same {@code facts}
 * list inline. {@link #resolveFactsKeys(Iterable)} copies the referenced group's counters onto each
 * rule (setting {@link AiFieldGenerationConfig#setFacts(List)}) so the rest of the pipeline reads the
 * already-resolved {@code getFacts()} uniformly, whether the counters were inline or shared.</p>
 *
 * <p>Every entry must have a non-null {@code key} (a null key throws {@link IllegalArgumentException}
 * naming the index), and a {@code factsKey} that matches no group throws it too. The
 * engines wrap these in {@code net.ladenthin.srcmorph.engine.SrcMorphException}, so a misconfigured
 * configuration file fails fast as a configuration error before any model is loaded.</p>
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
     * @throws IllegalArgumentException if any entry has a {@code null} or a duplicate {@code key}
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
            final String key = definition.getKey();
            if (key == null) {
                throw new IllegalArgumentException(
                        "factDefinitions[" + i + "].key is required (bad entry: " + definition + ")");
            }
            final List<AiFactCounter> facts = definition.getFacts();
            // Normalize a group that declared no counters to an empty list, so the map value is never null
            // (a group that references no facts is a no-op, same as an empty facts list).
            if (factsByKey.containsKey(key)) {
                throw new IllegalArgumentException("factDefinitions[" + i + "].key '" + key + "' is defined twice");
            }
            factsByKey.put(key, facts != null ? facts : Collections.emptyList());
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
     * counters, set as the rule's {@code facts}. Rules without a {@code factsKey} are left untouched
     * (their inline {@code facts}, if any, stand).
     *
     * @param rules the routing rules to resolve in place; {@code null} entries are skipped
     * @throws IllegalArgumentException if a rule's {@code factsKey} matches no registered group, or a rule
     *                                  sets both inline {@code facts} and a {@code factsKey}
     */
    public void resolveFactsKeys(final Iterable<AiFieldGenerationConfig> rules) {
        for (final AiFieldGenerationConfig rule : rules) {
            if (rule == null) {
                continue;
            }
            final String factsKey = rule.getFactsKey();
            if (factsKey != null) {
                final List<AiFactCounter> inline = rule.getFacts();
                if (inline != null && !inline.isEmpty()) {
                    throw new IllegalArgumentException("A rule names its facts twice, as facts and as factsKey '"
                            + factsKey + "'; keep one: " + rule);
                }
                rule.setFacts(facts(factsKey));
            }
        }
    }
}
