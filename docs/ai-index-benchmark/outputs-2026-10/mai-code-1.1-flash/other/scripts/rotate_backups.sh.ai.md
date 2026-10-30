### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:47:38Z
- G: 
- A: 0.0.0
- X: file
---
> Creates timestamped compressed backups of a directory and prunes older archives to retain only the newest N.

#### Purpose
- Creates a dated tar.gz backup of a source directory under a backup directory.
- Deletes older backup archives beyond the configured retention count.

#### Type
- Bash script / shell script.
- Entry point: `rotate_backups.sh <source_dir> <backup_dir> [keep=7]`.

#### Input
- `SOURCE_DIR` from positional argument 1; directory to archive.
- `BACKUP_DIR` from positional argument 2; destination directory for archives.
- `KEEP` from positional argument 3, default `7`; must be a positive integer.
- Consumes filesystem state: source directory existence, backup directory creation, archive file names.

#### Output
- Produces a new archive file named `${name}_YYYYMMDDTHHMMSSZ.tar.gz` in `BACKUP_DIR`.
- Creates `"$archive.partial"` during generation, then renames to final archive path.
- Removes older archive files beyond the first `KEEP` newest archives.
- Emits console messages: `created ...`, `removed ...`.

#### Core logic
- Enforces strict shell behavior with `set -euo pipefail`.
- Validates `KEEP` matches `^[1-9][0-9]*$`; otherwise prints error and exits with status `2`.
- Validates `SOURCE_DIR` exists; otherwise prints `no such directory: ...` and exits with status `2`.
- Creates `BACKUP_DIR` with `mkdir -p`.
- Derives `name` from `basename "$SOURCE_DIR"`.
- Builds `archive` and `tmp` paths using UTC timestamp from `date -u +%Y%m%dT%H%M%SZ`.
- Uses `trap 'rm -f "$tmp"' EXIT` to clean the temporary partial archive on exit.
- Archives the source directory with `tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"`.
- Moves the completed archive into place with `mv "$tmp" "$archive"`.
- Lists newest archives in `BACKUP_DIR` using `ls -1t "$BACKUP_DIR/${name}"_*.tar.gz`.
- Deletes archives beyond the first `KEEP` entries by slicing `archives[@]:$KEEP` and removing each matched path with `rm -f -- "$old"`.

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep=7) -> exit code` - creates and rotates directory backups.
- `rotate_backups.sh` no exported functions; script runs as a CLI.

#### Dependencies
- Bash builtins: `set`, `trap`, `mapfile`, `[[ ]]`, `mkdir`, `basename`, `dirname`, `date`, `mv`, `rm`, `echo`, `exit`
- External commands: `tar`, `ls`, `du`, `cut`
- Filesystem resources: `SOURCE_DIR`, `BACKUP_DIR`, archive paths matching `${name}_*.tar.gz`

#### Exceptions / Errors
- Invalid `KEEP` value: prints `keep must be a positive integer, got: $KEEP` to stderr and exits `2`.
- Missing or invalid `SOURCE_DIR`: prints `no such directory: $SOURCE_DIR` to stderr and exits `2`.
- Missing positional arguments: prints `usage: rotate_backups.sh <source_dir> <backup_dir> [keep]` and exits due to `${1:?...}` / `${2:?...}`.
- `set -euo pipefail` aborts script on command failures such as `tar`, `mv`, or `rm` errors.
