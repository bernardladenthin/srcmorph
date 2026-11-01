### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:20:50Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a directory and retains only the newest N archives.

#### Purpose
- Generates a `.tar.gz` backup of a specified source directory.
- Keeps only the most recent `KEEP` backups, deleting older ones.
- Intended to be run directly from the command line.

#### Type
- `bash`
- `set -euo pipefail`

#### Input
- `$1` → `SOURCE_DIR` (required)
- `$2` → `BACKUP_DIR` (required)
- `$3` → `KEEP` (optional, defaults to `7`)
- No external options or environment variables.

#### Output
- Creates a file `"$BACKUP_DIR/${name}_<timestamp>.tar.gz"`.
- Prints creation and removal messages to stdout.
- Exit status `0` on success; `2` on invalid arguments or missing directory.

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
- `tar` (creates compressed archive)
- `du` (reports size)
- `ls` (lists archives)
- `date` (generates timestamp)

#### Errors and exit codes
- `2` – invalid `KEEP` value or missing `SOURCE_DIR`.
- `EXIT` trap removes temporary file `"$tmp"`.
- No explicit retries or timeouts.
