### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:26:50Z
- G: 
- A: 0.0.0
- X: file
- P: 96B53C21
---
> Creates a dated, compressed backup of a directory and prunes older archives, keeping only the newest N.

#### Purpose
- Generate a timestamped `.tar.gz` backup of a specified source directory.
- Retain only the most recent `KEEP` backups; delete older ones.
- Intended to be run directly from the command line.

#### Type
- `bash` (shebang `#!/usr/bin/env bash`)
- Strict mode: `set -euo pipefail`

#### Input
- `$1` → `SOURCE_DIR` (required)
- `$2` → `BACKUP_DIR` (required)
- `$3` → `KEEP` (optional, defaults to `7`)
- No external options or environment variables.

#### Output
- Creates file `"$BACKUP_DIR/${name}_YYYYMMDDTHHMMSSZ.tar.gz"`.
- Prints creation and removal messages to stdout.
- Exit status `0` on success, `2` on invalid arguments or missing directory.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary archive `"$tmp"`, then rename to final name.
- List existing archives sorted newest first.
- Delete all but the first `KEEP` entries, echoing each removal.

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
- External commands: `tar`, `date`, `du`, `ls`, `rm`, `mv`
- Utilities: `basename`, `dirname`, `cut`

#### Errors and exit codes
- `exit 2` if `KEEP` not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` removes temporary file on exit.
