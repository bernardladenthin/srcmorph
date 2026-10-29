### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:19Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated, compressed backup of a directory and prunes old archives, keeping only the newest N.

#### Purpose
- Creates a timestamped tar.gz archive of a source directory into a backup directory.
- Retains only the newest K archives, deleting older ones.

#### Type
Shell script (bash). Uses standard POSIX utilities (`tar`, `mv`, `ls`, `rm`, `du`).

#### Input
- `source_dir` (required): The directory to back up.
- `backup_dir` (required): The directory where archives are stored.
- `keep` (optional, default 7): Positive integer specifying how many recent archives to retain.

#### Output
- Writes a new `.tar.gz` archive to `backup_dir` named `<basename>_YYYYMMDDTHHMMSSZ.tar.gz`.
- Prints the path and size of the created archive.
- Deletes excess archives beyond the `keep` count from `backup_dir`.
- Outputs a message per removed archive.

#### Core logic
- Validates that `keep` is a positive integer and that `source_dir` exists.
- Creates the backup directory if it does not exist.
- Archives the source directory using `tar`, producing a compressed file with a UTC timestamp.
- Moves the temporary archive to its final location.
- Lists archives in `backup_dir` matching the pattern `<name>_*.tar.gz`, sorted newest first.
- Iterates over archives beyond the `keep` limit and removes them.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep]` – creates and prunes backups.

#### Dependencies
- `bash`, `tar`, `mv`, `ls`, `rm`, `du`, `date`, `mapfile`.

#### Exceptions / Errors
- Exits with code 2 and prints usage if `keep` is not a positive integer.
- Exits with code 2 if `source_dir` does not exist.
