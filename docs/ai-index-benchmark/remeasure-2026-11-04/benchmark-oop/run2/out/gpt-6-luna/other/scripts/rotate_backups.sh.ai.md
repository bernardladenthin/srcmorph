### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:38:02Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates timestamped compressed backups of a source directory and prunes older archives to retain a configured number.

#### Purpose
- Back up one directory as a dated `.tar.gz` archive and retain the newest requested number of archives for that directory.
- Run directly as `rotate_backups.sh`; not a sourced library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`: directory to back up; required.
- Positional argument 2 → `BACKUP_DIR`: archive destination; required.
- Positional argument 3 → `KEEP`: positive archive count; defaults to `7`.
- No option parser or environment variables read.
- Reads the contents of `SOURCE_DIR`; lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a compressed archive named `<source-basename>_<UTC timestamp>.tar.gz` and prints its path and human-readable size.
- Deletes matching older archives beyond the newest `KEEP`, printing each removed path.
- Writes errors to stderr for invalid `KEEP` or missing `SOURCE_DIR`.

#### Core logic
- Validate that `KEEP` contains a positive integer; otherwise exit with status `2`.
- Check that `SOURCE_DIR` exists as a directory; otherwise exit with status `2`.
- Create `BACKUP_DIR`, derive the source basename, and construct a UTC-timestamped archive path plus `.partial` temporary path.
- Install an `EXIT` trap to remove the partial archive.
- Run `tar` from the source directory’s parent to create the compressed temporary archive, then rename it to the final path.
- Print the archive path and size.
- List matching archives newest first and delete all entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = array populated by `mapfile -t` from the archive listing.
- `old` = each archive selected for deletion by the pruning loop.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Uses Bash built-ins and syntax: `[[ ]]`, `mapfile`, process substitution, array slicing, and `trap`.
- Archive input and output paths are under `SOURCE_DIR` and `BACKUP_DIR`; no remote hosts, endpoints, or database tables.

#### Errors and exit codes
- Exit `2` when `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Required-argument expansion fails when either `SOURCE_DIR` or `BACKUP_DIR` is omitted; `set -u` and `set -e` can terminate execution on unset values or command failures.
- `EXIT` trap removes `tmp` if present.
- No explicit retry or timeout logic.
