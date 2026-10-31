### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:49Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older archives beyond the configured retention count.

#### Purpose
- Back up one source directory as a timestamped `.tar.gz` archive.
- Retain only the newest `KEEP` archives for that source.

#### Type
- Bash command-line script.

#### Input
- Positional argument `$1` (`SOURCE_DIR`): directory to back up.
- Positional argument `$2` (`BACKUP_DIR`): directory for archives.
- Optional positional argument `$3` (`KEEP`): number of newest archives to retain.

#### Output
- Writes a compressed archive to `BACKUP_DIR`.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Deletes older matching archives after creating the new archive.

#### Core logic
- Requires source and backup directory arguments; defaults retention to seven archives.
- Validates `KEEP` as a positive integer and verifies `SOURCE_DIR` exists.
- Creates `BACKUP_DIR` if needed; names the archive from the source directory basename and a UTC timestamp.
- Creates a temporary `.partial` archive with `tar`, then renames it to the final archive; an exit trap removes the temporary file.
- Lists matching archives newest-first and removes all entries after the first `KEEP`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` — create and rotate directory backups.
- Internal functions: none.

#### Constants and configuration
- `SOURCE_DIR`: required value from `$1`.
- `BACKUP_DIR`: required value from `$2`.
- `KEEP`: `$3`, default `7`; must match a positive-integer pattern.
- Archive timestamp format: UTC `%Y%m%dT%H%M%SZ`.
- `name`: basename of `SOURCE_DIR`.
- `archive`: `BACKUP_DIR/${name}_<timestamp>.tar.gz`.
- `tmp`: final archive path plus `.partial`.

#### Dependencies
- Bash features: `set -euo pipefail`, `[[ =~ ]]`, `mapfile`, process substitution, parameter expansion, `trap`.
- Commands: `basename`, `dirname`, `date`, `tar`, `mkdir`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Missing required arguments: parameter expansion emits usage text and exits.
- Invalid `KEEP`: prints an error to stderr and exits with status 2.
- Missing source directory: prints an error to stderr and exits with status 2.
- `set -euo pipefail` makes unset-variable access and failing commands fatal; the exit trap removes the temporary archive.
