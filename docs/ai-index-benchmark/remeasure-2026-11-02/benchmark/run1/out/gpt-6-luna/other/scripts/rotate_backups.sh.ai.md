### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:56Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Creates dated compressed archives of a source directory and removes older archives beyond the retention count.

#### Purpose
- Back up one directory as a timestamped `.tar.gz` archive and retain only the newest requested number of archives for that directory.
- Run directly with source and backup directory arguments.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- `$1` is assigned to `SOURCE_DIR`; required source directory path.
- `$2` is assigned to `BACKUP_DIR`; required backup directory path.
- `$3` is assigned to `KEEP`; optional positive-integer retention count, default `7`.
- Reads `SOURCE_DIR` and its contents; lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed and writes a compressed archive named `${name}_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints the created archive path and its human-readable size; prints each removed archive path.
- Deletes archives beyond the newest `KEEP` matching archives.
- Exits `2` for invalid `KEEP` or a missing source directory. With `set -euo pipefail`, an unhandled command or pipeline failure exits nonzero.
- An `EXIT` trap removes the partial archive file.

#### Core logic
- Assigns positional arguments; defaults `KEEP` to `7`.
- Validates that `KEEP` is a positive integer and that `SOURCE_DIR` exists as a directory; creates `BACKUP_DIR`.
- Builds an archive name from the source directory basename and the current UTC timestamp; registers cleanup of the `.partial` path.
- Archives the source directory with `tar`, then renames the partial archive to its final path and prints its size.
- Lists matching archives newest-first and removes all entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` is assigned by `mapfile -t` from the archive listing.
- `old` is assigned each archive selected for removal in the loop.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Bash features: `[[ ]]`, `mapfile`, process substitution, arrays, parameter expansion, and `EXIT` trap.

#### Errors and exit codes
- Exit `2` when `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Required-argument expansion fails if `$1` or `$2` is unset or empty.
- `EXIT` trap runs `rm -f "$tmp"` to clean up the partial archive.
- `set -euo pipefail` makes unset-variable, command, and pipeline failures fatal; no retries or timeouts.
