# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## Release Process

> Paste this prompt into a new Claude Code session, fill in the three placeholders, and send it to perform a release.

```
Release `{PROJECT}` to Maven Central.

**Step 1 — Prepare the release (do immediately):**
1. Read the current version from `pom.xml` on `main` — it will be `{VERSION}-SNAPSHOT`
2. Strip `-SNAPSHOT` from `pom.xml` (→ `{VERSION}`)
3. In `README.md`, update **both**:
   - The release dependency example to `{VERSION}`
   - The snapshot dependency example to `{VERSION}-SNAPSHOT` (it should already match, but verify)
4. Commit both files directly to `main` (no pull request)

**Step 2 — Wait for manual confirmation:**
I will create the `v{VERSION}` tag and GitHub release manually — wait for me to confirm
the release is published on Maven Central before proceeding.

**Step 3 — Post-release snapshot bump (after my confirmation):**
Bump **both** files on `main`:
- `pom.xml` → `{NEXT_VERSION}-SNAPSHOT`
- `README.md` snapshot dependency example → `{NEXT_VERSION}-SNAPSHOT`

Commit both changes together directly to `main`.

**Placeholders:**

| Placeholder      | Value                                        |
|------------------|----------------------------------------------|
| `{PROJECT}`      | *(project name)*                             |
| `{VERSION}`      | *(release version, e.g. `1.3.0`)*           |
| `{NEXT_VERSION}` | *(next snapshot base, e.g. `1.3.1`)*        |
```

---

## [Unreleased]

### Added
- OpenSSF Best Practices badge and passing-level artifacts (CONTRIBUTING.md, SECURITY.md, CHANGELOG.md).

### Changed
- Switched runtime dependency to `net.ladenthin:llama` 5.0.0 (official Maven Central release).
- CI: added `startgate` abort-window environment before publish pipeline.
- CI: separated snapshot and release publish paths; added `check-snapshot` / `check-tag` gate jobs.
- CI: bumped `softprops/action-gh-release` v2 → v3 (Node 24 compatibility).
- CI: added JaCoCo coverage reporting with Coveralls and Codecov integration.
- README: grouped badges by category (Build / Coverage / Package / License / Community); added Maven Central dependency section.

### Fixed
- CI: quoted gate job names to avoid YAML colon-in-scalar parsing error.
- CI: use `GITHUB_TOKEN` for Coveralls `github-token` parameter instead of `COVERALLS_TOKEN`.
