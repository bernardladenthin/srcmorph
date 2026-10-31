### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:29:59Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the newest N archives.

#### Purpose
- Backs up a source directory into a timestamped tar.gz archive.
- Maintains only the newest N archives in the backup location.

#### Type
- Bash shell script; no classes or modules.

#### Input
- source_dir: path to source directory (required).
- backup_dir: path to backup directory (required).
- keep: number of archives to retain (optional, default 7).

#### Output
- Creates a dated archive file in backup_dir.
- Prints creation and deletion messages to stdout.
- No return value; exits with status 0 on success.

#### Core logic
- Validates keep is a positive integer.
- Validates source directory exists.
- Creates backup directory if missing.
- Archives source directory contents to a dated tar.gz.
- Removes temporary partial archive on exit.
- Lists archives sorted newest first.
- Deletes all but the newest keep archives.

#### Public API
- rotate_backups.sh(source_dir, backup_dir, keep) -> void
  - Creates a compressed dated backup of source.
  - Retains only the newest N archives.

#### Dependencies
- Bash built-ins: set, trap, mapfile, basename, mkdir, rm, mv.
- External utilities: tar, date, ls, du.

#### Exceptions / Errors
- keep not a positive integer: exits with code 2 and error message.
- source directory missing: exits with code 2.
- Invalid usage: missing arguments handled by set -e.
