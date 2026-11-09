### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:02Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates a dated, compressed backup of a directory and prunes older archives, keeping only the newest N.

#### Purpose
- Backup a specified directory.
- Retain only the most recent N archives.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`).
- Strict mode: `set -euo pipefail`.

#### Input
- `$1` → `SOURCE_DIR` (required).
- `$2` → `BACKUP_DIR` (required).
- `$3` → `KEEP` (optional, defaults to `7`).
- No external options or environment variables.

#### Output
- Creates a file `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"`.
- Deletes older archives beyond the `KEEP` count.
- Prints creation and removal messages.
- Exits with status `0` on success, `2` on argument or validation errors.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR`.
- Build archive name with UTC timestamp.
- Create temporary archive (`.partial`), then rename to final.
- List existing archives sorted newest first.
- Remove archives beyond the `KEEP` limit.

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
- `tar` (creates compressed archive).
- `date` (generates timestamp).
- `du` (reports archive size).
- `ls` (lists existing archives).
- `mkdir`, `rm`, `mv`, `echo` (standard utilities).

#### Errors and exit codes
- `exit 2` if `KEEP` is not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` ensures temporary file is removed on exit.
