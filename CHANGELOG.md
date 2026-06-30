# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

The release procedure (prompt template and step-by-step instructions) lives in [docs/RELEASE.md](./docs/RELEASE.md).

---

## [Unreleased]

### Changed
- Split the big-window size-routing rule per source kind in the `ai-index-selftest` example POM: the former single `big-window` rule is now `big-window-java` (prompt `file-body-java`), and a matching `big-window-sql` rule (prompt `file-body-sql`) routes oversized `.sql` sources to the same large-context model — so an oversized `.sql` file keeps the SQL prompt instead of being misrouted/uncovered.

## [1.0.1] - 2026-06-29

### Added
- Third-level **project index** (`aggregate-project` goal): a single `project.ai.md` table of contents harvesting each package's lead, with an optional one-call AI `#### Overview` paragraph.
- **Rule-based file routing** for the `generate` goal: each `<fieldGeneration>` routes a file to a `(model, prompt)` via a composable `<condition>` tree (`<and>`/`<or>`/`<not>` over `extensions`/`size`/`lines`/`modifiedAfter`/`modifiedBefore`/`pathGlob`), with `<priority>`, `<skip>`, and exactly one explicit `<fallback>`. Plan-then-execute loads each model once; `aiIndex.planOnly=true` prints the routing plan tree without loading a model.
- Plan-time **context-window fit check**: oversized files fail the build up front; new big-window fallback preset (IBM Granite 4.0-H-Tiny, Apache-2.0) covers source files up to ~1 MB.
- Independently switchable phases via `aiIndex.file.skip` / `aiIndex.package.skip` / `aiIndex.project.skip` (plus the global `aiIndex.skip`).
- File-size **band filter** (`minFileSizeBytes` / `maxFileSizeBytes`) for size-tiered indexing, and `excludes` globs to skip generated/trivial sources.
- Deterministic child-link list (`F` header field) for project → package → file navigation.
- Per-file **progress bar with ETA** and measured (actual vs estimated) per-file generation time.
- **GPU support**: opt-in `gpu-cuda` / `gpu-vulkan` profiles, parameterized native classifier, and `gpuLayers` / `mainGpu` / `devices` knobs.
- Opt-in sampling controls (default off): `min_p`, `top_n_sigma`, DRY repetition suppression, reasoning/think budget, and model-level `swa-full` + cache-reuse.
- Extension-selected file-body prompts (java / sql / fallback).

### Changed
- Default model preset is now `gpt-oss-20B-mxfp4`; all gpt-oss presets are GPU-ready.
- Pinned `net.ladenthin:llama` to the released `5.0.3` (dropped the SNAPSHOT dependency and snapshot repository).
- Bumped JUnit 6.1.0 → 6.1.1 and palantir-java-format 2.92.0 → 2.94.0.

### Removed
- The empty-body generation retry mechanism.

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

[Unreleased]: https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/compare/v1.0.1...HEAD
[1.0.1]: https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/bernardladenthin/llamacpp-ai-index-maven-plugin/releases/tag/v1.0.0
