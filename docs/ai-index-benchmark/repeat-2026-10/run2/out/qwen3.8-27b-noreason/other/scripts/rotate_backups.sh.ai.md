### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:09:18Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped tar.gz backup of a directory and prunes old archives to retain only the newest N copies.

#### Purpose
- Generates a compressed, dated backup archive of a single source directory.
- Enforces a retention policy by deleting the oldest archives beyond a configurable count.

#### Type
- Bash script (executable shell script)

#### Input
- `SOURCE_DIR` (positional arg 1): path to the directory to back up
- `BACKUP_DIR` (positional arg 2): destination directory for archives
- `KEEP` (positional arg 3, default `7`): maximum number of archives to retain

#### Output
- `archive` file: `<backup_dir>/<source_basename>_<UTC-timestamp>.tar.gz`
- Side effects: removes old `.tar.gz` files exceeding the `KEEP` limit; prints creation and removal messages to stdout

#### Core logic
- Validates `KEEP` is a positive integer and `SOURCE_DIR` exists; exits with code 2 on failure
- Creates `BACKUP_DIR` if absent (`mkdir -p`)
- Builds archive path using `basename` of source and `date -u +%Y%m%dT%H%M%SZ`
- Writes to a `.partial` temp file first; `trap` on EXIT removes it if interrupted
- Runs `tar -C <parent> -czf <tmp> <name>` to create the compressed archive
- Atomically renames `.partial` to final `.tar.gz`
- Lists existing archives for this source sorted newest-first (`ls -1t`)
- Deletes all archives beyond index `KEEP` in the sorted list

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exit 0 on success, exit 2 on validation error

#### Dependencies
- `tar`, `date`, `du`, `basename`, `dirname`, `ls`, `mv`, `rm`, `mkdir` (standard Unix utilities)
- Bash builtins: `mapfile`, `trap`, `set -euo pipefail`

#### Exceptions / Errors
- Exit code 2 with stderr message if `KEEP` is not a positive integer
- Exit code 2 with stderr message if `SOURCE_DIR` does not exist
- `set -euo pipefail` causes immediate exit on any unhandled command failure or unset variable
- `trap 'rm -f "$tmp"' EXIT` cleans up partial archive on any exit path
