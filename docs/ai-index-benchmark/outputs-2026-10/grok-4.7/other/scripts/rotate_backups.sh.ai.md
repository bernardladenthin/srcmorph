### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:45:11Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense structured markdown summary of the rotate_backups.sh source file.> Creates a dated compressed archive of one directory and deletes older archives beyond a retention count.

#### Purpose
- Rotates directory backups: one new `.tar.gz` per run, then prunes all but the newest `KEEP` archives for that source.
- Intended CLI: `rotate_backups.sh <source_dir> <backup_dir> [keep=7]`.

#### Type
- Bash script (executable shell program); not a class. Strict mode `set -euo pipefail`.

#### Input
- `$1` `SOURCE_DIR`: directory to archive (required).
- `$2` `BACKUP_DIR`: destination for archives (required; created if missing).
- `$3` `KEEP`: retention count, default `7`; must match `^[1-9][0-9]*$`.
- Reads existing `$BACKUP_DIR/${name}_*.tar.gz` via `ls -1t` for rotation.
- Uses `date -u` for the archive timestamp and `du -h` for size reporting.

#### Output
- Writes `$BACKUP_DIR/${basename}_YYYYMMDDTHHMMSSZ.tar.gz` via a `.partial` temp file then `mv`.
- Deletes archives of the same source beyond the first `KEEP` newest files.
- Stdout: `created …` (path and human size) and `removed …` per deleted archive.
- Stderr and exit `2` on bad `KEEP` or missing source directory.
- `trap` removes the partial archive on `EXIT`.

#### Core logic
- Require `SOURCE_DIR` and `BACKUP_DIR`; default `KEEP` to `7`.
- Reject non-positive-integer `KEEP`; require `SOURCE_DIR` to be a directory; `mkdir -p` `BACKUP_DIR`.
- Name the archive from `basename` of the source plus UTC `date` (`%Y%m%dT%H%M%SZ`) and `.tar.gz`.
- `tar -C` parent of the source, compress only the basename into `$archive.partial`, then `mv` to the final path so a failed tar does not leave a finished archive.
- List matching archives newest-first (`ls -1t`); `rm -f` every entry after index `KEEP` (`${archives[@]:$KEEP}`).

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep=7) -> exit status` — dated archive plus retention prune.

#### Dependencies
- `bash`, `tar`, `date`, `du`, `cut`, `ls`, `mkdir`, `mv`, `rm`, `basename`, `dirname`, `mapfile`.

#### Exceptions / Errors
- Missing args: parameter expansion `${1:?}` / `${2:?}` prints usage and exits non-zero.
- Invalid `KEEP` or missing `SOURCE_DIR`: message on stderr, `exit 2`.
- `set -euo pipefail`: command failures (tar, mv, mkdir) abort the script.
- Partial file cleaned by `trap 'rm -f "$tmp"' EXIT`.
