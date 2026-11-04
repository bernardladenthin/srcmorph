### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:07:23Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates a dated compressed archive of a directory and prunes older archives to retain the requested number.

#### Purpose
- Back up one source directory to a backup directory, retaining only the newest `KEEP` archives for that source.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`; required.
- Positional argument 2 → `BACKUP_DIR`; required.
- Positional argument 3 → `KEEP`; optional, defaults to `7`; must be a positive integer.
- Reads `SOURCE_DIR` and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed and writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Removes older matching archives beyond the retention count.
- Prints created archive and its human-readable size; prints each removed archive.
- Exits with status `2` for missing required arguments, invalid `KEEP`, or a nonexistent `SOURCE_DIR`; strict-mode command failures also cause nonzero exit.

#### Core logic
- Assigns positional arguments and validates `KEEP` against a positive-integer pattern.
- Checks that `SOURCE_DIR` exists as a directory; creates `BACKUP_DIR`.
- Builds an archive name from the source basename and UTC timestamp; sets a `.partial` temporary path and an `EXIT` trap to remove it.
- Runs `tar` from the source directory’s parent, then renames the partial archive to its final path.
- Prints the archive path and size.
- Lists matching archives newest-first and deletes entries after the first `KEEP`.

#### Functions
- None.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` populated by `mapfile -t` from the matching archive listing.
- `old` receives each archive selected for removal.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`.
- Reads the source directory contents and matching `BACKUP_DIR/${name}_*.tar.gz` archives.

#### Errors and exit codes
- Exit `2` if required positional arguments are absent, `KEEP` is not a positive integer, or `SOURCE_DIR` is not a directory.
- `set -e` exits on an unhandled failing command; `set -u` rejects unset variables; `set -o pipefail` propagates pipeline failures.
- `EXIT` trap removes the `.partial` archive path.
- No retries or timeouts.
