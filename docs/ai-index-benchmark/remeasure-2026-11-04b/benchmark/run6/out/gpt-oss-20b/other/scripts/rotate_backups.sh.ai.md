### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:51Z
- G: 
- A: 0.0.0
- X: file
- P: A1309C5E
---
> Creates a dated, compressed backup of a directory and prunes older archives, keeping only the newest N.

#### Purpose
- Back up a specified directory to a target location.
- Retain only the most recent `KEEP` archives; older ones are deleted.
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
- Creates a file `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"`.
- Prints creation and removal messages to stdout.
- Exit status `0` on success; `2` on invalid arguments or missing source directory.

#### Core logic
- Validate `KEEP` is a positive integer.
- Ensure `SOURCE_DIR` exists; create `BACKUP_DIR` if missing.
- Build archive name with UTC timestamp.
- Create temporary archive `"$tmp"`, then rename to final name.
- List existing archives sorted newest first; delete those beyond the `KEEP` count.
- Report each removal.

#### Functions
- None.

#### Constants and configuration
- `SOURCE_DIR=${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}`
- `BACKUP_DIR=${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}`
- `KEEP=${3:-7}`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`

#### Dependencies
- External commands: `tar`, `date`, `du`, `ls`, `rm`, `mkdir`.
- No sourced files.

#### Errors and exit codes
- `exit 2` if `KEEP` is not a positive integer.
- `exit 2` if `SOURCE_DIR` does not exist.
- `trap` ensures temporary file `$tmp` is removed on exit.
