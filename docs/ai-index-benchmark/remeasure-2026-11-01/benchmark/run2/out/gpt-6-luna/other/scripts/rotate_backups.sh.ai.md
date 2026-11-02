### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:44:21Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory and retain only the newest `KEEP` archives for it.
- Run directly as `rotate_backups.sh <source_dir> <backup_dir> [keep]`.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR`, required source directory.
- `$2` → `BACKUP_DIR`, required destination directory.
- `$3` → `KEEP`, optional positive integer; defaults to `7`.
- Reads `SOURCE_DIR` contents and lists matching archives in `BACKUP_DIR`.
- No environment variables read.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a `.tar.gz` archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Deletes older matching archives beyond the first `KEEP`.
- Exits `2` for invalid `KEEP` or a missing source directory.

#### Core logic
- Validate `KEEP` as a positive integer and require `SOURCE_DIR` to be a directory.
- Create `BACKUP_DIR`; derive the source basename and a UTC timestamped archive path.
- Set an `EXIT` trap to remove the temporary `.partial` archive.
- Create a gzip-compressed tar archive from the source directory’s parent, then rename the temporary archive to its final path.
- Print the archive path and size.
- List matching archives newest first and delete entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` = output of `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`, loaded by `mapfile`.
- `old` = each archive after index `KEEP` in `archives`.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Uses Bash builtins: `mapfile`, `echo`, `set`, `trap`.
- `tar` archives the source directory; archive discovery matches `<source-basename>_*.tar.gz` in `BACKUP_DIR.

#### Errors and exit codes
- Exit `2` if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Required-argument expansion fails if `$1` or `$2` is unset or empty; `set -u` also treats unset variables as errors.
- `set -e` exits on an unhandled failing command; `set -o pipefail` propagates pipeline failure.
- `EXIT` trap removes `tmp` with `rm -f`.
