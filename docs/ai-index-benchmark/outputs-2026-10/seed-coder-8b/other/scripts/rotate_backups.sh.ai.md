### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:24:10Z
- G: 
- A: 0.0.0
- X: file
---
> Rotates and manages compressed, time-stamped backups of a directory

#### Purpose
- Manages backup rotation for a specified directory
- Maintains a specified number of recent backups

#### Type
- Bash script

#### Input
- Source directory path
- Backup directory path
- Optional: number of backups to keep (default 7)

#### Output
- Compressed archive of source directory
- Console messages about creation and deletion of backups

#### Core logic
- Validates input parameters
- Creates a timestamped archive of source directory
- Rotates and deletes old archives beyond the specified keep count

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]`
  - Manages backup rotation for specified directory

#### Dependencies
- Bash shell
- `tar` command
- `date` command
- `ls` command

#### Exceptions / Errors
- Exits with error code 2 for invalid inputs or missing directories
- Handles potential interruptions by cleaning up temporary files
