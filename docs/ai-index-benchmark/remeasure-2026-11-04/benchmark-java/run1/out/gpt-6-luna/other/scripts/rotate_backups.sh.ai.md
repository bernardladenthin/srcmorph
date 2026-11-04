### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:51Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates a dated compressed archive of a directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one source directory as a timestamped `.tar.gz` archive and retain only the newest `KEEP` archives for that source.
- Run directly with source directory, backup directory, and optional retention count.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- Positional argument `$1` → `SOURCE_DIR`; required source directory.
- Positional argument `$2` → `BACKUP_DIR`; required archive directory.
- Positional argument `$3` → `KEEP`; optional positive integer, default `7`.
- Reads the contents of `SOURCE_DIR` for archiving and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes `BACKUP_DIR/${name}_<UTC timestamp>.tar.gz`, first as a `.partial` file, then renames it to the final archive.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Deletes matching archives after the newest `KEEP`.
- Exits with status `2` for an invalid `KEEP` or missing source directory. With `set -e`, failures of unguarded commands terminate the script.

#### Core logic
- Require `SOURCE_DIR` and `BACKUP_DIR`; set `KEEP` to the third argument or `7`.
- Validate `KEEP` as a positive integer; reject invalid values with a diagnostic.
- Check that `SOURCE_DIR` is a directory; create `BACKUP_DIR`.
- Derive the source basename and construct a UTC-timestamped archive path and `.partial` path.
- Register an `EXIT` trap to remove the partial file.
- Create the gzip-compressed tar archive from the source directory’s parent, then rename the partial file to the final path.
- Print the archive path and size.
- List matching archives newest-first and delete entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — populated by `mapfile -t` from the newest-first matching archive listing.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Bash built-ins/features: `[[ ]]`, `mapfile`, process substitution, array slicing, `trap`.
- Uses source directory contents and archive files matching `BACKUP_DIR/${name}_*.tar.gz`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer.
- Exit `2` if `SOURCE_DIR` is not a directory.
- Required-argument expansion fails when `$1` or `$2` is unset or empty; `set -u` also terminates on unset variables.
- `set -e` terminates on an unhandled command failure; `set -o pipefail` propagates pipeline failures.
- `EXIT` trap runs `rm -f "$tmp"` to remove the partial archive.
