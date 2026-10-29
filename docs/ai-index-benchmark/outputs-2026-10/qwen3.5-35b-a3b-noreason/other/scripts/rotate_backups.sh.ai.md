### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:57:36Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated compressed backups of a directory and retains only the newest N archives.

#### Purpose
- Generates a timestamped `.tar.gz` archive of a source directory into a backup location.
- Enforces retention policy by deleting older archives beyond a specified count.

#### Type
Bash shell script (executable utility).

#### Input
- `source_dir`: Path to the directory to be backed up.
- `backup_dir`: Path to the destination directory for archives.
- `keep` (optional): Integer count of archives to retain (default: 7).

#### Output
- Created archive file: `${name}_YYYYMMDDTHHMMSSZ.tar.gz` in `backup_dir`.
- Console logs: Archive size and list of removed files.
- Side effects: Deletion of old archives exceeding the `keep` limit.

#### Core logic
- Validates `keep` parameter is a positive integer and `source_dir` exists.
- Creates `backup_dir` if missing.
- Constructs archive filename using source directory basename and UTC timestamp.
- Uses `tar` to create a compressed archive to a temporary `.partial` file.
- Moves temporary file to final archive name on success.
- Lists existing archives for the source sorted by modification time (newest first).
- Iterates through the list and removes files starting from index `keep`.
- Cleans up temporary partial files on script exit via trap.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> `void` (script entry point)

#### Dependencies
- `bash` (shell environment).
- `tar` (archiving).
- `date` (timestamping).
- `ls` (file listing).
- `mv` (file moving).
- `rm` (file deletion).
- `du` (disk usage reporting).
- `cut` (field extraction).
- `mapfile` (array population).

#### Exceptions / Errors
- Missing arguments: Exits with usage message and code 2.
- Invalid `keep` value: Exits with error message and code 2.
- Missing `source_dir`: Exits with error message and code 2.
- Script failure: Trap ensures removal of partial archive files.
