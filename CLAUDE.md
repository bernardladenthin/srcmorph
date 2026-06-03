# CLAUDE.md — llamacpp-ai-index-maven-plugin

This document provides guidance for AI assistants working on the llamacpp-ai-index-maven-plugin codebase.

---

## Project Overview

**llamacpp-ai-index-maven-plugin** is a free Maven plugin that generates AI-readable hierarchical index and summary files for Java source code projects using llama.cpp-compatible local models (GGUF format). It produces structured `.ai.md` files with metadata headers and AI-generated summaries for both individual source files and packages.

- **Group ID:** `net.ladenthin`
- **Artifact ID:** `llamacpp-ai-index-maven-plugin`
- **Version:** 0.1.0-SNAPSHOT
- **Java:** target bytecode 1.8, built with JDK 21
- **License:** Apache 2.0
- **Author:** Bernard Ladenthin (Copyright 2026)
- **Plugin goal prefix:** `ai-index`

---

## Build System

The project uses **Maven** (minimum 3.6.3).

### Common Commands

```bash
# Compile only
mvn compile

# Run all tests
mvn test

# Build the plugin JAR
mvn package

# Build without tests
mvn package -DskipTests

# Run the plugin against itself (self-test profile)
mvn ai-index:generate -P ai-index-selftest

# Install to local Maven repository
mvn install
```

### JVM / Compiler Configuration

- Java 1.8 source and target (compiled with JDK 21)
- UTF-8 encoding
- `maven-enforcer-plugin` requires Maven ≥ 3.6.3

### Offline / Restricted-Network Environments

When Maven cannot reach the internet (proxy, air-gap, restricted CI), use the options below.

**Offline Maven (requires warm `~/.m2/repository` cache):**
```bash
# Run tests without downloading anything
mvn test -o

# Package without downloading anything
mvn package -o -DskipTests
```

The cache is warm after any previous successful `mvn test` or `mvn install` run.

**Direct `javac` compilation (fallback, no Maven required):**
```bash
# Gather classpath from already-downloaded JARs
CP=$(find ~/.m2/repository -name "*.jar" | tr '\n' ':')
OUT=/tmp/aiindex-classes && mkdir -p "$OUT"

# Compile production sources
find src/main/java -name "*.java" | xargs javac -cp "$CP" -d "$OUT" --release 8

# Compile test sources (after production classes are compiled)
TOUT=/tmp/aiindex-test-classes && mkdir -p "$TOUT"
find src/test/java -name "*.java" | xargs javac -cp "$CP:$OUT" -d "$TOUT" --release 8
```

Zero compiler output means zero errors.

---

## Project Structure

