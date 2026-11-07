// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.facts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.FactDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Resolves reusable {@link FactDefinition} groups by key and applies them to routing rules.
 *
 * <p>Built from the {@code factDefinitions} list of the configuration file, this is the fact-set
 * equivalent of {@code ModelRegistry}: a rule references a shared fact group via
 * {@link RoutingRule#getFactsKey() factsKey} instead of repeating the same {@code facts}
 * list inline. {@link #resolveFactsKeys(Iterable)} copies the referenced group's counters onto each
 * rule (setting {@link RoutingRule#setFacts(List)}) so the rest of the pipeline reads the
 * already-resolved {@code getFacts()} uniformly, whether the counters were inline or shared.</p>
 *
 * <p>Every entry must have a non-null {@code key} (a null key throws {@link IllegalArgumentException}
 * naming the index), and a {@code factsKey} that matches no group throws it too. The
 * engines wrap these in {@code net.ladenthin.srcmorph.engine.SrcMorphException}, so a misconfigured
 * configuration file fails fast as a configuration error before any model is loaded.</p>
 *
 * @see FactDefinition
 * @see RoutingRule#getFactsKey()
 */
@ToString
public final class FactGroups {

    /** Error message prefix used when a rule's {@code factsKey} matches no registered definition. */
    private static final String MISSING_DEFINITION_MESSAGE_PREFIX = "Missing fact definition for factsKey: ";

    private final Map<String, List<FactCounter>> factsByKey;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Builds a new {@code FactGroups} from the supplied definitions list.
     *
     * @param definitions list of fact definitions; may be {@code null} or empty (no shared groups);
     *                    every entry must have a non-null {@code key}
     * @throws IllegalArgumentException if any entry has a {@code null} or a duplicate {@code key}
     */
    public FactGroups(final @Nullable List<FactDefinition> definitions) {
        if (definitions == null) {
            this.factsByKey = new HashMap<>(compatibilityHelper.hashMapCapacityFor(0));
            return;
        }
        final int count = definitions.size();
        this.factsByKey = new HashMap<>(compatibilityHelper.hashMapCapacityFor(count));
        for (int i = 0; i < count; i++) {
            final FactDefinition definition = definitions.get(i);
            final String key = definition.getKey();
            if (key == null) {
                throw new IllegalArgumentException(
                        "factDefinitions[" + i + "].key is required (bad entry: " + definition + ")");
            }
            final List<FactCounter> facts = definition.getFacts();
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
    public List<FactCounter> facts(final String key) {
        final List<FactCounter> facts = factsByKey.get(key);
        if (facts == null) {
            throw new IllegalArgumentException(MISSING_DEFINITION_MESSAGE_PREFIX + key);
        }
        return facts;
    }

    /**
     * Resolves each rule's {@link RoutingRule#getFactsKey() factsKey} to the referenced shared
     * counters, set as the rule's {@code facts}. Rules without a {@code factsKey} are left untouched
     * (their inline {@code facts}, if any, stand).
     *
     * @param rules the routing rules to resolve in place; {@code null} entries are skipped
     * @throws IllegalArgumentException if a rule's {@code factsKey} matches no registered group, or a rule
     *                                  sets both inline {@code facts} and a {@code factsKey}
     */
    public void resolveFactsKeys(final Iterable<? extends @Nullable RoutingRule> rules) {
        for (final @Nullable RoutingRule rule : rules) {
            if (rule == null) {
                continue;
            }
            final String factsKey = rule.getFactsKey();
            if (factsKey != null) {
                final List<FactCounter> inline = rule.getFacts();
                if (inline != null && !inline.isEmpty()) {
                    throw new IllegalArgumentException("A rule names its facts twice, as facts and as factsKey '"
                            + factsKey + "'; keep one: " + rule);
                }
                rule.setFacts(facts(factsKey));
            }
        }
    }
}
