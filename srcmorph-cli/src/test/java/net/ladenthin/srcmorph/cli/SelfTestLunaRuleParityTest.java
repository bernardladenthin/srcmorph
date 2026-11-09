// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.config.SrcMorphConfigurationFileLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The self-test routes the kinds as the Luna example ({@code examples/copilot-api-gateway/srcmorph-luna.yaml})
 * does, where the real-project runs measured them: every file rule both name has the same condition,
 * prompts, counters, priority and oversize handling, the model aside. The self-test is read through its
 * CLI twin ({@code examples/build-your-own-jar/srcmorph.yaml}), which {@link
 * BuildYourOwnJarSelfTestParityTest} holds equal to it.
 *
 * <p>The differences are deliberate and listed here with their reason; one more fails the test.</p>
 */
public class SelfTestLunaRuleParityTest {

    private static final Path SELF_TEST = Path.of("../examples/build-your-own-jar/srcmorph.yaml");
    private static final Path LUNA = Path.of("../examples/copilot-api-gateway/srcmorph-luna.yaml");

    private static final ObjectMapper JSON = SrcMorphConfigurationFileLoader.newJsonMapper();

    /**
     * The self-test's rules the Luna example has not: the size tiers that move a big Java, SQL, XML or POM
     * file to the Granite presets, which the gateway's window does not need.
     */
    private static final Set<String> SELF_TEST_ONLY =
            Set.of("huge-java", "huge-sql", "big-java", "big-sql", "huge-xml", "big-xml", "huge-pom", "big-pom");

    /**
     * The Luna example's rules the self-test has not: {@code java-ee}, whose sources the self-test's
     * {@code java} rule summarises with the same prompt ({@code file-body-oop}); its counters were level
     * with the fallback in the runs of 2026-11-07.
     */
    private static final Set<String> LUNA_ONLY = Set.of("java-ee");

    /**
     * The fields a rule both name may differ in, by rule: the self-test indexes no script by its
     * {@code #!} line ({@code fileInterpreters} is empty there), so its shell and Perl rules match the
     * extensions only; Ansible goes to the fallback prompt without counters, where gpt-oss-20b, the
     * self-test's model, named more of a play's modules (95 % against 86 %, 2026-11-07).
     */
    private static final Map<String, Set<String>> DIFFERENT_FIELDS = Map.of(
            "shell", Set.of("condition"),
            "perl", Set.of("condition"),
            "ansible", Set.of("promptKey", "factsKey"));

    private static Map<String, ObjectNode> selfTest;
    private static Map<String, ObjectNode> luna;

    @BeforeAll
    static void load() throws Exception {
        selfTest = rulesById(SELF_TEST);
        luna = rulesById(LUNA);
    }

    /** The file rules of a configuration by id, each as its JSON tree without the model it names. */
    private static Map<String, ObjectNode> rulesById(final Path file) throws IOException {
        final List<RoutingRule> rules =
                Objects.requireNonNull(Main.loadConfiguration(file).srcMorph.getRules());
        final Map<String, ObjectNode> byId = new LinkedHashMap<>();
        for (final RoutingRule rule : rules) {
            final ObjectNode tree = JSON.valueToTree(rule);
            tree.remove("aiDefinitionKey");
            byId.put(Objects.requireNonNull(rule.getId()), tree);
        }
        return byId;
    }

    private static Set<String> without(final Set<String> ids, final Set<String> others) {
        final Set<String> rest = new TreeSet<>(ids);
        rest.removeAll(others);
        return rest;
    }

    // <editor-fold defaultstate="collapsed" desc="ruleIds">
    @Test
    public void ruleIds_selfTestAndLuna_differOnlyByTheListedRules() {
        // act, assert
        assertThat(without(selfTest.keySet(), luna.keySet()), is(new TreeSet<>(SELF_TEST_ONLY)));
        assertThat(without(luna.keySet(), selfTest.keySet()), is(new TreeSet<>(LUNA_ONLY)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="sharedRules">
    @Test
    public void sharedRules_selfTestAndLuna_differOnlyInTheListedFields() {
        // arrange
        final Map<String, Set<String>> differences = new LinkedHashMap<>();

        // act
        for (final Map.Entry<String, ObjectNode> entry : selfTest.entrySet()) {
            final ObjectNode other = luna.get(entry.getKey());
            if (other == null) {
                continue;
            }
            final Set<String> fields = new TreeSet<>();
            entry.getValue().fieldNames().forEachRemaining(fields::add);
            other.fieldNames().forEachRemaining(fields::add);
            fields.removeIf(field -> Objects.equals(entry.getValue().get(field), other.get(field)));
            if (!fields.isEmpty()) {
                differences.put(entry.getKey(), fields);
            }
        }

        // assert
        assertThat(differences, is(DIFFERENT_FIELDS));
    }
    // </editor-fold>
}
