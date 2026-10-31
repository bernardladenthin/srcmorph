### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:07Z
- G: 
- A: 0.0.0
- X: file
---
> Rotates compressed backups of a directory by creating a timestamped tar.gz archive and deleting all but the newest N archives.

#### Purpose
- Standalone Bash utility for scheduled/manual directory backup retention.
- Produces a dated, immutable archive and self-prunes to a configured keep count.

#### Type
- Shell script (Bash) — uses bashisms: mapfile, arrays, [[ ]], set -euo pipefail.

#### Input
- $1 SOURCE_DIR — directory to archive; must exist.
- $2 BACKUP_DIR — destination directory; created if missing.
- $3 KEEP (default 7) — positive integer retention count.
- Existing backups addressed via glob: BACKUP_DIR/${basename}_*.tar.gz.

#### Output
- Archive file `BACKUP_DIR/${basename}_<UTC %Y%m%dT%H%M%SZ>.tar.gz`.
- Stale archives beyond KEEP are removed.
- Progress lines to stdout; diagnostics to stderr.

#### Core logic
- Validate KEEP matches `^[1-9][0-9]*$`, else echo error and exit 2.
- Validate SOURCE_DIR is a directory, else echo error and exit 2.
- `mkdir -p "$BACKUP_DIR"`.
- Compose archive name from basename of SOURCE_DIR plus UTC timestamp.
- Write to staging file `archive.partial`; `trap 'rm -f "$tmp"' EXIT`.
- `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"` — compresses the top-level entry only.
- `mv "$tmp" "$archive"` for atomic finalization; print created line with `du -h` size.
- `mapfile` from `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz` (newest first).
- Slice `"${archives[@]:$KEEP}"` and `rm -f --` each stale archive; echo removed.

#### Public API
- CLI: `rotate_backups.sh <source_dir> <backup_dir> [keep]` → exit 0 on success; no library functions exported.

#### Dependencies
- External binaries: tar, date, ls, du, cut, mv, rm, mkdir, basename, dirname.
- Bash builtins: mapfile, trap, [[ ]].

#### Exceptions / Errors
- Missing SOURCE_DIR or non-numeric KEEP → exit 2 with message to stderr.
- trap EXIT removes the .partialial staging file if the script aborts mid-tar.
