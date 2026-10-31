### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:35:47Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of one directory and keeps only the newest N archives.

#### Purpose
- Creates a compressed, dated backup of one directory
- Keeps only the newest N archives
- Usage: rotate_backups.sh <source_dir> <backup_dir> [keep]

#### Type
- Shell script (bash)

#### Input
- source_dir: source directory to back up
- backup_dir: destination directory for archives
- keep: number of archives to retain (default 7)
- name: basename of source_dir
- archive: backup archive path with timestamp
- tmp: partial archive path

#### Output
- archive: compressed tar.gz archive
- printed archive path and size
- printed removed archive paths

#### Core logic
- Validate keep is a positive integer
- Validate source_dir exists
- Create backup_dir if missing
- Build archive name from source basename and UTC timestamp
- Create partial tar.gz archive in backup_dir
- Move partial archive to final archive name
- List archives matching source prefix, sorted newest first
- Delete archives beyond the first KEEP entries

#### Public API
- rotate_backups.sh(source_dir, backup_dir, keep) -> exit code: creates and rotates backups

#### Dependencies
- tar
- date
- du
- ls
- mapfile

#### Exceptions / Errors
- exit 2: usage error, invalid keep, missing source_dir
- set -euo pipefail: abort on error, undefined variable, or pipeline failure
