### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:17:25Z
- G: 
- A: 0.0.0
- X: file
---
> Creates timestamped compressed backups of a directory and retains only newest N archives.
#### Purpose
- Dated tar.gz backup rotation for single source_dir into backup_dir
- Enforces retention policy via KEEP count, default 7
#### Type
- Bash shell script, executable CLI utility, set -euo pipefail
#### Input
- SOURCE_DIR arg $1: existing directory to archive
- BACKUP_DIR arg $2: destination directory, auto-created via mkdir -p
- KEEP arg $3 optional: positive integer retention count
#### Output
- BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz compressed archive
- Console stdout: created archive path plus du -h size
- Side effects: deletes excess old archives, removes .partial temp on EXIT trap
#### Core logic
- Validates KEEP regex ^[1-9][0-9]*$, validates SOURCE_DIR is directory
- Derives archive name from basename SOURCE_DIR plus date -u timestamp
- Builds archive via tar -C dirname SOURCE_DIR -czf tmp partial file, then mv atomic publish
- Lists matching BACKUP_DIR/<name>_*.tar.gz newest-first via ls -1t, mapfile archives array
- Slices archives[@]:$KEEP and rm -f old files with removed log
#### Dependencies
- bash, tar, date, mkdir, mv, ls, rm, du, cut, basename, dirname, trap
#### Exceptions / Errors
- Exits 2 on invalid KEEP, missing SOURCE_DIR directory
- Exits on usage error when $1/$2 missing via :? expansion
