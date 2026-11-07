### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:59:12Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates timestamped compressed archives of a source directory and prunes older archives in the backup directory.

#### Purpose
- Back up one directory and retain only the newest requested number of its archives.
- Run directly with source and backup directory arguments.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- Positional `$1` → `SOURCE_DIR`; required source directory.
- Positional `$2` → `BACKUP_DIR`; required destination directory.
- Positional `$3` → `KEEP`; optional archive count, default `7`; must be a positive integer.
- Reads the contents of `SOURCE_DIR` for archiving and matching archives in `BACKUP_DIR` for pruning.
- No options or environment variables read.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `${name}_YYYYMMDDTHHMMSSZ.tar.gz` in `BACKUP_DIR`.
- Removes older matching archives beyond the newest `KEEP`.
- Prints the created archive and its human-readable size, then prints each removed archive.

#### Core logic
- Require `SOURCE_DIR` and `BACKUP_DIR`; set `KEEP` to the supplied value or `7`.
- Reject a non-positive-integer `KEEP` or a nonexistent `SOURCE_DIR` with an error and exit status `2`.
- Create `BACKUP_DIR`; derive the source basename and a UTC timestamped archive path.
- Register an `EXIT` trap to remove the `.partial` archive.
- Create the tarball from the source directory’s parent, move the completed temporary archive into place, and print its size.
- List matching archives newest first; delete and report entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — populated by `mapfile` from the newest-first archive listing.
- `old` — each archive selected for removal.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`, `dirname`.
- Bash built-ins: `mapfile`, `echo`, `[[ ... ]]`.
- Archive pattern: `"$BACKUP_DIR/${name}"_*.tar.gz`.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Missing required positional arguments trigger parameter-expansion errors and a nonzero exit.
- `set -e` exits on an unhandled command failure; `set -u` treats unset variables as errors; `pipefail` propagates pipeline failures.
- `EXIT` trap removes `tmp` with `rm -f`.
