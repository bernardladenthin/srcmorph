### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:17Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated, compressed backups of a source directory and retains only the N most recent archives.

#### Purpose
- Generates a compressed `.tar.gz` backup of a specified source directory with a UTC timestamp.
- Enforces retention policy by deleting older archives beyond a configurable count.

#### Type
Bash shell script (executable utility).

#### Input
- `source_dir`: Path to the directory to be backed up.
- `backup_dir`: Path to the destination directory for archives.
- `keep` (optional): Integer count of archives to retain (default: 7).

#### Output
- Creates a new compressed archive file in `backup_dir` named `<source_name>_<timestamp>.tar.gz`.
- Prints status messages for creation and removal of files.
- Removes older archive files from `backup_dir` exceeding the retention limit.

#### Core logic
- Validates `keep` argument is a positive integer and `source_dir` exists.
- Creates `backup_dir` if missing.
- Constructs a unique archive filename using the source directory basename and UTC timestamp.
- Uses `tar` to create a compressed archive to a temporary `.partial` file.
- Atomically moves the temporary file to the final archive name.
- Lists existing archives matching the source pattern, sorted by modification time (newest first).
- Iterates through the list and removes files starting from index `keep`.
- Cleans up temporary files on exit via trap.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> `void` (script execution)

#### Dependencies
- `bash` (shell environment)
- `tar` (archiving)
- `date` (timestamp generation)
- `ls` (file listing)
- `mv` (file movement)
- `rm` (file deletion)
- `du` (disk usage reporting)
- `cut` (text processing)
- `mapfile` (array population)

#### Exceptions / Errors
- Exits with code 2 if `keep` is not a positive integer.
- Exits with code 2 if `source_dir` does not exist.
- Script fails immediately on any command error due to `set -euo pipefail`.
