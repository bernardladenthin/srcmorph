### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:57Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that directory.

#### Type
- Bash shell script; executable script with strict error handling (`set -euo pipefail`).

#### Input
- `SOURCE_DIR`: required existing directory to back up.
- `BACKUP_DIR`: required destination directory, created if needed.
- `KEEP`: optional positive integer retention count; defaults to `7`.

#### Output
- Creates a UTC-timestamped `${name}_YYYYMMDDTHHMMSSZ.tar.gz` archive in `BACKUP_DIR`.
- Deletes older matching archives beyond `KEEP`; prints created and removed archive paths.

#### Core logic
- Validates `KEEP` and verifies `SOURCE_DIR` exists; exits with status `2` on validation errors.
- Names the archive using the source directory’s basename and current UTC timestamp.
- Creates a compressed tar archive at a `.partial` path, then renames it to the final archive path.
- Registers cleanup of the partial archive on exit.
- Lists matching archives newest first and removes all after the first `KEEP`.

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep=7) -> exit status` — create backup and prune old archives.

#### Dependencies
- Bash; `tar`, `date`, `basename`, `dirname`, `mkdir`, `mv`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Missing required arguments trigger usage errors.
- Invalid `KEEP` or missing source directory prints an error and exits `2`.
- `set -euo pipefail` makes command failures, unset variables, and pipeline failures terminate the script.
