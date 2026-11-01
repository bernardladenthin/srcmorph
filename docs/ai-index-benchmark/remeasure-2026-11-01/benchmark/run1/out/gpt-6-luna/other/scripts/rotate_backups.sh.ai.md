### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:51Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a dated compressed archive of a directory and removes older archives beyond the retention limit.

#### Purpose
- Back up one source directory and retain only the newest `KEEP` archives for that directory.
- Run directly with source and backup directories; optional retention count defaults to 7.

#### Type
- Bash (`#!/usr/bin/env bash`); strict mode: `set -euo pipefail`.

#### Input
- Positional argument 1 → `SOURCE_DIR`; required source directory.
- Positional argument 2 → `BACKUP_DIR`; required archive directory.
- Positional argument 3 → `KEEP`; optional positive integer, defaults to `7`.
- No option parser or environment variables read.
- Reads the contents of `SOURCE_DIR` for archiving and lists matching archives in `BACKUP_DIR`.

#### Output
- Creates `BACKUP_DIR` if needed.
- Writes a gzip-compressed tar archive named `<source-basename>_<UTC timestamp>.tar.gz`.
- Removes matching archives beyond the newest `KEEP`.
- Prints the created archive path and size, then each removed archive path.
- Exits with status 2 for invalid `KEEP` or a missing source directory; otherwise successful completion exits 0. With `set -e`, command failures terminate with a nonzero status.

#### Core logic
- Resolve required source and backup directories; set retention count to the third argument or `7`.
- Reject `KEEP` unless it is a positive integer; reject a nonexistent `SOURCE_DIR`.
- Create `BACKUP_DIR`.
- Derive the source basename, UTC-timestamped archive path, and `.partial` temporary path.
- Register an `EXIT` trap to remove the temporary archive.
- Create the compressed tar archive in the temporary path, then move it to the final path.
- Print the archive path and human-readable size.
- List matching archives newest first and remove entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` — array populated from the newest-first archive listing.
- `old` — loop variable for archives removed during retention cleanup.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `du`, `cut`, `ls`, `rm`.
- Bash features: `[[ ]]`, `mapfile`, process substitution, arrays, `EXIT` trap.

#### Errors and exit codes
- Exit 2 if `KEEP` is not a positive integer or `SOURCE_DIR` is not a directory.
- Required-argument expansions fail with their usage message when arguments 1 or 2 are missing.
- `EXIT` trap runs `rm -f "$tmp"` to clean up the partial archive.
- No retries or timeouts.