```
llamacpp-ai-index-maven-plugin/
├── src/
│   ├── main/
│   │   └── java/net/ladenthin/maven/llamacpp/aiindex/
│   │       ├── AiMdDocument.java           # Record: header + body
│   │       ├── AiMdHeader.java             # Record: document metadata
│   │       ├── AiMdHeaderCodec.java        # Encode/decode metadata headers
│   │       ├── AiMdDocumentCodec.java      # Encode/decode full documents
│   │       ├── AiMdHeaderSupport.java      # Header manipulation utilities
│   │       ├── AiGenerationConfig.java     # Configuration for a generation step
│   │       ├── AiModelDefinition.java      # POJO for a named AI model definition (Maven @Parameter)
│   │       ├── AiModelDefinitionSupport.java# Key-indexed lookup: AiModelDefinition -> AiGenerationConfig
│   │       ├── AiFieldGenerationConfig.java# Per-field generation config (references model def by key)
│   │       ├── AiFieldGenerationSupport.java# Shared field-generation loop (summary/keywords/body)
│   │       ├── AiGenerationKind.java       # Enum: generation types
│   │       ├── AiGenerationRequest.java    # Request object
│   │       ├── AiGenerationResult.java     # Record: summary + keywords + body output
│   │       ├── AiPromptDefinition.java     # Prompt template definition
│   │       ├── AiPreparedPrompt.java       # Prompt after substitution
│   │       ├── AiPromptSupport.java        # Prompt lookup utilities
│   │       ├── AiPromptPreparationSupport.java # Prompt preparation logic
│   │       ├── AiGenerationProvider.java   # Provider interface
│   │       ├── AiGenerationProviderFactory.java # Factory for providers
│   │       ├── MockAiGenerationProvider.java    # Mock for testing
│   │       ├── LlamaCppJniAiSummaryProvider.java# llama.cpp JNI provider
│   │       ├── LlamaCppJniConfig.java      # llama.cpp configuration
│   │       ├── AiSummaryResponse.java      # AI generation response
│   │       ├── SourceFileIndexer.java      # Indexes + summarizes source files
│   │       ├── PackageIndexer.java         # Aggregates + summarizes package index files
│   │       ├── AiChecksumSupport.java      # Checksum utilities
│   │       ├── AiTimeSupport.java          # Timestamp utilities
│   │       ├── AiPathSupport.java          # Path utilities
│   │       ├── AbstractAiIndexMojo.java    # Shared parameters and utilities for all mojos
│   │       ├── GenerateMojo.java           # goal: ai-index:generate
│   │       └── AggregatePackagesMojo.java  # goal: ai-index:aggregate-packages
│   ├── site/
│   │   └── ai/                            # Output directory for .ai.md files
│   └── test/
│       ├── java/net/ladenthin/maven/llamacpp/aiindex/
│       │   └── *.java                     # JUnit Jupiter tests
│       └── resources/
│           └── SmolLM2-135M-Instruct-Q3_K_M.gguf  # Small test model
├── .github/workflows/                     # CI/CD pipelines
├── pom.xml
└── README.md
```

---

## Core Architecture

### Two-Phase Operation

The plugin operates in two logical phases:

**Phase 1 — File Indexing & Summarization**
```
[Source .java files] → SourceFileIndexer → [*.java.ai.md files (with s/k filled)]
```

**Phase 2 — Package Aggregation & Summarization**
```
[*.java.ai.md files] → PackageIndexer → [package.ai.md files (with s/k filled)]
```

### Key Components

| Class | Role |
|---|---|
| `AbstractAiIndexMojo` | Shared `@Parameter` fields and utilities for all mojos |
| `GenerateMojo` | Phase 1: index + summarize source files |
| `AggregatePackagesMojo` | Phase 2: aggregate + summarize package index files |
| `SourceFileIndexer` | Walks source trees, creates `.ai.md` files, calls AI for `s`/`k` fields |
| `PackageIndexer` | Creates `package.ai.md` files with contents listings, calls AI for `s`/`k` fields |
| `AiGenerationProvider` | Interface for AI backends (llama.cpp JNI or mock) |
| `AiFieldGenerationSupport` | Shared field-generation loop extracted from both indexers |
| `AiGenerationResult` | Record carrying `summary`, `keywords`, and `body` out of the loop |
| `AiModelDefinition` | Maven `@Parameter` POJO for a named AI model definition |
| `AiModelDefinitionSupport` | Key-indexed lookup: converts `AiModelDefinition` → `AiGenerationConfig` |
| `AiMdDocumentCodec` | Reads and writes `.ai.md` files |
| `AiMdHeaderCodec` | Encodes/decodes the YAML-like metadata header |
| `AiPromptSupport` | Looks up prompt templates by key |
| `AiPromptPreparationSupport` | Prepares prompts with source substitution and trimming |

### Document Format

Each `.ai.md` file begins with a metadata header block:

```
<!-- ai-md-header
h: "1.0"
title: "MyClass.java"
c: "a1b2c3d4"
d: "2026-01-01T00:00:00Z"
t: "2026-01-01T00:01:00Z"
g: "0.1.0"
a: "1.0.0"
x: "file"
s: "This class handles..."
k: "parser,codec,markdown"
-->
```

| Field | Meaning |
|---|---|
| `h` | Header format version |
| `title` | File or package path |
| `c` | CRC32 checksum of the source file (8-char uppercase hex; see `AiChecksumSupport`) |
| `d` | Index creation timestamp (ISO-8601) |
| `t` | Last generation timestamp |
| `g` | Plugin version (`project.version`) |
| `a` | AI model version |
| `x` | Node type: `file` or `package` |
| `s` | AI-generated summary |
| `k` | AI-generated keywords (comma-separated) |

