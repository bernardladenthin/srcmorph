### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:37:50Z
- G: 
- A: 0.0.0
- X: file
---
> Creates compressed directory backups with a retention policy to keep only the most recent archives.

#### Purpose
* Automate directory archiving into compressed tarballs.
* Implement a rotation policy to prevent disk space exhaustion by limiting the number of stored backups.

#### Type
Bash script.

#### Input
* `SOURCE_DIR` (Positional Arg 1): Path to the directory to be backed up.
* `BACKUP_DIR` (Positional Arg 2): Destination path where archives are stored.
* `KEEP` (Positional Arg 3): Number of newest archives to retain (defaults to 7).

#### Output
* A timestamped `.tar.gz` archive in `BACKUP_DIR`.
* Deletion of archives exceeding the `KEEP` threshold.
* Console logs indicating created and removed files.

#### Core logic
* Validates that `KEEP` is a positive integer and `SOURCE_DIR` exists.
* Ensures `BACKUP_DIR` exists via `mkdir -p`.
* Generates a filename using the `basename` of `SOURCE_DIR` and a UTC timestamp (`%Y%m%dT%H%M%SZ`).
* Performs atomic backup by compressing `SOURCE_DIR` into a `.partial` file using `tar -czf` before renaming it to the final archive name.
* Uses a `trap` to ensure `.partial` files are removed on exit.
* Identifies existing archives for the specific source using `ls -1t` (sorted by newest first).
* Slices the list of archives and deletes all entries beyond the `KEEP` index.

#### Public API
`rotate_backups.sh <source_dir> <backup_dir> [keep]` -> exit code (0 success, 2 validation error).

#### Dependencies
* `tar` (compression)
* `date` (timestamping)
* `ls` (file listing/sorting)
* `du` (size reporting)
* `mkdir`, `mv`, `rm` (filesystem management)

#### Exceptions / Errors
* Exit 2: Missing required arguments.
* Exit 2: `KEEP` value is not a positive integer.
* Exit 2: `SOURCE_DIR` is not a valid directory.
* `set -euo pipefail`: Script terminates immediately on any command failure or unset variable.
