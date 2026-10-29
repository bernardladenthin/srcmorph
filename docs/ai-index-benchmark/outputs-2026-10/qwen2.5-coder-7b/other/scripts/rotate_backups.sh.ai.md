### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:05Z
- G: 
- A: 0.0.0
- X: file
---
> Rotates backups of a specified directory, keeping only the newest N archives.

#### Purpose
- Creates a compressed, dated backup of a source directory.
- Keeps only the newest N backup archives.
- Deletes older backup archives.

#### Type
- Bash script

#### Input
- `<source_dir>`: Directory to backup.
- `<backup_dir>`: Directory to store backups.
- `[keep=7]`: Number of newest archives to keep.

#### Output
- Created backup archive path and size.
- Removed older backup archive paths.

#### Core logic
- Validates input parameters.
- Ensures source directory exists.
- Creates backup directory if it doesn't exist.
- Generates a unique archive name with the current date and time.
- Creates a temporary archive file.
- Moves the temporary archive file to the final archive name.
- Lists all backup archives for the source directory.
- Deletes all archives except the newest N.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]`: Creates a backup of `<source_dir>` and keeps only the newest `keep` archives in `<backup_dir>`.

#### Dependencies
- `bash`: The script is written in Bash.
- `tar`: For creating the archive.
- `ls`: For listing files.
- `rm`: For deleting files.
- `du`: For getting the size of the archive.

#### Exceptions / Errors
- Exits with code 2 if `keep` is not a positive integer.
- Exits with code 2 if the source directory does not exist.
