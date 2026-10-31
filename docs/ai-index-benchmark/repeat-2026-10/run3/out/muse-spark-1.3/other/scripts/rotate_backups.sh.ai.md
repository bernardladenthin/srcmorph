### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:24:25Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed dated backups of a directory while retaining only newest archives.

#### Purpose
- Backup rotation for source directories into timestamped tar.gz archives.
- Enforces retention policy keeping newest N backups per source.

#### Type
- Shell script / Bash executable, standalone CLI utility.

#### Input
- SOURCE_DIR positional arg 1: directory to archive, basename defines archive prefix.
- BACKUP_DIR positional arg 2: destination directory, auto-created via mkdir -p.
- KEEP positional arg 3 optional, default 7: positive integer retention count.

#### Output
- Archive file `$BACKUP_DIR/<name>_YYYYMMDDTHHMMSSZ.tar.gz` via tar -czf.
- Console status: created archive with size, removed old archives.
- Side effects: deletes excess archives beyond KEEP, cleans partial file on exit.

#### Core logic
- Validates KEEP regex `^[1-9][0-9]*$`, validates SOURCE_DIR exists.
- Builds UTC timestamped archive name from basename SOURCE_DIR + date -u.
- Stages compression to `$archive.partial` with trap cleanup, tar -C parent directory.
- Atomically promotes partial via mv to final archive.
- Lists matching `$BACKUP_DIR/<name>_*.tar.gz` newest-first via ls -1t, slices array beyond KEEP and rm old.

#### Public API
- rotate_backups.sh(source_dir, backup_dir, [keep]) -> tar.gz archive, prunes old backups.

#### Dependencies
- bash with set -euo pipefail, mapfile, trap.
- External tools: tar, date -u, ls -1t, du -h, mkdir, mv, rm, basename, dirname.

#### Exceptions / Errors
- Exit 2 on invalid KEEP non-positive-integer with stderr message.
- Exit 2 on missing SOURCE_DIR directory with stderr message.
