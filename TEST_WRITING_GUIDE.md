# Unit Test Writing Guide — srcmorph (reactor-wide Supplement)

> **Canonical workspace rules** for test sources live in
> [`../workspace/guides/test/TEST_WRITING_GUIDE-8.md`](../workspace/guides/test/TEST_WRITING_GUIDE-8.md):
> the SPDX header, JUnit Jupiter and Hamcrest, the AAA structure with `// pre-assert`, the
> `<editor-fold>` grouping, the three-part test names, exception and parameterized tests, the
> import order, DRY constants per fold and the rule to preserve correct comments. This repository is
> Java 8 in production and Java 21 in its tests; only the `-8.md` baseline applies (no records,
> `var` or text blocks in tests either, so a test reads like the code it tests). This file holds
> only what is particular to this reactor; it applies to all three modules.

---

## 1. Style choices of this reactor

- **Grouping:** Style A of the canonical guide, NetBeans `<editor-fold>`, in every test class (no
  `@Nested`): one fold per method under test, `defaultstate="collapsed"`, `desc` the method's name
  or a short feature label (a fact counter's label in the `*FactPatternsTest` classes).
- **Names:** always the three parts `methodUnderTest_inputOrCondition_expectedBehavior`; a
  constructor is `constructor_...`.
- **Visibility:** test classes and test methods are `public`, as in the owner's other repositories.
  SonarCloud's rule that test methods should not be public is not followed here.
- **`assertThrows`:** the lambda holds only the call that throws; its setup comes before it in the
  `// arrange` section.
- **No fully qualified names in code:** import the class; a qualified name stays only where two
  classes of one simple name meet in a file.

---

## 2. The deterministic provider

Every test that does not need real inference runs on `provider.MockGenerationProvider`
(`generationProvider: mock` in a configuration): its answer is derived from the request, so a
summary, a header and a run record are the same on every run. The shared rule and prompt fixtures
(`createFileRules()`, `createFilePromptDefinitions()`, the prompt and model keys) are in
`CommonTestFixtures`.

---

## 3. Model-backed tests (the llama.cpp JNI provider)

Tests that load the real native binding use the committed model
`srcmorph/src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf` through `NativeLlamaAvailability`:

```java
@Test
public void generate_realModel_returnsText() throws IOException {
    // arrange
    NativeLlamaAvailability.assumeAvailable();
    final ProviderConfig config = ProviderConfig.builder()
            .modelPath(NativeLlamaAvailability.modelPath())
            .threads(NativeLlamaAvailability.THREADS)
            .build();
    // ...
}
```

- `assumeAvailable()` skips, naming the cause, only where the test genuinely cannot run (no model
  in the checkout, or no native library for the platform); with
  `-Dnet.ladenthin.srcmorph.requireNativeLlama=true` it fails instead, which is how CI makes sure
  these tests ran.
- `THREADS` is the production default (4 or fewer cores): more threads than cores made one
  generation take minutes on a CI runner, and PIT re-runs these tests per mutant.
- `LlamaCppJniKnobSweepTest` drives every model knob through a real generation; a knob added later
  fails its completeness check until it is swept or listed in `NOT_SWEPT` with a reason.

---

## 4. Capturing log lines

Production code logs through SLF4J; a test that asserts a log line attaches logback's
`ListAppender` (logback is a test-scope dependency only) to the class's logger and restores the
logger's level and additivity afterwards, as `SummaryGeneratorTest` does:

```java
private ListAppender<ILoggingEvent> logAppender;

@BeforeEach
public void setUp() {
    logAppender = new ListAppender<>();
    logAppender.start();
    final Logger logger = (Logger) LoggerFactory.getLogger(SummaryGenerator.class);
    logger.setLevel(Level.DEBUG);
    logger.addAppender(logAppender);
}
```

---

## 5. Markers and live tests

- `support.AbsolutePathRequirement` tags the tests of the rule that an absolute path is always
  possible for every configurable path (`mvn test -Dgroups=absolute-path` runs exactly these);
  modules that cannot see the class tag their tests with the same string.
- `OpenAiCompatibleLiveTest` calls a real OpenAI-compatible server and skips unless its system
  properties name one; the API key comes from the environment variable the model names, never from
  a file of this repository.
- `CoreArchitectureTest`, `CliArchitectureTest` and `PluginArchitectureTest` are the ArchUnit rules
  of the three modules (CLAUDE.md lists them).
- A test of a link to a directory makes it with `CommonTestFixtures.directoryLink`: a symbolic link,
  or on Windows without the privilege a junction. CI runs on Linux, so a junction (a Windows test
  with `CommonTestFixtures.junction`, a dangling one, the junction branch of `support.DirectoryLinks`)
  runs only on a Windows machine, and a symbolic link only where one can be made; such a test skips
  elsewhere. Cover a branch both reach with attributes made up in the test, as `DirectoryLinksTest`
  does.
