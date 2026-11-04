### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:58Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates dated compressed archives of a source directory and removes older archives beyond the retention count.

#### Purpose
- Back up one directory to a backup directory, retaining only the newest configured number of archives.
- Run directly as `rotate_backups.sh`; not a sourced library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR`: directory to archive.
- `$2` → `BACKUP_DIR`: destination directory.
- `$3` → `KEEP`: optional retention count; defaults to `7`.
- No options or environment variables read.
- Reads the contents of `SOURCE_DIR` and matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed and writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Removes older matching archives beyond the newest `KEEP`.
- Prints the created archive and its human-readable size; prints each removed archive.
- Exits `2` for an invalid retention count or missing source directory; exits `0` on success. Other command failures trigger `set -e`.

#### Core logic
- Requires `SOURCE_DIR` and `BACKUP_DIR`; assigns `KEEP` from the optional third argument or defaults it to `7`.
- Validates `KEEP` as a positive integer and verifies `SOURCE_DIR` exists as a directory.
- Creates `BACKUP_DIR`, derives the source basename, and builds a UTC-timestamped archive path plus a `.partial` temporary path.
- Registers an `EXIT` trap to remove the temporary archive.
- Creates the tar.gz archive from the source directory’s parent, then renames the completed temporary archive to its final path.
- Prints the archive path and size.
- Lists matching archives newest-first and removes all entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — populated by `mapfile` from the matching archive listing.
- `old` — each archive selected for removal by the retention loop.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Bash builtins: `mapfile`, `echo`, `exit`, `trap`.
- Uses the `BACKUP_DIR` directory and archive names matching `${name}_*.tar.gz`.

#### Errors and exit codes
- Missing required positional arguments fail through parameter expansion with the usage message.
- Exits `2` when `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- `EXIT` trap runs `rm -f "$tmp"` to clean up the partial archive.
- `set -euo pipefail` makes unset variables and command failures fatal; no retry or timeout logic.
