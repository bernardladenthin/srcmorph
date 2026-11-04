# Contributing to srcmorph

Thank you for your interest in contributing to **srcmorph**, a prompt-driven source-tree transformer for generating AI-readable hierarchical documentation of Java source projects using local llama.cpp-compatible models.

---

## How to build and run

### Prerequisites

- JDK 25 to build (the version in `.java-version`, which CI takes, Temurin); the production code of
  `srcmorph` is compiled for Java 8 (`maven.compiler.release` 8), its tests for Java 21
- Maven 3.6.3+

### Common commands

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
mvn clean install -Psrcmorph-selftest

# Skip AI generation during build
mvn clean install -Dsrcmorph.skip=true
```

### Offline / restricted-network environments

If Maven cannot reach the internet, use the offline flag after a warm `~/.m2` cache:

```bash
mvn test -o
mvn package -o -DskipTests
```

---

## Reporting issues

Please use the GitHub issue tracker:

https://github.com/bernardladenthin/srcmorph/issues

When filing a bug report, include:
- Java version (`java -version`) and Maven version (`mvn -version`)
- The full command you ran
- Relevant stack trace or log output
- A minimal reproduction case if possible

For feature requests, describe the use case and why the current behavior does not satisfy it.

Security-sensitive issues must **not** be filed in the public tracker. See [SECURITY.md](./SECURITY.md) for the private reporting channel.

---

## Pull request workflow

1. **Fork** the repository on GitHub.
2. **Create a feature branch** from `main`:
   ```bash
   git checkout -b feat/my-feature
   ```
3. **Make your changes** and commit with a clear message (see Commit message convention below).
4. **Push** your branch to your fork:
   ```bash
   git push -u origin feat/my-feature
   ```
5. **Open a Pull Request** against the `main` branch on GitHub.
6. Respond to any review feedback. Maintainers may request changes before merging.

Pull requests are expected to pass all CI checks (build, test, CodeQL) before merge.

---

## Coding standards

- **Java version:** The production code of `srcmorph` is compiled with `--release 8`; do not use APIs unavailable on Java 8 in `src/main` (the compiler rejects them).
- **Encoding:** UTF-8 throughout.
- **License headers:** Every file carries an SPDX header (`SPDX-FileCopyrightText` and `SPDX-License-Identifier`), or an entry in `REUSE.toml` where it cannot hold one; `reuse lint` checks it. Copy the header from any existing source file.
- **Named constants:** Every meaningful string literal, header field name, and node type must be declared as a `public static final` or `private static final` constant with Javadoc.
- **Null safety:** Mark nullable parameters and return types explicitly; prefer early null/empty guards with a log warning over silent skips.
- **Value carriers:** Java 8 has no `record`; write a final class with final fields (Lombok's `@ToString` / `@EqualsAndHashCode` where the classes around it use them) and mark it `@ConvertToRecord` (`support.ConvertToRecord`) for the move to a later baseline.
- **Logging:** Use SLF4J (`org.slf4j.Logger`, one `private static final Logger LOGGER` per class) in the library and the CLI; the Maven plugin's Mojos log through Maven's `getLog()`.
- **Javadoc:** Use HTML entities for operators (`&lt;`, `&gt;`, `&#x2264;`, etc.); never use bare Unicode symbols in Javadoc.

Consult [CLAUDE.md](./CLAUDE.md) and [CODE_WRITING_GUIDE.md](./CODE_WRITING_GUIDE.md) at the root of the repository for the full set of conventions enforced on every production file.

---

## Test policy

> Every new feature or behavior change MUST include automated tests. Pull requests that add or change functionality without corresponding tests will be asked to add tests before merge. Bug fixes SHOULD include a regression test.

Tests are written with **JUnit 5 (Jupiter)** and **Hamcrest**. Use `MockAiGenerationProvider` (or a lambda `AiGenerationProvider`) for all tests that do not explicitly exercise the llama.cpp JNI backend. Tests that call the real JNI must guard with `NativeLlamaAvailability` (a JUnit assumption) so the suite remains runnable on machines without the native library.

See [TEST_WRITING_GUIDE.md](./TEST_WRITING_GUIDE.md) at the root of the repository for the full test conventions.

---

## Commit message convention

This project follows [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>(<optional scope>): <short summary>

<optional body>

<optional footer(s)>
```

Common types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `ci`, `build`, `perf`.

Examples:

```
feat(generate): add force flag to bypass checksum cache
fix(codec): close reader on IOException
docs(security): publish maintainer fallback email
```

---

## Communication channels

- **GitHub Issues** — bug reports, feature requests, and questions:
  https://github.com/bernardladenthin/srcmorph/issues
- **GitHub Security Advisories** — private vulnerability reports:
  https://github.com/bernardladenthin/srcmorph/security/advisories/new

---

## Code of Conduct

This project adheres to a Contributor Covenant–based [Code of Conduct](./CODE_OF_CONDUCT.md). By participating, you agree to uphold it. Report unacceptable behavior to the maintainer contact listed in that document.

---

## Releasing

The release procedure (prompt template, version-bump steps, snapshot bump) lives in [docs/RELEASE.md](./docs/RELEASE.md). Only maintainers cut releases.

---

## License of contributions

By submitting a contribution (pull request, patch, or other submission) to this repository, you agree that your contribution will be licensed under the **Apache License, Version 2.0**, the same license that covers the project. See [LICENSE](./LICENSE) for the full text.
