### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:09:43Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the newest N archives.

#### Purpose
- Generates a tar.gz backup of a source directory.
- Keeps only the most recent N backup archives.

#### Type
- Bash shell script.

#### Input
- `source_dir`: directory to back up.
- `backup_dir`: destination for archives.
- `keep` (optional): positive integer, defaults to 7.

#### Output
- New archive file in `backup_dir`.
- Deletion of older archives beyond the keep count.
- Console messages for created and removed archives.

#### Core logic
- Parse and validate arguments; exit on errors.
- Ensure `source_dir` exists; create `backup_dir`.
- Build archive name with UTC timestamp.
- Tar the source directory to a temporary file, then rename to final archive.
- List archives sorted newest first; delete those beyond the keep limit.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` → creates backup, removes old archives.

#### Constants and configuration
- `KEEP` default `7`.
- Regex pattern `^[1-9][0-9]*$` for positive integer validation.

#### Dependencies
- `bash`, `tar`, `date`, `du`, `ls`, `mapfile`, `cut`, `rm`, `echo`, `mkdir`, `basename`, `dirname`.

#### Exceptions / Errors
- Exit code 2 if `keep` is not a positive integer.
- Exit code 2 if `source_dir` does not exist.
- `set -euo pipefail` causes script to terminate on any command failure.
