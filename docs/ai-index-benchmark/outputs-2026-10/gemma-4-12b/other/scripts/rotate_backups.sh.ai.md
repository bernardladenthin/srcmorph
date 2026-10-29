### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:21:27Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and maintains a rolling history of the N most recent archives.

#### Purpose
* Automates directory archiving with timestamped filenames.
* Implements a retention policy to prune old backup files.

#### Type
Shell script (Bash).

#### Input
* SOURCE_DIR: Path to the directory to be backed up.
* BACKUP_DIR: Path where the compressed archives will be stored.
* KEEP: Integer specifying the number of recent archives to retain (defaults to 7).

#### Output
* A `.tar.gz` file in the backup directory.
* Console output showing creation size and deletion status.
* Side effect: Deletes files in BACKUP_DIR exceeding the KEEP count.

#### Core logic
* Validates that KEEP is a positive integer and SOURCE_DIR exists.
* Creates the BACKUP_DIR if it does not exist.
* Generates a filename using the source directory's basename and a UTC timestamp.
* Archives the source directory into a `.partial` file using `tar`.
* Atomically moves the partial file to the final archive name.
* Lists archives for the specific source, sorted by modification time (newest first).
* Iterates through the list and deletes files beyond the specified KEEP index.

#### Dependencies
* tar
* date
* du
* ls
* mkdir

#### Exceptions / Errors
* Exits with status 2 if arguments are missing or KEEP is not a positive integer.
* Exits with status 2 if SOURCE_DIR is not a valid directory.
* Uses `set -euo pipefail` to exit on any command failure or unset variable.