### Provider Pattern

`AiGenerationProvider` is a `Closeable` interface for AI backends:

| Implementation | Description |
|---|---|
| `LlamaCppJniAiSummaryProvider` | Uses the `net.ladenthin:llama` JNI binding to run local GGUF models |
| `MockAiGenerationProvider` | Returns deterministic mock responses; used in all tests |

`AiGenerationProviderFactory` selects the provider by name (`"llamacpp-jni"` or `"mock"`).

---

## Maven Plugin Goals

| Goal | Description |
|---|---|
| `ai-index:generate` | Phase 1: index source files and fill AI summary/keywords fields |
| `ai-index:aggregate-packages` | Phase 2: aggregate package index files and fill AI summary/keywords fields |

### Key Parameters (`GenerateMojo`)

| Parameter | Property | Default | Description |
|---|---|---|---|
| `outputDirectory` | `aiIndex.outputDirectory` | `${basedir}/src/site/ai` | Where `.ai.md` files are written |
| `skip` | `aiIndex.skip` | `false` | Skip all execution |
| `force` | `aiIndex.force` | `false` | Regenerate even if summary exists |
| `subtrees` | `aiIndex.subtrees` | *(all)* | Limit to specific source subdirectories |
| `fileExtensions` | `aiIndex.fileExtensions` | `.java` | File extensions to index |
| `summaryProvider` | `aiIndex.summaryProvider` | `mock` | `mock` or `llamacpp-jni` |
| `llamaModelPath` | `aiIndex.llama.modelPath` | — | Path to GGUF model file |
| `llamaContextSize` | `aiIndex.llama.contextSize` | `2048` | Context window size |
| `llamaMaxOutputTokens` | `aiIndex.llama.maxOutputTokens` | `128` | Max generated output tokens |
| `llamaTemperature` | `aiIndex.llama.temperature` | `0.15` | Sampling temperature |
| `llamaThreads` | `aiIndex.llama.threads` | `2` | CPU threads for inference |

---

## Testing

### Frameworks

- **JUnit Jupiter** (6.1.0) — test runner (`@Test`, `@BeforeEach`, `@TempDir`)
- **Hamcrest** — matchers (`assertThat`, `is`, `equalTo`)
- **`MockAiGenerationProvider`** — deterministic AI responses for all tests

### Test Model

`src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf` is a small (≈90 MB) GGUF model used by integration tests that exercise the real `LlamaCppJniAiSummaryProvider`. These tests are skipped when the JNI native library is unavailable.

### Conventions

- All tests that invoke the real llama.cpp JNI must guard with an availability check.
- Tests that only exercise Java logic use `MockAiGenerationProvider`.
- Use `Files.createTempDirectory(...)` for temporary file system state.
- See `TEST_WRITING_GUIDE.md` for full conventions.

---

## Code Conventions

### Logging

Production code uses `org.apache.maven.plugin.logging.Log` (not SLF4J), obtained via `AbstractMojo.getLog()` or passed via constructor. For constructor-based logger injection see `CODE_WRITING_GUIDE.md`.

### Null Safety

- Mark nullable return types and parameters explicitly.
- Prefer early null/empty guards with `log.warn(...)` over silent skips.

### Named Constants

Every meaningful literal (string keys, header field names, node types, version strings) must be a `public static final` or `private static final` named constant with Javadoc. See `CODE_WRITING_GUIDE.md`.

### License Headers

All source files must include the Apache 2.0 license header wrapped in `// @formatter:off` / `// @formatter:on`. See any existing source file for the template.

### Records

Immutable value types are implemented as Java `record` types (e.g., `AiMdDocument`, `AiMdHeader`, `AiPreparedPrompt`, `AiSummaryResponse`). Prefer records for data carriers.

---

## CI/CD Pipelines (`.github/workflows/`)

