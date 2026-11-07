### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:33Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates timestamped compressed backups of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory as a dated `.tar.gz` archive, retaining only the newest requested number of archives.
- Run directly as `rotate_backups.sh`; not a sourced library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`; required source directory.
- Positional argument 2 → `BACKUP_DIR`; required destination directory.
- Positional argument 3 → `KEEP`; optional retention count, defaults to `7`; must be a positive integer.
- Reads the contents of `SOURCE_DIR`; lists matching archives in `BACKUP_DIR`.
- Reads no environment variables.

#### Output
- Creates `BACKUP_DIR` if needed and writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Prints the created archive path and size; prints each removed archive path.
- Deletes matching older archives beyond the newest `KEEP`.
- Exits with status `2` for invalid `KEEP` or a missing source directory. With `set -e`, failures of other commands also terminate the script.

#### Core logic
- Requires `SOURCE_DIR` and `BACKUP_DIR`; sets `KEEP` from argument 3 or defaults it to `7`.
- Validates `KEEP` against a positive-integer pattern; rejects invalid values with an error on stderr.
- Verifies `SOURCE_DIR` exists as a directory; creates `BACKUP_DIR`.
- Builds an archive name from the source basename and a UTC timestamp; registers an `EXIT` trap to remove the partial archive.
- Runs `tar` from the source directory’s parent, writes to a `.partial` path, then renames the completed archive into place.
- Prints the archive path and human-readable size.
- Lists matching archives newest-first and removes entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` receives the matching archive paths from `ls -1t`.
- `old` receives each archive selected for removal by the retention loop.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`.
- Bash builtins/features: `[[ ]]`, `mapfile`, arrays, process substitution, `trap`.
- Operates on `SOURCE_DIR` and `BACKUP_DIR`; no remote hosts, services, or endpoints.

#### Errors and exit codes
- Status `2`: missing required positional argument, invalid `KEEP`, or `SOURCE_DIR` is not a directory.
- `set -euo pipefail`: command failures, unset-variable expansions, and pipeline failures terminate execution.
- `EXIT` trap removes `tmp` if present.
