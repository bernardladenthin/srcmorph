### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:34Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Rotates backups of a directory by creating a timestamped tar.gz and deleting older archives beyond a configurable count.

#### Purpose
- Creates a compressed, dated backup of a directory.
- Keeps only the newest N archives; older ones are removed.
- Intended to be run directly from the command line.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR` (required, source directory).
- `$2` → `BACKUP_DIR` (required, destination directory).
- `$3` → `KEEP` (optional, defaults to `7`).
- No external options or environment variables.

#### Output
- Writes a new file: `"$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`.
- Deletes older backup files matching `"$BACKUP_DIR/${name}_*.tar.gz"` beyond the `KEEP` count.
- Prints creation and removal messages to stdout.
- Exit status: `0` on success, `2` on argument or validation errors.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary archive `"$tmp"` using `tar -C "$(dirname "$SOURCE_DIR")" -czf`.
- Move temporary file to final archive name.
- List existing archives sorted newest first.
- Remove all but the first `KEEP` archives, echoing each removal.

#### Functions
- None.

#### Constants and configuration
- `SOURCE_DIR`=`"${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR`=`"${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP`=`"${3:-7}"`
- `name`=`"$(basename "$SOURCE_DIR")"`
- `archive`=`"$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp`=`"$archive.partial"`

#### Dependencies
- External commands: `tar`, `date`, `du`, `ls`, `rm`, `mv`.
- Utilities: `basename`, `dirname`, `cut`.

#### Errors and exit codes
- `exit 2` if `KEEP` is not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` removes temporary file on exit.
