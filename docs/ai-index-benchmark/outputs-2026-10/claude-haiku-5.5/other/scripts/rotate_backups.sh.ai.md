### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:30Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated, gzipped tar backup of a directory and prunes old archives to keep the newest N.

#### Purpose
- Back up one source directory into a timestamped `.tar.gz` archive.
- Retain only the newest `KEEP` archives (default 7) for that source.

#### Type
- Bash script (shell executable, `#!/usr/bin/env bash`), strict mode `set -euo pipefail`.
- Not a library; top-level procedural script with a `trap` cleanup handler.

#### Input
- `$1` `SOURCE_DIR`: required, must be an existing directory.
- `$2` `BACKUP_DIR`: required, created via `mkdir -p` if missing.
- `$3` `KEEP`: optional, default `7`, must match `^[1-9][0-9]*$`.
- Clock: `date -u` for UTC timestamp `YYYYMMDDTHHMMSSZ`.
- Filesystem: contents of `SOURCE_DIR`; existing `${name}_*.tar.gz` files in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR/<basename>_<UTC timestamp>.tar.gz`.
- Writes temp file `<archive>.partial`, then renames to final archive via `mv`.
- Stdout: `created <archive> (<size>)`; `removed <old>` per pruned archive.
- Deletes archives beyond the first `KEEP` (sorted newest first by `ls -1t`).
- Trap on EXIT removes the `.partial` temp file.

#### Core logic
- Validate `KEEP` as positive integer; exit 2 on failure.
- Validate `SOURCE_DIR` exists; exit 2 otherwise.
- Ensure `BACKUP_DIR` exists.
- Derive `name` from basename of `SOURCE_DIR`.
- Build archive path with UTC timestamp.
- Register `trap 'rm -f "$tmp"' EXIT` for partial-file cleanup.
- Run `tar -C <parent> -czf <tmp> <name>` to archive relative to parent dir.
- Atomically rename `tmp` to `archive` with `mv`.
- Collect matching archives newest first into array `archives` via `mapfile` and `ls -1t`.
- Iterate `archives[@]:$KEEP` and `rm -f --` each older archive, logging removal.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` -> exit status (0 success, 2 usage/validation error): creates archive and prunes old ones.

#### Dependencies
- Bash builtins: `mapfile`, `trap`, `echo`, `mkdir`, `basename`, `dirname`.
- External commands: `tar`, `date`, `ls`, `du`, `cut`, `rm`, `mv`.

#### Exceptions / Errors
- Invalid `KEEP` value: message to stderr, exit 2.
- Missing `SOURCE_DIR`: `usage` message via `${1:?...}` expansion, exits non-zero.
- Missing `BACKUP_DIR`: `usage` message via `${2:?...}`, exits non-zero.
- `SOURCE_DIR` not a directory: stderr message, exit 2.
- Any failing command (`tar`, `mv`, `ls`) aborts script under `set -e`; EXIT trap removes partial archive.
- `ls` on zero matches under `pipefail` may abort the `mapfile` substitution.
