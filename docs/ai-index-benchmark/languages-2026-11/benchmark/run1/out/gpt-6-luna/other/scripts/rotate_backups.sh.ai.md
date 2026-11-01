### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:18Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated compressed archives of a source directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one directory and retain only the newest configured number of archives.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR`, source directory to archive.
- `$2` → `BACKUP_DIR`, destination directory for archives.
- `$3` → `KEEP`, optional positive-integer retention count; defaults to `7`.
- Reads the contents of `SOURCE_DIR` and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `${name}_<UTC timestamp>.tar.gz`; prints its path and human-readable size.
- Deletes older matching archives beyond the first `KEEP`, printing each removed path.
- Exits `2` for invalid `KEEP` or a missing source directory. With `set -e`, command failures terminate with their failure status.
- An `EXIT` trap removes the partial archive file.

#### Core logic
- Validate `KEEP` as a positive integer; reject invalid values with an error on stderr.
- Require `SOURCE_DIR` to exist as a directory; create `BACKUP_DIR`.
- Derive the source directory’s basename and construct a UTC-timestamped archive path plus a `.partial` temporary path.
- Register cleanup of the partial file on exit.
- Create the compressed tar archive from the source directory’s parent, then rename the partial file to its final name.
- Print the archive path and size.
- List matching archives newest first and remove every archive after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = matching archive paths returned by `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`
- `old` = each archive selected for removal

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Uses Bash builtins and features: `[[ ]]`, `mapfile`, process substitution, arrays, parameter expansion, `echo`, `trap`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- `${1:?…}` or `${2:?…}` aborts if the required argument is unset or empty.
- `set -e` terminates on an unhandled command failure; `set -u` rejects unset variables; `set -o pipefail` propagates pipeline failures.
- `EXIT` trap runs `rm -f "$tmp"` to remove an incomplete archive.
