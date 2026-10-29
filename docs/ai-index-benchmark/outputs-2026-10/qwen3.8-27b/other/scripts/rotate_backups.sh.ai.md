### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:44:43Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a timestamped tar.gz backup of a directory and prunes old archives beyond a configurable retention count.

#### Purpose
- One-shot backup rotation: compress a source directory into a dated archive and enforce a "keep newest N" policy per source name.

#### Type
- Bash script (executable, `set -euo pipefail`); no functions, linear top-level flow.

#### Input
- `$1` — `SOURCE_DIR`: path to the directory to back up (must exist).
- `$2` — `BACKUP_DIR`: destination directory for archives (created if absent).
- `$3` — `KEEP` (optional, default `7`): positive integer retention count.

#### Output
- A new file `$BACKUP_DIR/<basename>_<UTC-timestamp>.tar.gz` containing the source directory.
- Deletion of archives older than the newest `KEEP` for that source name.
- stdout: confirmation lines with archive path, size, and removed-file names.

#### Core logic
- Validate `KEEP` is a positive integer via regex `^[1-9][0-9]*$`; exit 2 on failure.
- Verify `SOURCE_DIR` is a directory; exit 2 if not.
- `mkdir -p BACKUP_DIR`.
- Build archive name: `<basename>_<YYYYMMDDTHHMMSSZ>.tar.gz` (UTC).
- Write to a `.partial` temp file; `trap` removes it on any exit (atomic-rename safety).
- `tar -C $(dirname SOURCE_DIR) -czf tmp <basename>` — compresses the directory.
- `mv tmp archive` — atomic rename into final name.
- `ls -1t` lists matching archives newest-first; `mapfile` captures them.
- Loop over indices `KEEP..end` and `rm -f` each old archive.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — no return value; exit 0 on success, exit 2 on validation failure.

#### Dependencies
- `tar`, `gzip` (via `-z`), `date`, `du`, `basename`, `dirname`, `ls`, `mv`, `rm`, `mkdir`, `mapfile` (bash builtin).

#### Exceptions / Errors
- Exit 2 with stderr message if `KEEP` is not a positive integer.
- Exit 2 with stderr message if `SOURCE_DIR` does not exist or is not a directory.
- `set -euo pipefail` causes immediate abort on any unhandled command failure.
- `trap ... EXIT` guarantees `.partial` temp file cleanup on premature exit.
