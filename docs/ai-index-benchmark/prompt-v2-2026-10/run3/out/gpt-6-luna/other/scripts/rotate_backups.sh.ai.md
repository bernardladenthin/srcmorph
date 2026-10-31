### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:00:04Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped compressed archive of a source directory and prunes older archives to retain the configured number.

#### Purpose
- Back up one directory as a `.tar.gz` archive.
- Keep only the newest `KEEP` archives for that source directory.

#### Type
- Bash shell script (`rotate_backups.sh`).

#### Input
- Positional argument 1: `SOURCE_DIR`, required existing source directory.
- Positional argument 2: `BACKUP_DIR`, required destination directory.
- Positional argument 3: `KEEP`, optional positive integer; defaults to `7`.

#### Output
- Writes a UTC-timestamped archive at `BACKUP_DIR/<source-name>_<timestamp>.tar.gz`.
- Removes older matching archives beyond the retention limit.
- Prints the created archive path and size, and each removed archive path.

#### Core logic
- Enables `set -euo pipefail` for strict shell error handling.
- Validates `KEEP` against a positive-integer pattern and verifies `SOURCE_DIR` exists; creates `BACKUP_DIR` if needed.
- Derives the archive prefix from `basename "$SOURCE_DIR"` and timestamp from `date -u +%Y%m%dT%H%M%SZ`.
- Creates a compressed tar archive of the source directory’s basename from its parent directory, first writing to a `.partial` path.
- Registers an exit trap to remove the partial archive; renames it to the final archive after successful `tar`.
- Lists matching archives newest first and deletes entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` — create and rotate directory archives.
- Internal operations: `basename`, `dirname`, `date`, `tar`, `mv`, `du`, `cut`, `mapfile`, `ls`, `rm`.

#### Constants and configuration
- `KEEP`: retention count from argument 3; default `7`.
- Archive timestamp format: `%Y%m%dT%H%M%SZ` in UTC.
- Partial archive suffix: `.partial`.
- Archive extension: `.tar.gz`.
- Exit status `2`: invalid `KEEP`, missing required argument, or nonexistent source directory.

#### Dependencies
- Bash features: `[[ ... =~ ... ]]`, process substitution, arrays, `mapfile`, `trap`.
- External commands: `basename`, `dirname`, `date`, `mkdir`, `tar`, `mv`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Exits on unset or missing required arguments under strict mode.
- Prints an error to stderr and exits `2` for invalid `KEEP` or missing source directory.
- On exit, removes the `.partial` archive if present.
