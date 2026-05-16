# Contributing to llamacpp-ai-index-maven-plugin

Thank you for your interest in contributing to **llamacpp-ai-index-maven-plugin**, a free Maven plugin for generating AI-readable hierarchical documentation of Java source projects using local llama.cpp-compatible models.

---

## 1. How to Build and Run

### Prerequisites

- Java 21+
- Maven 3.6.3+

### Common Commands

```bash
# Compile production sources
mvn compile

# Run all tests
mvn test

# Build the plugin JAR (with sources and Javadoc)
mvn package

# Build without running tests
mvn package -DskipTests

# Install to your local Maven repository (~/.m2)
mvn install

# Run the plugin against itself (self-test; requires a local GGUF model)
mvn clean install -Pai-index-selftest

# Run with native llama.cpp JNI tests enabled
mvn clean install -Pai-index-selftest -DrunNativeLlamaTests=true

# Skip AI generation during build
mvn clean install -DaiIndex.skip=true
```

### Offline / Restricted-Network Environments

If Maven cannot reach the internet, use the offline flag after a warm `~/.m2` cache:

```bash
mvn test -o
mvn package -o -DskipTests
```

---

## 2. Filing Issues

Please use the GitHub issue tracker:

https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/issues

When filing a bug report, include:
- Java version (`java -version`) and Maven version (`mvn -version`)
- The full command you ran
- Relevant stack trace or log output
- A minimal reproduction case if possible

For feature requests, describe the use case and why the current behavior does not satisfy it.

---

## 3. Pull Request Workflow

1. **Fork** the repository on GitHub.
2. **Create a feature branch** from `main`:
   ```bash
   git checkout -b feat/my-feature
   ```
3. **Make your changes** and commit with a clear message:
   ```bash
   git commit -m "feat: add support for XYZ"
   ```
4. **Push** your branch to your fork:
   ```bash
   git push -u origin feat/my-feature
   ```
5. **Open a Pull Request** against the `main` branch on GitHub.
6. Respond to any review feedback. Maintainers may request changes before merging.

All CI checks (build, test, CodeQL) must pass before a PR can be merged.

---

## 4. Coding Standards

- **Java version:** Production code targets Java 8 (source/target = 1.8); do not use APIs unavailable on Java 8 in `src/main`.
- **Encoding:** UTF-8 throughout.
- **License headers:** All `.java` files must include the Apache 2.0 license header wrapped in `// @formatter:off` / `// @formatter:on`. Copy the header from any existing source file.
- **Named constants:** Every meaningful string literal, header field name, and node type must be declared as a `public static final` or `private static final` constant with Javadoc.
- **Null safety:** Mark nullable parameters and return types explicitly; prefer early null/empty guards with a log warning over silent skips.
- **Records:** Use Java `record` types for immutable value carriers.
- **Logging:** Use `org.apache.maven.plugin.logging.Log` (not SLF4J), obtained via `getLog()` or constructor injection.
- **Javadoc:** Use HTML entities for operators (`&lt;`, `&gt;`, `&#x2264;`, etc.); never use bare Unicode symbols in Javadoc.

Consult `CLAUDE.md` and `CODE_WRITING_GUIDE.md` at the root of the repository for the full set of conventions enforced on every production file.

---

## 5. Test Policy

> Every new feature or behavior change MUST include automated tests. Pull requests that add or change functionality without corresponding tests will be asked to add tests before merge. Bug fixes SHOULD include a regression test.

Tests are written with **JUnit 4** and **Hamcrest**. Use `MockAiGenerationProvider` for all tests that do not explicitly exercise the llama.cpp JNI backend. Tests that call the real JNI must guard with a JNI-availability check.

See `TEST_WRITING_GUIDE.md` at the root of the repository for the full test conventions.

---

## 6. Communication Channels

- **GitHub Issues** — bug reports, feature requests, and questions:
  https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/issues

- **GitHub Discussions** (if enabled on the repository) — open-ended conversation about usage and design.

---

## 7. License of Contributions

By submitting a contribution (pull request, patch, or other submission) to this repository, you agree that your contribution will be licensed under the **Apache License, Version 2.0**, the same license that covers the project. See [LICENSE](./LICENSE) for the full text.
