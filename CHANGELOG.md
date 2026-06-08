# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The release procedure (prompt template and step-by-step instructions) lives in [docs/RELEASE.md](./docs/RELEASE.md).

---

## [Unreleased]

## [1.0.0] - 2026-06-08

First public release on Maven Central. Pre-OpenSSF history themes (March–May 2026): Java 8 compatibility, key-indexed `aiDefinitions` (PR #21), Sonatype Central Portal migration, JaCoCo+Coveralls+Codecov, GH Actions major-version bumps, CodeQL v3→v4, model catalogue (Qwen2.5-Coder, Ministral 8B/14B, Gemma 4 MoE).

### Added
- OpenSSF Best Practices badge and passing-level artifacts (CONTRIBUTING.md, SECURITY.md, CHANGELOG.md).

### Changed
- Switched runtime dependency to `net.ladenthin:llama` 5.0.2 (official Maven Central release).
- CI: added `startgate` abort-window environment before publish pipeline.
- CI: separated snapshot and release publish paths; added `check-snapshot` / `check-tag` gate jobs.
- CI: bumped `softprops/action-gh-release` v2 → v3 (Node 24 compatibility).
- CI: added JaCoCo coverage reporting with Coveralls and Codecov integration.
- README: grouped badges by category (Build / Coverage / Package / License / Community); added Maven Central dependency section.

### Fixed
- CI: quoted gate job names to avoid YAML colon-in-scalar parsing error.
- CI: use `GITHUB_TOKEN` for Coveralls `github-token` parameter instead of `COVERALLS_TOKEN`.

---

[Unreleased]: https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/releases/tag/v1.0.0