| Workflow | Trigger | Purpose |
|---|---|---|
| `publish.yml` | Push, PR, manual dispatch | Unified build/test/coverage/package pipeline; publishes snapshots and Maven Central releases |
| `codeql.yml` | Schedule / Push | GitHub CodeQL security scanning |
| `scorecard.yml` | Schedule / Push | OpenSSF Scorecard supply-chain security analysis |
| `osv-scanner.yml` | Schedule / Push / PR | Google OSV-Scanner dependency vulnerability scan |
| `reuse.yml` | Push / PR | FSFE REUSE license-compliance check |
| `claude-code-review.yml` | PR | AI-powered code review |
| `claude.yml` | Issue/PR comment with `@claude` | Claude Code interactive assistant |

---

## Dependencies Summary

| Dependency | Version | Purpose |
|---|---|---|
| `net.ladenthin:llama` | 5.0.0 | llama.cpp JNI binding (GGUF inference) |
| `org.apache.maven:maven-plugin-api` | 3.9.13 | Maven plugin API (provided) |
| `org.apache.maven.plugin-tools:maven-plugin-annotations` | 3.15.1 | `@Mojo`, `@Parameter` annotations (provided) |

Test-only:

| Dependency | Version | Purpose |
|---|---|---|
| `org.junit.jupiter:junit-jupiter` | 6.1.0 | Test runner |
| `org.hamcrest:hamcrest` | 3.0 | Matchers |

---

## Test / Code Writing Compliance

After modifying or creating any `.java` file:

- For `*Test.java` files, verify rules from
  [`../workspace/guides/TEST_WRITING_GUIDE.md`](../workspace/guides/TEST_WRITING_GUIDE.md)
  (canonical) **and** this repo's own `TEST_WRITING_GUIDE.md` (plugin-
  specific supplement).
- For production sources, verify rules from
  [`../workspace/guides/CODE_WRITING_GUIDE.md`](../workspace/guides/CODE_WRITING_GUIDE.md)
  (canonical) **and** this repo's own `CODE_WRITING_GUIDE.md`.
- Apply all fixable violations automatically; report only those that
  cannot be resolved without a large refactor.

---

## Pull Request Workflow

See [`../workspace/workflows/pull-request-workflow.md`](../workspace/workflows/pull-request-workflow.md).

---

## Key Design Principles

1. **Local-first** — all AI inference runs locally via llama.cpp; no cloud API calls, no data leaves the machine.
2. **Deterministic indexing** — same source produces the same `.ai.md` skeleton; only AI-generated fields (`s`, `k`) vary.
3. **Incremental updates** — files with existing summaries are skipped unless `force=true`; checksums detect source changes.
4. **Unified indexing and summarization** — each indexer (`SourceFileIndexer`, `PackageIndexer`) both creates the `.ai.md` skeleton and fills in AI fields in a single pass; no separate summarization step is needed.
5. **Provider abstraction** — AI backends are pluggable through `AiGenerationProvider`; mock provider enables fully deterministic tests.
6. **Configuration-driven prompts** — prompt templates are defined in POM configuration, not hardcoded in Java; changing a prompt requires no code change.

## Javadoc Conventions

See [`../workspace/policies/javadoc-conventions.md`](../workspace/policies/javadoc-conventions.md).

## SpotBugs Suppressions

See [`../workspace/policies/spotbugs-suppressions.md`](../workspace/policies/spotbugs-suppressions.md).

## jqwik Policy

See [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md).

## Open TODOs

- **jqwik pin policy** — see [`../workspace/policies/jqwik-prompt-injection.md`](../workspace/policies/jqwik-prompt-injection.md). `jqwik.version ≤ 1.9.3` is mandatory.

