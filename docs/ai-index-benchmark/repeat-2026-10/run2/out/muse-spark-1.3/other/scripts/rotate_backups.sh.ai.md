### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:18:07Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed dated backups of a directory while retaining only newest N archives.

#### Purpose
- Backup rotation for single source directory into timestamped tar.gz archives
- Enforces retention policy keeping newest KEEP archives per source basename

#### Type
- Bash shell script, executable backup utility

#### Input
- SOURCE_DIR positional arg $1, must be existing directory
- BACKUP_DIR positional arg $2, created via mkdir -p if missing
- KEEP positional arg $3 optional, defaults to 7, positive integer regex ^[1-9][0-9]*$

#### Output
- BACKUP_DIR/{basename}_YYYYMMDDTHHMMSSZ.tar.gz compressed archive via tar -czf
- Atomic publish via .partial temp file + mv, cleanup via trap EXIT
- Stdout status lines: created archive with du -h size, removed old archives

#### Core logic
- Validates KEEP integer, validates SOURCE_DIR directory existence
- Derives archive name from basename SOURCE_DIR + date -u +%Y%m%dT%H%M%SZ
- Archives with tar -C dirname SOURCE_DIR to basename-relative tar.gz into tmp partial
- Lists matching BACKUP_DIR/{name}_*.tar.gz newest-first via ls -1t into mapfile archives array
- Deletes slice archives[@]:$KEEP via rm -f to enforce retention

#### Public API
- rotate_backups.sh(source_dir, backup_dir, [keep]) -> exit status, creates timestamped tar.gz

#### Dependencies
- bash with set -euo pipefail, mapfile, trap
- tar, GNU coreutils basename, dirname, date -u, ls -1t, du -h, mkdir -p, mv, rm

#### Exceptions / Errors
- Exit 2 on invalid keep: keep must be a positive integer
- Exit 2 on missing source: no such directory
- set -euo pipefail aborts on tar/mv/ls failures, trap removes .partial file
