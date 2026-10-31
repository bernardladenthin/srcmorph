### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:44:50Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and prunes older archives to keep only the newest N copies.

#### Purpose
- Automates backup creation for a specified source directory.
- Maintains a limited number of recent backups.

#### Type
- Bash script (`#!/usr/bin/env bash`).

#### Input
- `SOURCE_DIR` (first argument): directory to back up.
- `BACKUP_DIR` (second argument): destination for archives.
- `KEEP` (optional third argument, default `7`): number of recent archives to retain.

#### Output
- New `.tar.gz` archive in `BACKUP_DIR`.
- Console messages: creation size, removal notices.
- Exit status `0` on success; non‑zero on errors.

#### Core logic
- Validate `KEEP` is a positive integer.
- Verify `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name: `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Create temporary archive (`.partial`) with `tar -C` and gzip.
- Move temporary to final archive; report size via `du`.
- List existing archives sorted newest first (`ls -1t`).
- Delete all but the first `KEEP` archives; report each removal.

#### Public API
- Script entry point: `rotate_backups.sh <source_dir> <backup_dir> [keep]` – performs backup rotation.

#### Constants and configuration
- `KEEP` default value: `7`.
- `TMP_SUFFIX`: `.partial` (implicit in variable `tmp`).

#### Dependencies
- `tar`, `date`, `du`, `ls`, `rm`, `mkdir`, `basename`, `cut`, `echo`, `trap`.

#### Exceptions / Errors
- Exit `2` if `KEEP` invalid or `SOURCE_DIR` missing.
- `set -euo pipefail` causes immediate exit on any command failure.
