### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:09:50Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the newest N archives

#### Purpose
- Script to automate backup creation and cleanup

#### Type
- Shell script (bash)

#### Input
- `<source_dir>`: Directory to back up
- `<backup_dir>`: Directory to store backups
- `[keep]`: Number of backups to retain (default 7)

#### Output
- Creates compressed archive of `<source_dir>` in `<backup_dir>`
- Keeps only the most recent `<keep>` archives

#### Core logic
- Validates parameters and directories
- Generates timestamped archive name
- Creates compressed archive of source directory
- Moves archive to `<backup_dir>`
- Lists recent archives and deletes oldest beyond `<keep>`

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` -> Creates backup

#### Dependencies
- `tar`: For archiving directories
- `date`: For timestamp generation
- `ls`: For listing backup files
- `mapfile`: For reading directory contents into an array

#### Exceptions / Errors
- Checks for valid `<source_dir>` and `<backup_dir>`
- Validates `keep` as positive integer
- Handles missing directories and invalid parameters