- **`@VisibleForTesting` audit.** No usages currently. Walk the production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.
- **Null-safety refinement.** JSpecify + NullAway are now enforced at compile time in **strict JSpecify mode** with the extra options `CheckOptionalEmptiness`, `AcknowledgeRestrictiveAnnotations`, `AcknowledgeAndroidRecent`, `AssertsEnabled` (see `pom.xml`); `@NullMarked` on the package via `package-info.java`; JDK module exports in `.mvn/jvm.config`. Maven `@Parameter` / `@Component` fields are excluded from initializer checks; framework-populated POJOs (`AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiGenerationConfig`) carry class-level `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`. The Checker Framework Nullness Checker now runs as a second pass alongside NullAway (see "Checker Framework" item under Further-strictness — it has moved from open to done for this repo). Open follow-up: review remaining unannotated public API surfaces for places where `@Nullable` would be more precise than the implicit non-null default.

- **Further-strictness open points (cross-repo, not yet done).** Items below are tracked across all four Bernard-Ladenthin Java repos and can be picked up incrementally:
  - **SpotBugs `effort=Max` + `threshold=Low`** — currently default effort/threshold. Raising both surfaces more findings (and takes longer per build). Worth a one-off experiment to triage what appears before committing.
  - ~~**Error Prone bug-pattern promotions to `ERROR`** — Error Prone is already running and emits warnings during compile (`NotJavadoc`, `JdkObsolete`, `NonAtomicVolatileUpdate`, `InvalidThrows`, `MissingOverride`, `FutureReturnValueIgnored`, `EqualsGetClass`, `ReferenceEquality`, etc.). Promote the high-confidence, zero-noise-today patterns to `ERROR` via per-`-Xep:<Name>:ERROR` args.~~ **DONE** (commit `034b553` — 12 Error Prone bug patterns promoted to `ERROR`: `BoxedPrimitiveEquality`, `EqualsHashCode`, `EqualsIncompatibleType`, `IdentityBinaryExpression`, `SelfAssignment`, `SelfComparison`, `SelfEquals`, `DeadException`, `FormatString`, `InvalidPatternSyntax`, `OptionalEquality`, `ImpossibleNullComparison`; `-Xlint:all` enabled).
  - **`javac -Werror` + `-Xlint:all,-serial,-options`** — **DONE for this repo** (with `-Xlint:all,-serial,-options,-classfile,-processing`). EqualsGetClass warnings on the 7 `@ConvertToRecord` classes were fixed by switching to `instanceof` checks; `Java8CompatibilityHelper.formatted` carries an inline `@SuppressWarnings("AnnotateFormatMethod")` (we cannot annotate `@FormatMethod` because `AiPromptSupport#buildPrompt` passes a runtime template loaded from config); generated `HelpMojo` is excluded from Error Prone via `-XepExcludedPaths`; `listOf` carries `@SafeVarargs` + `@SuppressWarnings({"unchecked","varargs"})`. Cross-repo: streambuffer is done; `java-llama.cpp` next; BitcoinAddressFinder has its own catalogued warning list.
  - ~~**`-parameters` javac arg** — bakes real parameter names into bytecode (visible via reflection, Jackson, OpenAPI). Useful even where reflection isn't used today.~~ **DONE** (commit `7ae3279` — `<parameters>true</parameters>` in `maven-compiler-plugin`; dropped for the test execution where jcstress / Lincheck reflection would otherwise break).
  - ~~**`--release N`** instead of `-source N -target N` — forces the API surface to actually match the target JDK; prevents accidental use of post-N JDK APIs.~~ **DONE** (commit `7ae3279` — `<release>${maven.compiler.release}</release>` (release 8) for main sources, `<release>9</release>` for `module-info.java`).
  - **Mutation-testing threshold enforcement (PIT)** — `streambuffer` enforces 100 % mutation coverage over its whole package. **This repo and `java-llama.cpp` / `BitcoinAddressFinder` instead use a "single class, full plumbing" pattern**: PIT is wired in `pom.xml` and runs on every CI build with `<mutationThreshold>100</mutationThreshold>`, but `<targetClasses>` is narrowed to one well-tested utility class. The intent is to keep the wiring exercised and the gate live without forcing every class up to 100 % mutation coverage at once. Expand `<targetClasses>` incrementally as classes reach parity (README TODO tracks this).
  - **Checker Framework as a second static-nullness pass** — **DONE for this repo** (and for `streambuffer`). The Nullness Checker is wired in `pom.xml` (4.1.0) and runs alongside NullAway. `HelpMojo` is skipped via `-AskipDefs`; framework-populated POJO classes carry `@SuppressWarnings("initialization.fields.uninitialized")`; record-style equals overrides use `@Nullable Object`. Remaining cross-repo work: `java-llama.cpp` and `BitcoinAddressFinder`.
  - **JPMS `module-info.java` with `@NullMarked` at module level** — **DONE for this repo** (and `streambuffer`); remaining cross-repo work covers `java-llama.cpp` and `BitcoinAddressFinder`. The plugin's `module-info.java` exports the single hand-written package `net.ladenthin.maven.llamacpp.aiindex`; the auto-generated `HelpMojo` package is deliberately NOT exported because Maven loads plugins classpath-only and never consults the descriptor for Mojo discovery. Two-execution `maven-compiler-plugin` pattern (release 8 for sources, release 9 for `module-info.java`); the resulting jar carries `module-info.class` at its root and is backward-compatible with Java 8 classpath consumers. Module-level `@NullMarked` IS set on the module descriptor (see `module-info.java`); JSpecify is pulled in via `requires static org.jspecify;` so the annotation does not become a runtime dependency. The descriptor's own Javadoc explains the reasoning (centralised nullness scope visible to non-NullAway tools, no need for a per-package `@NullMarked` duplicate).
  - ~~**Banned-API enforcement** — add Maven Enforcer `bannedDependencies` / `dependencyConvergence` rules and a `banned-api-checker`-style rule for things like `Thread.sleep` in production, `System.exit`, etc.~~ **DONE** (commit `d654442` extended Maven Enforcer to the four standard rules including `bannedDependencies` and `dependencyConvergence`; commit `fd8cf80` added ArchUnit rules in `PluginArchitectureTest` that ban `System.exit`, `new Random`, and `Thread.sleep` in production; commit `ad37355` also bans `sun.*` / `com.sun.*` / `jdk.internal.*` imports).
  - **Additional ArchUnit rules to consider** — layered-architecture rules (`layeredArchitecture().consideringAllDependencies()`), per-module banned-imports lists, public-API-surface constraints (no public mutable static state, no public field that is not final, etc.). *Partial:* "public non-static fields must be final" landed in commit `d2b1af9` (`noPublicMutableFields` in `PluginArchitectureTest`); layered-architecture and per-module banned-imports are still open.
