### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:44:50Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated compressed backups of a directory and retains only the N most recent archives.

#### Purpose
- Generates a timestamped `.tar.gz` archive of a source directory into a backup location.
- Enforces retention policy by deleting older archives beyond the specified count.

#### Type
Bash shell script (executable utility).

#### Input
- `source_dir`: Directory path to back up.
- `backup_dir`: Destination directory for archives.
- `keep` (optional): Integer count of archives to retain (default: 7).

#### Output
- Created archive file: `${source_dir_name}_YYYYMMDDTHHMMSSZ.tar.gz`.
- Console logs: Archive size and list of removed files.
- Side effects: Deletion of old archives in `backup_dir`.

#### Core logic
- Validates `keep` argument is a positive integer and `source_dir` exists.
- Creates `backup_dir` if missing.
- Constructs archive filename using UTC timestamp and source directory basename.
- Uses `tar` to compress source into a temporary `.partial` file.
- Moves temporary file to final archive name on success.
- Lists archives matching source pattern sorted by modification time (newest first).
- Iterates through list starting at index `keep` and removes excess files.
- Ensures cleanup of temporary partial file on exit via trap.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> Exit code (0 success, 2 error)

#### Dependencies
- `bash` (shell environment).
- `tar` (compression utility).
- `date` (timestamp generation).
- `ls` (file listing).
- `mv`, `rm`, `mkdir`, `echo`, `mapfile`, `cut`, `du`.

#### Exceptions / Errors
- Missing arguments: Exits with code 2 and usage message.
- Invalid `keep` value: Exits with code 2 if not a positive integer.
- Missing source directory: Exits with code 2 if path does not exist.
- Script failure: Trap ensures removal of partial archive file on exit.
