### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:50Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates timestamped compressed backups of a directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one source directory as a dated `.tar.gz` archive and retain only the newest configured number.
- Run directly as `rotate_backups.sh`; not a sourced library.

#### Type
- Bash, from `#!/usr/bin/env bash`.
- Strict mode: `set -euo pipefail`.

#### Input
- Positional argument `$1` → `SOURCE_DIR`; required source directory.
- Positional argument `$2` → `BACKUP_DIR`; required backup destination.
- Positional argument `$3` → `KEEP`; optional retention count, default `7`.
- Reads the contents of `SOURCE_DIR` and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a UTC-timestamped `${name}_YYYYMMDDTHHMMSSZ.tar.gz` archive there; prints its path and human-readable size.
- Deletes archives beyond the newest `KEEP` matching archives; prints each removed path.
- Exits `2` for an invalid `KEEP` value or missing `SOURCE_DIR`. With `set -e`, failures of other commands also terminate the script.

#### Core logic
- Requires `SOURCE_DIR` and `BACKUP_DIR`; sets `KEEP` to its argument or `7`.
- Validates `KEEP` as a positive integer; checks that `SOURCE_DIR` is a directory.
- Creates `BACKUP_DIR`, derives the source directory’s basename, and builds a UTC-dated archive path and `.partial` temporary path.
- Installs an `EXIT` trap to remove the temporary archive.
- Archives the source directory from its parent directory, then renames the completed temporary archive and prints its size.
- Lists matching archives newest first and removes entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = array populated from `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`
- `old` = each archive selected for deletion by `"${archives[@]:$KEEP}"`

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Bash builtin: `mapfile`.
- Archive input: `SOURCE_DIR`; archive destination and retention search: `BACKUP_DIR`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Required-argument expansion fails when `$1` or `$2` is missing; `set -u` and `set -e` make this terminate.
- `EXIT` trap runs `rm -f "$tmp"` to clean up the partial archive.
- No retries or timeouts.