- **No LogCaptor smoke test needed** — this module has no logging code (`org.slf4j.*` not used in `src/main/java/`). If logging is ever introduced, add a LogCaptor smoke test at the same time so the binding/configuration is exercised in tests.

- **Cross-repo code-quality TODOs** — see [`../workspace/policies/code-quality-todos.md`](../workspace/policies/code-quality-todos.md) for the canonical `@VisibleForTesting` design-fit review, package hierarchy review, and class/method naming review. This repo has no `@VisibleForTesting` usages today; the package and naming reviews are still open here.

- ~~**Abstract the Java and test writing guidelines to a workspace-level shared layer.**~~ **DONE.** Canonical guides now live at [`../workspace/guides/CODE_WRITING_GUIDE.md`](../workspace/guides/CODE_WRITING_GUIDE.md) and [`../workspace/guides/TEST_WRITING_GUIDE.md`](../workspace/guides/TEST_WRITING_GUIDE.md); the canonical TDD skill is at [`../workspace/.claude/skills/java-tdd-guide/SKILL.md`](../workspace/.claude/skills/java-tdd-guide/SKILL.md). This repo's `CODE_WRITING_GUIDE.md` / `TEST_WRITING_GUIDE.md` now hold only plugin-specific supplements (Maven `@Parameter` POJO patterns, named-constant catalogue for header field keys / node types / provider names, LLM-integration test patterns).

- ~~**Adopt a standard `CLAUDE.md` template/tool for cross-repo consistency.**~~ **DONE.** Template lives at [`../workspace/templates/CLAUDE.md.template`](../workspace/templates/CLAUDE.md.template); this CLAUDE.md uses the template's section order.
