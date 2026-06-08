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
│   │   └── java/net/ladenthin/maven/llamacpp/aiindex/   # layered packages (top → bottom)
│   │       ├── mojo/        # Entry: AbstractAiIndexMojo, GenerateMojo, AggregatePackagesMojo
│   │       ├── indexer/     # Orchestration: SourceFileIndexer, PackageIndexer, AiFieldGenerationSupport
│   │       ├── provider/    # AI backends: AiGenerationProvider(+Factory), Mock/LlamaCppJni providers,
│   │       │                #   AiCompletionParser, LlamaCppJniConfig
│   │       ├── document/    # .ai.md model + codecs: AiMdDocument, AiMdHeader, AiMd*Codec,
│   │       │                #   AiMdHeaderSupport, AiMdChildEntryLineFormatter,
│   │       │                #   AiGenerationRequest, AiGenerationResult (carry an AiMdHeader)
│   │       ├── prompt/      # AiPromptDefinition, AiPreparedPrompt, AiPromptSupport, AiPromptPreparationSupport
│   │       ├── config/      # AiGenerationConfig, AiGenerationKind, AiFieldGenerationConfig,
│   │       │                #   AiModelDefinition, AiModelDefinitionSupport
│   │       └── support/     # Foundation: AiChecksumSupport, AiTimeSupport, AiPathSupport,
│   │                        #   Java8CompatibilityHelper, ConvertToRecord
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
| `SourceFileIndexer` | Walks source trees, creates `.ai.md` files, calls AI to fill the document body |
| `PackageIndexer` | Creates `package.ai.md` files with contents listings, calls AI to fill the document body |
| `AiGenerationProvider` | Interface for AI backends (llama.cpp JNI or mock) |
| `AiFieldGenerationSupport` | Shared field-generation loop extracted from both indexers |
| `AiGenerationResult` | Immutable carrier for the AI-generated body text out of the loop |
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
-->
This class handles parsing of Markdown headers...
(AI-generated body text continues here)
```

The header carries only deterministic metadata. All AI-generated
content lives in the document body after the header block, keeping
the header machine-parseable without AI involvement (see
`AiMdHeader.java` Javadoc for the rationale).

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

### Provider Pattern

`AiGenerationProvider` is a `Closeable` interface for AI backends:

| Implementation | Description |
|---|---|
| `LlamaCppJniAiGenerationProvider` | Uses the `net.ladenthin:llama` JNI binding to run local GGUF models |
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
| `generationProvider` | `aiIndex.generationProvider` | `mock` | `mock` or `llamacpp-jni` |
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

`src/test/resources/SmolLM2-135M-Instruct-Q3_K_M.gguf` is a small (≈90 MB) GGUF model used by integration tests that exercise the real `LlamaCppJniAiGenerationProvider`. These tests are skipped when the JNI native library is unavailable.

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

Immutable value types are implemented as Java `record` types (e.g., `AiMdDocument`, `AiMdHeader`, `AiPreparedPrompt`, `AiGenerationRequest`). Prefer records for data carriers.

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
| `net.ladenthin:llama` | 5.0.2 | llama.cpp JNI binding (GGUF inference); pinned to the layered-package + immutable-wither API. Brings `slf4j-api` transitively, converged to 2.0.18 via `<dependencyManagement>`. |
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

- For `*Test.java` files, follow the workspace version chain:
  [`../workspace/guides/test/TEST_WRITING_GUIDE-8.md`](../workspace/guides/test/TEST_WRITING_GUIDE-8.md)
  (this repo is Java 8) **and** this repo's own `TEST_WRITING_GUIDE.md`
  (plugin-specific supplement).
- For production sources, follow the workspace version chain:
  [`../workspace/guides/src/CODE_WRITING_GUIDE-8.md`](../workspace/guides/src/CODE_WRITING_GUIDE-8.md)
  (this repo is Java 8) **and** this repo's own `CODE_WRITING_GUIDE.md`.
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

## Lombok Config

See [`../workspace/policies/lombok-config.md`](../workspace/policies/lombok-config.md).

## JPMS Module Descriptor

This repo ships a `module-info.java` compiled in a separate `release 9` execution. Javadoc
currently runs in **classpath mode** (javadoc `<source>` resolves to `8`), which is the *only*
thing keeping it clear of the JPMS module-mode javadoc trap that bit BAF. **Before raising the
Java / javadoc source level to ≥ 9, read**
[`../workspace/policies/jpms-module-descriptor.md`](../workspace/policies/jpms-module-descriptor.md).

## Open TODOs

Open TODOs for this repo live in [`TODO.md`](TODO.md). Cross-repo status
tracking lives in [`../workspace/crossrepostatus.md`](../workspace/crossrepostatus.md).
