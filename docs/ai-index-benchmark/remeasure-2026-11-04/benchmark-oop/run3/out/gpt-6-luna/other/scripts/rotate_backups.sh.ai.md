### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:55Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates timestamped compressed archives of a directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one source directory and retain only the newest `KEEP` archives for that directory.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional arguments: `$1` → `SOURCE_DIR`; `$2` → `BACKUP_DIR`; `$3` → `KEEP` (defaults to `7`).
- No named options or environment variables are read.
- Reads `SOURCE_DIR` and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed and writes a dated `.tar.gz` archive there.
- Removes archives beyond the newest `KEEP` matching archives.
- Prints archive creation with its human-readable size, and each removed archive.
- Exits `2` for invalid `KEEP` or a missing source directory; strict mode can exit nonzero on command failures.

#### Core logic
- Assigns arguments; rejects `KEEP` unless it is a positive integer.
- Verifies `SOURCE_DIR` exists as a directory and creates `BACKUP_DIR`.
- Builds an archive name from the source basename and UTC timestamp; registers cleanup for the `.partial` file on exit.
- Archives the source directory with `tar`, then renames the partial archive to its final path.
- Prints the archive path and size.
- Lists matching archives newest-first and removes entries after the first `KEEP`.

#### Functions
- No functions defined.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = array populated from the newest-first archive listing.
- `old` = each archive selected for removal.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Bash builtins/features: `mapfile`, process substitution, arrays, `[[ ... =~ ... ]]`.
- Archive contents are rooted at `dirname "$SOURCE_DIR"` and include the source basename; archive matching uses `${name}_*.tar.gz` in `BACKUP_DIR`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- `${1:?…}` or `${2:?…}` terminates on missing required arguments.
- `set -e` makes command failures terminate with a nonzero status; `set -u` rejects unset-variable use; `set -o pipefail` propagates pipeline failures.
- `EXIT` trap removes `tmp` with `rm -f`.
