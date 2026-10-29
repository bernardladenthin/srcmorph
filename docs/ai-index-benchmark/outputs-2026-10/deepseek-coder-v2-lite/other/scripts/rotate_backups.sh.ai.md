### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:35:44Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of one directory and keeps only the newest N archives.

#### Purpose
- Create a compressed, dated backup of a directory.
- Keep only the newest N archives.

#### Type
- Shell script (`bash`)

#### Input
- `<source_dir>`: The directory to be backed up.
- `<backup_dir>`: The directory where backups will be stored.
- `[keep=7]`: Optional parameter to specify the number of archives to keep (default is 7).

#### Output
- Creates a compressed archive with a timestamp in the backup directory.
- Removes older backups if the number exceeds the specified limit.

#### Core logic
1. Validate input parameters.
2. Ensure the source directory exists.
3. Create a backup directory if it doesn't exist.
4. Compress the source directory into a temporary file.
5. Rename the temporary file to the final archive name.
6. Clean up old backups to keep only the newest N archives.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]`
  - Creates a backup of `<source_dir>` and stores it in `<backup_dir>`.
  - Keeps only the newest `[keep]` archives.

#### Dependencies
- `basename`, `date`, `dirname`, `tar`, `mkdir`, `ls`, `cut`, `du`, `mapfile`

#### Exceptions / Errors
- If `keep` is not a positive integer, prints an error message and exits with code 2.
- If `<source_dir>` does not exist, prints an error message and exits with code 2.
