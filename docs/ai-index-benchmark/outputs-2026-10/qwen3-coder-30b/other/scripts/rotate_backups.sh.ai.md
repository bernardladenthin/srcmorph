### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:19:33Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed, dated backups of directories while maintaining only a specified number of the most recent archives.

#### Purpose
- Generates compressed tar.gz backups of a source directory with UTC timestamps
- Retains only the newest N backup archives, deleting older ones

#### Type
Shell script (Bash)

#### Input
- Source directory path to backup
- Backup directory destination
- Optional integer parameter for number of archives to keep (default 7)

#### Output
- Compressed tar.gz archive files with timestamp suffixes
- Removal of older backup archives beyond the retention limit
- Standard output messages indicating created/removed files

#### Core logic
- Validates input parameters including keep count and source directory existence
- Creates backup archive with timestamped filename in backup directory
- Lists existing archives for source directory sorted by modification time (newest first)
- Removes archives beyond the specified retention count

#### Public API
- rotate_backups.sh(source_dir, backup_dir, keep=7) -> void

#### Dependencies
- tar command for compression
- date command for timestamp generation
- ls command for archive listing
- du command for file size reporting

#### Exceptions / Errors
- Exits with code 2 if keep parameter is not a positive integer
- Exits with code 2 if source directory does not exist
- Exits on any command failure due to set -e option
