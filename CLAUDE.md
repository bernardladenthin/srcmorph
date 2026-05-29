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
| `c` | SHA-256 checksum of the source file |
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

## Test Writing Compliance

After modifying or creating any `*Test.java` file, automatically verify that all rules from `TEST_WRITING_GUIDE.md` are applied to the modified test class. Apply all fixable violations on your own without asking. Only report violations that cannot be resolved without a large refactoring. Consider the task complete only after all auto-fixable rules are satisfied.

---

## Code Writing Compliance

After modifying or creating any production `.java` file, automatically verify that all rules from `CODE_WRITING_GUIDE.md` are applied to the modified class. Apply all fixable violations on your own without asking. Only report violations that cannot be resolved without a large refactoring. Consider the task complete only after all auto-fixable rules are satisfied.

---

## Pull Request Workflow

### Step 1 — Detect whether `gh` is available

```bash
gh --version 2>/dev/null && echo "gh available" || echo "gh not available"
```

If `gh` is **not** available, inform the user and stop.

### Step 2 — Create the PR

```bash
gh pr create \
  --title "<concise summary, ≤70 chars>" \
  --body "$(cat <<'EOF'
## Summary
- <bullet: what changed>
- <bullet: why>

## Test plan
- [ ] Affected test classes pass
- [ ] Full CI passes

<session URL>
EOF
)"
```

### Step 3 — Wait for all checks to complete

```bash
gh pr checks <PR-number> --watch --interval 30
```

### Step 4 — Triage failures

```bash
gh run list --branch <branch-name> --limit 10
gh run view <run-id> --log-failed
```

### Step 5 — Fix, commit, push, repeat

1. Apply the fix.
2. Commit and push:
   ```bash
   git add <files>
   git commit -m "Fix <check-name>: <short description>"
   git push
   ```
3. Return to Step 3. Repeat until all checks pass.

### Step 6 — Report to the user

Summarise what was fixed. If a failure cannot be fixed automatically, stop and ask for direction.

---

## Key Design Principles

1. **Local-first** — all AI inference runs locally via llama.cpp; no cloud API calls, no data leaves the machine.
2. **Deterministic indexing** — same source produces the same `.ai.md` skeleton; only AI-generated fields (`s`, `k`) vary.
3. **Incremental updates** — files with existing summaries are skipped unless `force=true`; checksums detect source changes.
4. **Unified indexing and summarization** — each indexer (`SourceFileIndexer`, `PackageIndexer`) both creates the `.ai.md` skeleton and fills in AI fields in a single pass; no separate summarization step is needed.
5. **Provider abstraction** — AI backends are pluggable through `AiGenerationProvider`; mock provider enables fully deterministic tests.
6. **Configuration-driven prompts** — prompt templates are defined in POM configuration, not hardcoded in Java; changing a prompt requires no code change.

## Javadoc Conventions

### HTML Entities

In Javadoc comments, never use bare Unicode characters for operators and symbols. Use HTML entities instead:

| Symbol | HTML entity |
|---|---|
| `<` | `&lt;` |
| `>` | `&gt;` |
| `≤` | `&#x2264;` |
| `≥` | `&#x2265;` |
| `→` | `&#x2192;` |
| `←` | `&#x2190;` |
| `≠` | `&#x2260;` |

Use numeric hex entities (`&#xNNNN;`) for any Unicode symbol outside ASCII. Named entities (`&lt;`, `&gt;`) are acceptable for `<` and `>`.

## SpotBugs Suppressions

`spotbugs-exclude.xml` at the repo root contains documented suppressions for findings that are by-design or false positives. **When refactoring or renaming code referenced in that file, re-check the affected `<Match>` blocks:**

- `<Class>`, `<Method>`, and `<Field>` filters use exact string matches — a rename silently disables the suppression and may either un-suppress a real bug or leave a stale entry behind.
- After refactors, run `mvn -B -ntp -DskipTests -Dgpg.skip=true verify` and confirm the BugInstance count is unchanged. A drop means a suppression is now stale and should be deleted; an increase means a new finding needs its own decision (fix vs. suppress).
- Keep the rationale comment on each `<Match>` accurate — if the original justification no longer applies to the post-refactor code, remove the suppression rather than leave outdated reasoning in place.
- Never use `--` inside `<!-- ... -->` comment bodies in `spotbugs-exclude.xml` — XML forbids it and the entire filter file silently stops loading (every previously suppressed finding reappears).

## Open TODOs

- **`@VisibleForTesting` audit.** No usages currently. Walk the production tree for package-private/protected methods or fields that exist purely so tests can reach them, and either annotate (`com.google.common.annotations.VisibleForTesting`) or move into the test source tree.
- **Strict null-safety with Maven hard-check.** No nullability annotations in the codebase today. Adopt JSpecify (`org.jspecify:jspecify`) for `@Nullable`/`@NonNull` and add Error Prone + NullAway in the compiler plugin so the build fails on potential NPEs (the BitcoinAddressFinder pom.xml already does this and is a working reference).
- **At least one LogCaptor smoke test.** SLF4J is used by the plugin. Add a `LogCaptor.forClass(...)` test that confirms a known log message actually fires through the configured pipeline, so a future binding/configuration regression is caught at test time rather than silently swallowed.

- **`@VisibleForTesting` design-fit review.** Complement to the audit above: for every existing or planned `@VisibleForTesting` usage, ask whether widening access is the cleanest path to testability. Common alternatives that should be preferred when applicable: (a) inject the dependency through the constructor and have the test pass a stub or fake; (b) extract the tested behaviour into a separate testable helper class with public methods; (c) restructure the production API so what the test wants to verify is observable through normal public methods. Only keep the annotation where these alternatives are materially worse. `@VisibleForTesting` should be the last resort, not the first.

- **Package hierarchy review.** Walk the full `src/main/java/.../` tree and assess whether the current package layout still expresses the design intent. Look for: classes that have drifted into the wrong package as the codebase grew; flat "kitchen-sink" packages that should be split (high class count, mixed concerns); deeply nested packages that fragment cohesive components; circular dependencies between packages; missing seams where a sub-package boundary would prevent leaking implementation details. Produce a target tree as a separate planning step BEFORE making any moves — large package refactors are expensive to review and easy to do twice if the target isn't clear up front.

- **Class and method naming review (pair with the package hierarchy work).** While the package hierarchy review is in flight, also audit class and method names for the same kinds of drift: stale names that no longer describe what the class actually does after years of growth; over-abbreviated or cryptic identifiers (`Utils`, `Helper`, `Mgr`, `do*`, `process*`) that hide responsibilities; method names whose verbs do not match the actual side effects (named `get*` but writes, named `is*` but mutates, etc.); name collisions across packages that force qualified imports everywhere. Renames are far cheaper to do INSIDE a package-restructure commit than as standalone follow-ups (one IDE refactor pass touches both the move and the rename), so capture name changes in the same target tree as the package plan rather than as a separate later step.
