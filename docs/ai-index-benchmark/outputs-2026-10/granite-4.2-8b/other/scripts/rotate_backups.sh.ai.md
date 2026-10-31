### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:59:21Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed dated backup of a source directory and retains only the newest N archives.

#### Purpose
- Compresses the contents of a source directory into a timestamped tar.gz file.
- Places the archive in a backup directory named after the source directory.
- Deletes older backup archives so that only the most recent N archives remain.

#### Type
Script (shell / bash)

#### Input
- `<source_dir>`: directory to back up (first positional argument, required).
- `<backup_dir>`: directory where backups are stored (second positional argument, required).
- `[keep=7]`: number of archives to keep (third positional argument, optional, defaults to 7); must be a positive integer.

#### Output
- Creates one compressed archive `$BACKUP_DIR/${name}_YYYYmmddTHHMMSS.tar.gz`.
- Writes output messages to stdout (e.g., "created archive size", "removed archive").
- Side effect: removes old `.tar.gz` files from `$BACKUP_DIR/${name}_*.tar.gz` so only the newest `KEEP` archives remain.

#### Core logic
- Validates `KEEP` is a positive integer (`^[1-9][0-9]*$`).
- Ensures `SOURCE_DIR` exists; creates `BACKUP_DIR` if needed.
- Computes archive name using `basename` of `SOURCE_DIR` and current UTC timestamp (`%Y%m%dT%H%M%SZ`).
- Creates a temporary partial archive `$tmp` via `tar -czf`.
- Moves temporary file to final archive name `$archive`.
- Lists all matching backup archives (`${name}_*.tar.gz`) sorted newest first.
- Deletes all archives after the first `KEEP` entries using `rm -f`.

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep=7) -> void`: creates a dated compressed backup of `source_dir` in `backup_dir` and keeps only the newest `keep` archives.

#### Dependencies
- Standard Unix utilities: `date`, `mkdir`, `tar`, `ls`, `mv`, `rm`, `echo`, `printf` (via `printf`-like output).
- Bash built-ins: `set`, `trap`, parameter expansion, array handling (`mapfile`).

#### Exceptions / Errors
- Exits with status 2 and prints error if `KEEP` is not a positive integer.
- Exits with status 2 and prints error if `SOURCE_DIR` does not exist.
- Uses `set -e` to exit on any command failure; `set -u` to treat unset variables as errors; `pipefail` to propagate pipeline failures.
- `trap` ensures temporary file `$tmp` is removed on script exit.
