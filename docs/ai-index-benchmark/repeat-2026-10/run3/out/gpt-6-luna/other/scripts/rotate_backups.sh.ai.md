### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:10Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older backups beyond the retention limit.

#### Purpose
- Back up one source directory as a `.tar.gz` archive.
- Retain only the newest configured number of archives for that directory.

#### Type
- Bash shell script; executable backup and retention utility.

#### Input
- Positional arguments: `source_dir`, `backup_dir`, optional `keep` (default `7`).
- Reads the source directory contents and current UTC time.

#### Output
- Writes a timestamped archive to `backup_dir/<source-name>_<UTC timestamp>.tar.gz`.
- Prints the created archive path and size; prints each removed archive path.

#### Core logic
- Validates that `keep` is a positive integer and `source_dir` exists; creates `backup_dir` if needed.
- Uses the source directory basename to name the archive and runs `tar` from its parent directory, preserving the source directory as the archive root.
- Creates the archive as `.partial`, then renames it to the final `.tar.gz` path; an exit trap removes any remaining partial file.
- Lists matching archives newest-first by modification time and deletes entries after the first `keep`.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` — create and rotate backups.

#### Dependencies
- Bash built-ins: `basename`, `dirname`, `date`, `mapfile`.
- External commands: `tar`, `mkdir`, `mv`, `du`, `cut`, `ls`, `rm`.

#### Exceptions / Errors
- Exits with status `2` if required arguments are missing, `keep` is not a positive integer, or the source directory does not exist.
- `set -euo pipefail` makes unhandled command failures, unset-variable access, and pipeline failures terminate the script.
