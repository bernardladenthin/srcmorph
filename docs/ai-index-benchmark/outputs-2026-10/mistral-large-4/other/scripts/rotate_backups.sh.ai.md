### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:46:09Z
- G: 
- A: 0.0.0
- X: file
---
> Creates timestamped gzip-compressed backups of a directory and prunes older archives to retain only the newest N.

#### Purpose
- Backs up one source directory into dated `.tar.gz` archives inside a backup directory
- Enforces retention by deleting all but the newest `KEEP` archives for that source

#### Type
- Bash script (executable, `#!/usr/bin/env bash`)

#### Input
- Arg 1 `SOURCE_DIR`: existing directory to archive (validated with `[ -d ]`)
- Arg 2 `BACKUP_DIR`: destination directory (created via `mkdir -p` if absent)
- Arg 3 `KEEP`: optional positive integer retention count (default `7`, regex-validated)
- Consumes: file tree under `SOURCE_DIR` via `tar -C "$(dirname "$SOURCE_DIR")"`

#### Output
- Writes `$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz` where `name=basename "$SOURCE_DIR"`
- Prints `created <path> (<human-size>)` and `removed <path>` lines to stdout
- Side effect: deletes older `${name}_*.tar.gz` archives beyond the first `KEEP` in newest-first order
- Temporary `.partial` file removed on `EXIT` via `trap`

#### Core logic
- `set -euo pipefail` for strict mode
- Validate `KEEP` matches `^[1-9][0-9]*$` else exit 2 with stderr message
- Validate `SOURCE_DIR` is a directory else exit 2
- `mkdir -p "$BACKUP_DIR"`
- Build archive path with UTC timestamp; `tmp="$archive.partial"`
- `trap 'rm -f "$tmp"' EXIT` to clean partial on any exit
- `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"` to compress source
- `mv "$tmp" "$archive"` for atomic rename
- `mapfile -t archives < <(ls -1t "$BACKUP_DIR/${name}"_*.tar.gz)` to list newest first
- `for old in "${archives[@]:$KEEP}"; do rm -f -- "$old"; done` to prune excess

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` -> exit 0; creates one archive and removes older ones beyond retention; exit 2 on invalid arguments

#### Dependencies
- Coreutils: `date`, `du`, `ls`, `basename`, `dirname`, `mkdir`, `rm`, `mv`
- `tar` with gzip (`-z`)
- Bash builtins: `set`, `trap`, `mapfile`, `[[ ]]`, arrays

#### Exceptions / Errors
- Exit code 2: missing `SOURCE_DIR`/`BACKUP_DIR` (via `:?`), `KEEP` not a positive integer, or `SOURCE_DIR` not a directory
- Validation errors printed to stderr
- `trap` on `EXIT` guarantees `.partial` temp file cleanup on success or failure
