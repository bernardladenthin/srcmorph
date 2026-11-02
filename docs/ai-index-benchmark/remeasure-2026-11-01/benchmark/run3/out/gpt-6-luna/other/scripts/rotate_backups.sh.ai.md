### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:06Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated compressed archives of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory and retain only the newest requested number of its archives.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional `$1` → `SOURCE_DIR`; required source directory.
- Positional `$2` → `BACKUP_DIR`; required destination directory.
- Positional `$3` → `KEEP`; optional positive integer archive count, default `7`.
- Reads `SOURCE_DIR` to verify and archive the directory; reads and lists matching archives in `BACKUP_DIR`.
- No options, environment variables, or sourced files.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `${name}_<UTC timestamp>.tar.gz`; prints its path and human-readable size.
- Deletes matching archives beyond the newest `KEEP`; prints each removed path.
- Exits with status `2` for invalid `KEEP` or a missing source directory. Under `set -e`, command failures terminate with the failing status.

#### Core logic
- Require `SOURCE_DIR` and `BACKUP_DIR`; set `KEEP` to its argument or `7`.
- Validate `KEEP` as a positive integer; check that `SOURCE_DIR` exists as a directory.
- Create `BACKUP_DIR`, derive the source basename, and form a UTC-dated archive path and `.partial` temporary path.
- Register an `EXIT` trap to remove the temporary path.
- Create the compressed archive from the source directory’s parent, then rename the temporary archive to its final path.
- Print the archive path and size.
- List matching archives newest first and remove every archive after the first `KEEP`.

#### Functions
- No functions defined.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = array populated from `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`
- `old` = each archive selected for removal

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Bash builtins/features: `mapfile`, arrays, process substitution, `[[ ... =~ ... ]]`.
- Uses the source directory’s basename as the archive name prefix; no remote hosts, endpoints, or database tables.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Missing required positional arguments trigger the `${...:?}` usage error.
- `EXIT` trap runs `rm -f "$tmp"` to clean up the partial archive.
- `set -e` exits on an unhandled command failure; `set -u` treats unset variables as errors; `pipefail` propagates pipeline failures.
