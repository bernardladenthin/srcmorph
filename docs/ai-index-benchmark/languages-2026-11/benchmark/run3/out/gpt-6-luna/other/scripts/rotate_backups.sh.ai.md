### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:00:24Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory and retain only the newest requested number of its archives.
- Run directly as `rotate_backups.sh`; not a sourced library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR`: directory to back up.
- `$2` → `BACKUP_DIR`: directory for backup archives.
- `$3` → `KEEP`: optional retention count; defaults to `7`.
- Reads `SOURCE_DIR` contents via `tar`; reads matching archive entries from `BACKUP_DIR`.
- No options or environment variables read.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Removes matching archives beyond the newest `KEEP`.
- Prints the created archive path and size, then each removed archive path.
- Exits `2` for an invalid retention count or missing source directory; exits `0` on success. With `set -e`, command failures terminate with a nonzero status.

#### Core logic
- Assigns the source directory, backup directory, and retention count from positional arguments.
- Rejects `KEEP` unless it is a positive integer; rejects a nonexistent source directory.
- Creates `BACKUP_DIR`, derives the source basename, and constructs a UTC-dated archive path plus a `.partial` temporary path.
- Installs an `EXIT` trap to remove the temporary archive.
- Creates the compressed archive from the source directory’s parent, then moves the temporary file to its final path.
- Prints the archive path and human-readable size.
- Lists matching archives newest-first and deletes entries after the first `KEEP`.

#### Functions

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = archive paths returned by `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`
- `old` = each archive selected for removal from `"${archives[@]:$KEEP}"`

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Uses Bash built-ins: `mapfile`, `echo`, `[[ ]]`, and `trap`.
- Archive format: gzip-compressed tar (`.tar.gz`); no remote hosts, endpoints, or database tables.

#### Errors and exit codes
- Exits `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- `EXIT` trap runs `rm -f "$tmp"` to remove the partial archive.
- `set -e` causes termination on an unhandled failing command; `set -u` rejects unset-variable use; `pipefail` propagates pipeline failures.
- No retries or timeouts.
