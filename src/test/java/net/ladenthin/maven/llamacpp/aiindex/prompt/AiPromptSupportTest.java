// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.prompt;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class AiPromptSupportTest {

    private static AiPromptDefinition def(String key, String template) {
        AiPromptDefinition d = new AiPromptDefinition();
        d.setKey(key);
        d.setTemplate(template);
        return d;
    }

    @Test
    public void buildPromptRendersFileNameAndSource() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "File %s: %s")));
        String prompt = support.buildPrompt("summary", Paths.get("a", "Foo.java"), "the source");
        assertThat(prompt, is("File Foo.java: the source"));
    }

    @Test
    public void buildPromptUsesFullPathWhenFileNameNull() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "[%s] %s")));
        // A root path has a null getFileName() on every OS, so buildPrompt falls back to the full
        // path string. Derive the expected separator from the same Path so the assertion holds on
        // Windows too (the root renders with the platform separator: "/" on POSIX, "\" on Windows).
        Path root = Paths.get("/");
        assertThat(support.buildPrompt("summary", root, "x"), is("[" + root + "] x"));
    }

    @Test
    public void buildPromptThrowsWhenKeyUnknown() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("summary", "x %s %s")));
        // template == null branch.
        assertThrows(IllegalArgumentException.class, () -> support.buildPrompt("missing", Paths.get("F"), "s"));
    }

    @Test
    public void buildPromptThrowsWhenTemplateBlank() {
        AiPromptSupport support = new AiPromptSupport(Collections.singletonList(def("blank", "   ")));
        // isBlank(template) branch — kills the negate mutant on the template guard.
        assertThrows(IllegalArgumentException.class, () -> support.buildPrompt("blank", Paths.get("F"), "s"));
    }

    @Test
    public void nullKeyInDefinitionThrowsNamedNpe() {
        // Exercises the requireNonNull(key) message supplier lambda.
        NullPointerException ex = assertThrows(
                NullPointerException.class, () -> new AiPromptSupport(Collections.singletonList(def(null, "tpl"))));
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].key"));
    }

    @Test
    public void nullTemplateInDefinitionThrowsNamedNpe() {
        // Exercises the requireNonNull(template) message supplier lambda.
        NullPointerException ex = assertThrows(
                NullPointerException.class, () -> new AiPromptSupport(Collections.singletonList(def("key", null))));
        assertThat(ex.getMessage(), containsString("promptDefinitions[0].template"));
    }

    @Test
    public void nullDefinitionsListIsTreatedAsEmpty() {
        AiPromptSupport support = new AiPromptSupport(null);
        assertThrows(IllegalArgumentException.class, () -> support.buildPrompt("any", Paths.get("F"), "s"));
    }

    @Test
    public void multipleDefinitionsAreAllRegistered() {
        AiPromptSupport support = new AiPromptSupport(Arrays.asList(def("a", "A %s %s"), def("b", "B %s %s")));
        assertThat(support.buildPrompt("a", Paths.get("F"), "s"), is("A F s"));
        assertThat(support.buildPrompt("b", Paths.get("F"), "s"), is("B F s"));
    }
}
