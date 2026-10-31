### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:52:33Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated tar.gz backup of a directory and automatically prunes older archives to keep only the newest N copies.

#### Purpose
- Compresses SOURCE_DIR into a timestamped tar.gz archive inside BACKUP_DIR.
- Retains only the newest KEEP archives matching the source directory name and removes the rest.

#### Type
- Bash shell script, executable automation utility.
- Procedural script with positional parameters; no classes, interfaces, or generics.

#### Input
- SOURCE_DIR: first positional argument, directory to archive.
- BACKUP_DIR: second positional argument, destination directory for archives.
- KEEP: optional third positional argument, positive integer retention limit, default 7.
- Reads SOURCE_DIR contents through tar.
- Reads existing BACKUP_DIR/${name}_*.tar.gz files through ls.

#### Output
- Writes BACKUP_DIR/${name}_<UTC timestamp>.tar.gz compressed archive.
- Deletes archives beyond the KEEP threshold.
- Emits creation and removal status lines to stdout.
- Removes partial archive file on EXIT via trap, including failure paths.

#### Core logic
- Validate KEEP against ^[1-9][0-9]*$ and exit 2 on mismatch.
- Validate SOURCE_DIR exists as a directory and exit 2 if missing.
- Create BACKUP_DIR with mkdir -p if absent.
- Derive name from basename of SOURCE_DIR.
- Construct archive path as BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz.
- Stage compression to tmp file with .partial suffix and register EXIT trap to rm -f it.
- Run tar -C parent directory -czf tmp on the source name.
- Rename tmp to final archive path.
- List BACKUP_DIR/${name}_*.tar.gz sorted newest first with ls -1t into mapfile.
- Iterate array slice from index KEEP onward and rm -f each old archive.

#### Public API
- rotate_backups.sh(source_dir, backup_dir, keep=7) -> exit 0 on success, exit 2 on invalid arguments or missing source.

#### Dependencies
- bash, tar, gzip, date, du, cut, ls, mapfile, basename, dirname, mv, rm, mkdir, trap.

#### Exceptions / Errors
- exit 2 when KEEP is not a positive integer.
- exit 2 when SOURCE_DIR is not a directory.
- set -euo pipefail aborts on any command failure or unset variable.
- EXIT trap removes $tmp partial archive if script fails before mv.
