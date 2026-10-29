### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:13:13Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed, dated backups of a directory while retaining only the newest N archives

#### Purpose
- Compresses a specified source directory into a dated archive file
- Maintains a configurable number of the most recent backups
- Deletes older backups beyond the specified retention count

#### Type
Shell script (bash) with no dependencies on external classes or libraries

#### Input
- `SOURCE_DIR`: Directory to be backed up (required)
- `BACKUP_DIR`: Destination directory for the backup archives (required)
- `KEEP`: Number of archives to retain (optional, defaults to 7)

#### Output
- Creates a compressed archive of `SOURCE_DIR` in `BACKUP_DIR` with a timestamped filename
- Deletes older archives in `BACKUP_DIR` to maintain only the newest `KEEP` backups

#### Core logic
1. Validates input parameters and directory existence
2. Constructs the archive filename using the source directory name and current UTC timestamp
3. Creates a temporary file to hold the tarball while compressing
4. Compresses the source directory into the temporary file
5. Renames the temporary file to the final archive name
6. Lists archives in `BACKUP_DIR` sorted by newest first
7. Deletes archives beyond the specified `KEEP` count

#### Public API
None - this is a standalone script invoked directly

#### Dependencies
- `tar`: For creating compressed archive files
- `date`: For generating timestamped filenames
- `du`: For reporting archive sizes
- Standard shell utilities: `basename`, `mkdir`, `ls`, `mv`, `rm`

#### Exceptions / Errors
- Exits with status 2 if `KEEP` is not a positive integer
- Exits with status 2 if `SOURCE_DIR` does not exist
- Prints error messages to stderr for invalid input or missing directories
