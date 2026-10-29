### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:09:06Z
- G: 
- A: 0.0.0
- X: file
---
> Creates dated backups of a directory and retains only the newest N archives

#### Purpose
- Creates a compressed, dated backup of SOURCE_DIR
- Retains only the newest KEEP archives in BACKUP_DIR

#### Type
bash script / backup rotation utility

#### Input
- SOURCE_DIR : source directory to archive
- BACKUP_DIR : target directory for backup archives
- KEEP : number of newest archives to keep (default 7)

#### Output
- Creates dated tar.gz archive `${name}_YYYYMMDDTHHMMSSZ.tar.gz` in BACKUP_DIR
- Removes archives beyond the first KEEP entries sorted newest first
- Prints creation and removal status to stdout

#### Core logic
- Validates KEEP is a positive integer; exits with code 2 if invalid
- Checks SOURCE_DIR exists; exits with code 2 if missing
- Creates BACKUP_DIR if absent
- Sets `name` to basename of SOURCE_DIR
- Archives SOURCE_DIR into a partial file then moves to final `archive` path in BACKUP_DIR
- Lists existing archives in BACKUP_DIR matching `${name}_*.tar.gz` sorted newest first via `ls -1t`
- Deletes archives indexed after the first KEEP entries
- Uses trap to remove partial `tmp` archive on script EXIT

#### Public API
- `rotate_backups.sh(source_dir, backup_dir, keep?) -> exitCode` rotates and prunes dated backups

#### Dependencies
- tar : archives SOURCE_DIR into tar.gz
- ls : lists BACKUP_DIR archives sorted by modification time
- mapfile : reads archive list into bash array
- du : reports archive size in human-readable form
- basename : extracts directory name from SOURCE_DIR path
- dirname : extracts parent path for tar invocation
- date : generates UTC timestamp for archive name
- mv : moves partial archive to final location
- rm : removes obsolete backup archives
- set -euo pipefail : enforces strict error handling

#### Exceptions / Errors
- invalid KEEP argument exits with code 2
- missing SOURCE_DIR exits with code 2
- unexpected errors abort via set -euo pipefail
- partial archive cleanup handled by trap on EXIT
