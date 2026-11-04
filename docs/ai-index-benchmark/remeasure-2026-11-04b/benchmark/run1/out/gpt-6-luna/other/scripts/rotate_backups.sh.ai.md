### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:46Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates timestamped compressed backups of a source directory and prunes older archives to retain a configured number.

#### Purpose
- Back up one directory into dated `.tar.gz` archives and retain the newest requested number.
- Run directly with source and backup directory arguments; not a sourced library.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional arguments: `$1` sets `SOURCE_DIR`; `$2` sets `BACKUP_DIR`; optional `$3` sets `KEEP`, defaulting to `7`.
- Requires `KEEP` to be a positive integer and `SOURCE_DIR` to exist as a directory.
- Reads the contents of `SOURCE_DIR`; examines matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed; writes a compressed timestamped archive and prints its path and human-readable size.
- Deletes archives beyond the newest `KEEP` and prints each removed path.
- Exits with status `2` for invalid `KEEP` or a missing source directory. With `set -e`, command failures terminate with a nonzero status.
- EXIT trap removes the temporary `.partial` archive.

#### Core logic
- Validate `KEEP` and confirm `SOURCE_DIR` exists; create `BACKUP_DIR`.
- Derive the source directory’s basename and construct a UTC-timestamped archive path plus a `.partial` temporary path.
- Register cleanup of the temporary file on exit.
- Create a gzip-compressed tar archive of the source directory’s contents in the temporary path, then rename it to the final archive path.
- Print the archive path and size.
- List matching archives newest-first and remove entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = array populated from the matching archive listing
- `old` = each archive selected for removal

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Uses Bash `mapfile` and process substitution.
- Archive naming pattern: `BACKUP_DIR/${name}_*.tar.gz`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Positional parameter expansion reports usage and terminates if `$1` or `$2` is unset.
- `set -e` terminates on failing commands; `set -u` treats unset variables as errors; `pipefail` propagates pipeline failures.
- EXIT trap runs `rm -f "$tmp"` to remove an unfinished archive.
