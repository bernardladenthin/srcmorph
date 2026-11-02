### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:45:24Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Creates timestamped compressed backups of a directory and removes older archives beyond the retention limit.

#### Purpose
- Archive one source directory and retain only the newest requested number of its backups.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash (`#!/usr/bin/env bash`); `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`: source directory.
- Positional argument 2 → `BACKUP_DIR`: backup destination directory.
- Positional argument 3 → `KEEP`: number of archives to retain; defaults to `7`.
- Requires `KEEP` to be a positive integer and `SOURCE_DIR` to exist as a directory.
- Reads the contents of `SOURCE_DIR`; lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Deletes older matching archives beyond `KEEP`.
- Prints the created archive and its human-readable size; prints each removed archive.
- Exits `2` for invalid `KEEP` or missing `SOURCE_DIR`; exits `0` on success. With `set -e`, command failures terminate with a nonzero status.

#### Core logic
- Reads the source directory, backup directory, and retention count from positional arguments.
- Validates `KEEP`; reports an error and exits `2` if it is not a positive integer.
- Verifies `SOURCE_DIR` exists; reports an error and exits `2` otherwise.
- Creates `BACKUP_DIR`, derives the source basename, and builds a UTC timestamped archive path with a `.partial` temporary path.
- Registers an `EXIT` trap to remove the partial archive.
- Creates the tar.gz archive from the source directory’s parent, then renames the partial file to its final path.
- Prints the archive path and size.
- Lists matching archives newest-first and deletes entries after the first `KEEP`.

#### Functions
- No functions defined.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — array populated by `mapfile -t` from the newest-first archive listing.
- `old` — loop variable for archives beyond the retention limit.

#### Dependencies
- Bash builtins: `mapfile`, `set`, `trap`, `echo`.
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Uses `tar` to archive the source directory and `ls -1t` to select matching `BACKUP_DIR/${name}_*.tar.gz` files.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Argument expansion fails if `SOURCE_DIR` or `BACKUP_DIR` is unset or empty.
- `set -e` stops execution on an unhandled command failure; `set -u` rejects unset variables; `pipefail` propagates pipeline failures.
- `EXIT` trap removes `tmp` if it remains.
