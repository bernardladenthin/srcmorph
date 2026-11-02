### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:43Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a source directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one directory and retain only the newest `KEEP` archives for that source.
- Run directly as `rotate_backups.sh <source_dir> <backup_dir> [keep=7]`.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`, directory to archive.
- Positional argument 2 → `BACKUP_DIR`, archive destination.
- Positional argument 3 → `KEEP`, positive integer retention count; defaults to `7`.
- Reads `SOURCE_DIR` and `BACKUP_DIR` directories and lists matching archives in `BACKUP_DIR`.
- Reads no environment variables.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Removes archives for that source beyond the newest `KEEP`.
- Prints the created archive path and size; prints each removed archive path.
- Writes validation errors to stderr.

#### Core logic
- Assigns the source, destination, and retention count from positional arguments.
- Rejects `KEEP` unless it is a positive integer; rejects a missing source directory.
- Creates `BACKUP_DIR`, derives the source basename, and builds a UTC-timestamped archive path and `.partial` temporary path.
- Installs an `EXIT` trap to remove the partial archive.
- Runs `tar` from the source’s parent directory to create the compressed archive at the temporary path, then renames it to the final path.
- Prints the final archive path and human-readable size.
- Lists matching archives newest-first and deletes entries after the first `KEEP`.

#### Functions
- No functions.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — populated by `mapfile` from the newest-first archive listing.
- `old` — each archive selected for deletion.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`.
- Uses Bash features: `[[ ... =~ ... ]]`, `mapfile`, process substitution, and array slicing.
- Archive selection uses `<source-basename>_*.tar.gz` in `BACKUP_DIR`.

#### Errors and exit codes
- Exits `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Missing required positional arguments trigger the `${...:?}` error and a nonzero exit.
- With `set -e`, an unsuccessful command terminates the script; with `set -u`, unset variables cause failure; `pipefail` propagates pipeline failures.
- `EXIT` trap removes `tmp` on exit, including after failure.
- No retries or timeouts.
