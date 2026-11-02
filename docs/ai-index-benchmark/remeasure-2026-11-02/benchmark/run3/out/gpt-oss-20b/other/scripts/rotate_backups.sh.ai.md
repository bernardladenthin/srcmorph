### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:06Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Creates a dated, compressed backup of a directory and retains only the most recent N archives.

#### Purpose
- Generates a timestamped `.tar.gz` backup of a specified source directory.
- Keeps only the newest `KEEP` archives, deleting older ones.
- Intended to be run directly from the command line.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- `$1` → `SOURCE_DIR` (required)
- `$2` → `BACKUP_DIR` (required)
- `$3` → `KEEP` (optional, defaults to `7`)
- Validates `KEEP` is a positive integer.
- Checks `SOURCE_DIR` exists; creates `BACKUP_DIR` if missing.

#### Output
- Creates file `"$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`.
- Prints creation message with size.
- Deletes older archives beyond the `KEEP` limit, printing each removal.
- Exit status `0` on success; `2` on invalid arguments or missing directory.

#### Core logic
- Resolve `name` from `SOURCE_DIR`.
- Build `archive` path with UTC timestamp.
- Create temporary file `tmp` and set trap to delete it on exit.
- Run `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"`.
- Move `tmp` to final `archive`.
- List existing archives sorted newest first.
- Loop over entries beyond `KEEP` and remove them.

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
- External commands: `tar`, `date`, `du`, `ls`, `rm`, `mv`, `mkdir`.
- Utilities: `basename`, `dirname`, `cut`.

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` ensures temporary file `tmp` is removed on any exit.
