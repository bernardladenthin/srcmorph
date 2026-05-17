**Build:**  
![Java 8+](https://img.shields.io/badge/Java-8%2B-informational)  
[![Publish](https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/actions/workflows/publish.yml/badge.svg)](https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/actions/workflows/publish.yml)  
[![CodeQL](https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/actions/workflows/codeql.yml/badge.svg)](https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/actions/workflows/codeql.yml)  

**Coverage:**  
[![Coverage Status](https://coveralls.io/repos/github/bernardladenthin/llamacpp-ai-index-maven-plugin/badge.svg?branch=main)](https://coveralls.io/github/bernardladenthin/llamacpp-ai-index-maven-plugin?branch=main)  
[![codecov](https://codecov.io/gh/bernardladenthin/llamacpp-ai-index-maven-plugin/graph/badge.svg)](https://codecov.io/gh/bernardladenthin/llamacpp-ai-index-maven-plugin)  

**Security:**  
[![Known Vulnerabilities](https://snyk.io/test/github/bernardladenthin/llamacpp-ai-index-maven-plugin/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/bernardladenthin/llamacpp-ai-index-maven-plugin?targetFile=pom.xml)  
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fbernardladenthin%2Fllamacpp-ai-index-maven-plugin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fbernardladenthin%2Fllamacpp-ai-index-maven-plugin?ref=badge_shield)  
[![Dependencies](https://img.shields.io/librariesio/github/bernardladenthin/llamacpp-ai-index-maven-plugin)](https://libraries.io/github/bernardladenthin/llamacpp-ai-index-maven-plugin)  

**Package:**  
[![Maven Central](https://img.shields.io/maven-central/v/net.ladenthin/llamacpp-ai-index-maven-plugin)](https://central.sonatype.com/artifact/net.ladenthin/llamacpp-ai-index-maven-plugin)  
[![Snapshot](https://img.shields.io/badge/snapshot-latest-informational)](https://central.sonatype.com/repository/maven-snapshots/net/ladenthin/llamacpp-ai-index-maven-plugin/)  
![Release Date](https://img.shields.io/github/release-date/bernardladenthin/llamacpp-ai-index-maven-plugin)  
![Last Commit](https://img.shields.io/github/last-commit/bernardladenthin/llamacpp-ai-index-maven-plugin)  

**License:**  
[![License](https://img.shields.io/badge/License-Apache%202.0-orange)](./LICENSE)  

**Community:**  
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/12863/badge)](https://www.bestpractices.dev/projects/12863)  
[![OpenSSF Scorecard](https://api.scorecard.dev/projects/github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/badge)](https://scorecard.dev/viewer/?uri=github.com/bernardladenthin/llamacpp-ai-index-maven-plugin)  
[![Dependabot](https://img.shields.io/badge/Dependabot-enabled-success?logo=dependabot)](./.github/dependabot.yml)  
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg)](https://www.conventionalcommits.org/en/v1.0.0/)  
[![Keep a Changelog](https://img.shields.io/badge/changelog-Keep%20a%20Changelog-blue)](https://keepachangelog.com/en/1.1.0/)  
[![SemVer](https://img.shields.io/badge/SemVer-2.0.0-blue)](https://semver.org/spec/v2.0.0.html)  
[![REUSE](https://api.reuse.software/badge/github.com/bernardladenthin/llamacpp-ai-index-maven-plugin)](https://api.reuse.software/info/github.com/bernardladenthin/llamacpp-ai-index-maven-plugin)

# llamacpp-ai-index-maven-plugin
A Maven plugin for generating hierarchical, AI-readable documentation of source code projects using local llama.cpp-compatible models.
It creates structured `.ai.md` files per source file and aggregates them into package-level summaries for fast semantic navigation and retrieval.
## Features
- Generate AI summaries for Java source files
- Extract keyword metadata for search and indexing
- Aggregate summaries at package level
- Uses local models via llama.cpp (no cloud dependency)
- Incremental updates (skips unchanged files)
- Optimized for AI-assisted code understanding
## How It Works
The plugin runs in two phases.
### 1. File Generation (generate)
- Scans configured source directories
- Creates `.ai.md` files per source file
- Each file contains metadata header and markdown summary
### 2. Package Aggregation (aggregate-packages)
- Traverses generated `.ai.md` files
- Builds hierarchical package summaries
- Produces `package.ai.md` files
## Example Output
```
### AiMdDocument.java
- H: 1.0
- C: A48CED8C
- D: 2026-03-15T23:31:52Z
- T: 2026-03-19T18:13:31Z
- G: 0.1.0-SNAPSHOT
- X: file
- K: AiMdDocument, AiMdHeader, record, metadata, markdown
#### AiMdDocument.java
Represents a document consisting of a structured metadata header and a markdown body. Ensures non-null invariants and encapsulates AI-generated content.
```
## Requirements
- Java 8+ (production code targets Java 8; CI builds on Java 8 via temurin)
- Maven 3.6.3+
- Local GGUF model (llama.cpp compatible)

## Dependency

The plugin depends on [`net.ladenthin:llama`](https://central.sonatype.com/artifact/net.ladenthin/llama), the Java/JNI binding for llama.cpp.
It is published on Maven Central and resolves automatically — no manual installation required.

```xml
<dependency>
    <groupId>net.ladenthin</groupId>
    <artifactId>llama</artifactId>
    <version>5.0.0</version>
</dependency>
```
## Configuration
Minimal setup in POM:
```
<properties>
    <ai.index.model.path>/path/to/model.gguf</ai.index.model.path>
    <ai.index.output.directory>${project.basedir}/src/site/ai</ai.index.output.directory>
</properties>
```
## Usage
Run AI index generation:
```
mvn clean install -Pai-index-selftest
```
With native llama tests:
```
mvn clean install -Pai-index-selftest -DrunNativeLlamaTests=true
```
## Plugin Configuration
Key parameters:
- outputDirectory: target directory for `.ai.md` files
- subtrees: source directories to index
- summaryProvider: AI backend (`llamacpp-jni`)
- llamaModelPath: path to GGUF model
- llamaContextSize: context window
- llamaMaxTokens: output token limit
- llamaTemperature: sampling temperature
- llamaThreads: CPU threads
## Prompt System
The plugin uses configurable prompts:
- file-summary
- file-keywords
- package-summary
- package-keywords
Prompts are optimized to avoid code blocks, formatter artifacts, empty outputs, and produce structured markdown.
## Output Structure
```
src/site/ai/
└── main/
    └── java/
        └── com/
            └── example/
                ├── MyClass.java.ai.md
                ├── AnotherClass.java.ai.md
                └── package.ai.md
```
## Design Principles
- Deterministic metadata (hash-based change detection)
- Separation of concerns (header = metadata, body = summary)
- AI-friendly structure (predictable and hierarchical)
- Local-first (no external APIs required)
## Known Limitations
- Model output may require normalization (handled in code)
- Large models increase runtime
- Output quality depends on chosen model
## Recommended Models
- Qwen2.5 Coder (balanced quality and speed)
- Smaller instruct models for faster indexing
## Development
Run full build:
```
mvn clean install
```
Skip AI generation:
```
mvn clean install -DaiIndex.skip=true
```
## License
Apache License 2.0