### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:18Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of one directory and keeps only the newest N archives.

#### Purpose
- Compresses a source directory into a timestamped tar.gz archive in a backup directory.
- Retains only the newest KEEP archives per source directory, deleting older ones.

#### Type
Shell script (Bash), executable utility.

#### Input
- `SOURCE_DIR`: first positional argument, directory to back up.
- `BACKUP_DIR`: second positional argument, destination directory for archives.
- `KEEP`: optional third argument, positive integer count of archives to retain, defaults to 7.
- Reads filesystem contents of `SOURCE_DIR` and existing `BACKUP_DIR/${name}_*.tar.gz` archives.

#### Output
- Creates `BACKUP_DIR/${name}_YYYYMMDDTHHMMSSZ.tar.gz` via a `.partial` temp file.
- Removes archives beyond the KEEP limit from `BACKUP_DIR`.
- Prints creation and removal messages to stdout; errors to stderr.

#### Core logic
- Validates `KEEP` is a positive integer and `SOURCE_DIR` exists.
- Creates `BACKUP_DIR` if missing.
- Derives archive base name from `basename "$SOURCE_DIR"`.
- Generates UTC timestamped archive path and a `.partial` temp path.
- Installs an EXIT trap to remove the temp file on failure.
- Runs `tar -C` to compress the source directory into the temp file.
- Renames temp file to final archive path.
- Lists archives matching `${name}_*.tar.gz` newest first using `ls -1t`.
- Iterates over archives beyond index `KEEP` and removes each.

#### Public API
- `rotate_backups.sh <source_dir> <backup_dir> [keep=7]` -> exit code 0 on success, 2 on invalid input.

#### Dependencies
- Bash builtins: `set`, `mapfile`, `trap`, `basename`, `dirname`, `date`.
- External commands: `tar`, `mv`, `rm`, `ls`, `du`, `cut`, `mkdir`.

#### Exceptions / Errors
- Exit 2 with usage message if `SOURCE_DIR` or `BACKUP_DIR` missing.
- Exit 2 if `KEEP` is not a positive integer.
- Exit 2 if `SOURCE_DIR` is not a directory.
- EXIT trap removes `$tmp` on any failure.
