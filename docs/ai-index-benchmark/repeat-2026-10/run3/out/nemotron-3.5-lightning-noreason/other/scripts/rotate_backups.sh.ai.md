### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:18Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated, compressed backup of a directory and prunes old archives, keeping only the newest N.

#### Purpose
- Creates a compressed, dated archive of a source directory
- Retains only the newest N archives in the backup directory

#### Type
Shell script (bash); executable script with CLI interface

#### Input
- `source_dir` (required): directory to archive
- `backup_dir` (required): destination directory for archives
- `keep` (optional, default 7): positive integer specifying number of archives to retain

#### Output
- Creates a new `.tar.gz` archive in `backup_dir` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`
- Outputs creation confirmation with human-readable size
- Removes oldest archives exceeding the `keep` count
- Prints removal messages for each deleted archive

#### Core logic
- Validates `keep` is a positive integer; exits on invalid input
- Verifies `source_dir` exists; exits if missing
- Ensures `backup_dir` exists via `mkdir -p`
- Generates timestamped archive filename using current UTC time
- Creates partial archive in temp file, then atomically moves to final location
- Lists archives sorted by modification time (newest first)
- Deletes archives beyond the `keep` threshold using `ls -1t` ordering

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` - main entry point
- No other exported functions or interfaces

#### Dependencies
- bash shell with `set -euo pipefail`
- `tar` for archive creation
- `mv` for atomic file move
- `ls -1t` for time-sorted listing
- `rm` for file deletion
- `du -h` for human-readable size display
- `date -u` for UTC timestamp generation

#### Exceptions / Errors
- Exits with code 2 and error message if `keep` is not a positive integer
- Exits with code 2 and error message if `source_dir` does not exist
- Trap on EXIT ensures partial archive temp file is removed on interruption
