### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:28Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated gzip tar backup of a directory and prunes old archives, keeping the newest N.

#### Purpose
- Snapshot one source directory into a timestamped `.tar.gz` in a backup directory.
- Retain only the newest N archives for that source, deleting older ones.

#### Type
Bash script (shell executable, `#!/usr/bin/env bash`), strict mode `set -euo pipefail`.

#### Input
- `$1` source_dir: required, must exist as a directory.
- `$2` backup_dir: required, created with `mkdir -p` if missing.
- `$3` keep: optional, default `7`, must match `^[1-9][0-9]*$`.
- System tools: `tar`, `ls`, `du`, `date -u`, `basename`, `dirname`, `mapfile`, `rm`, `mv`.
- Existing archives in backup_dir matching `<name>_*.tar.gz`.

#### Output
- Creates `<backup_dir>/<name>_<UTC timestamp YYYYMMDDTHHMMSSZ>.tar.gz`.
- Writes stdout: `created <archive> (<size>)`.
- Deletes older archives beyond the newest `keep`; stdout `removed <path>` per deletion.
- Temporary file `<archive>.partial` is removed on EXIT via trap.

#### Core logic
- Validate `keep` as positive integer; exit 2 on failure.
- Validate source is an existing directory; exit 2 otherwise.
- Derive `name` from basename of source; build archive path with UTC timestamp.
- Write tar to `.partial` using `tar -C <parent> -czf` so archive contains only the basename-relative tree.
- Atomically rename `.partial` to final archive via `mv`.
- Print archive path and human-readable size.
- List matching archives newest first with `ls -1t` into array via `mapfile`.
- Slice array from index `keep` onward and `rm -f` each stale archive.
- Retention is per source name; other sources' archives in the same directory are untouched.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exit 0 on success; exit 2 on bad args. Creates backup and prunes.

#### Dependencies
- GNU/BSD `tar` (gzip via `-z`).
- `date`, `du`, `ls`, `basename`, `dirname`, `mapfile` (bash 4+), `rm`, `mv`, `mkdir`.

#### Exceptions / Errors
- Missing `$1` or `$2`: `${var:?}` aborts with usage message.
- Invalid `keep`: stderr message, exit 2.
- Nonexistent source directory: stderr message, exit 2.
- Any failing command under `set -e` aborts script; `.partial` cleaned by EXIT trap.
- `ls` on zero matches fails under `pipefail`/`set -e` in process substitution context; deletion loop not reached in that case.
- Filenames with newlines break `ls -1t` parsing; `rm -f --` guards option injection only.
