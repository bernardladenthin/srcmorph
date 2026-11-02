### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:26Z
- G: 
- A: 0.0.0
- X: file
- P: 87336179
---
> Creates a dated compressed archive of a source directory and prunes older archives in its backup directory.

#### Purpose
- Back up one directory to a timestamped `.tar.gz` archive and retain only the newest requested number.
- Run directly with source and backup directory arguments.

#### Type
- Bash; `set -euo pipefail`.

#### Input
- Positional arguments: `$1` sets `SOURCE_DIR`; `$2` sets `BACKUP_DIR`; `$3` sets `KEEP`, defaulting to `7`.
- `KEEP` must be a positive integer.
- Reads `SOURCE_DIR` and its parent directory; creates or reads `BACKUP_DIR`.

#### Output
- Creates a compressed archive named `${name}_<UTC timestamp>.tar.gz` in `BACKUP_DIR`.
- Prints the created archive path and human-readable size; prints each removed archive path.
- Removes archives beyond the newest `KEEP` matching archives.

#### Core logic
- Validates `KEEP` and verifies that `SOURCE_DIR` is a directory; creates `BACKUP_DIR`.
- Derives the source directory’s basename and constructs a UTC timestamped archive path and `.partial` temporary path.
- Registers an `EXIT` trap to remove the temporary file.
- Archives the source directory from its parent into the temporary path, then renames it to the final archive path.
- Prints the archive path and size.
- Lists matching archives newest first and removes entries after the first `KEEP`.

#### Constants and configuration
- `SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"`
- `KEEP="${3:-7}"`
- `name="$(basename "$SOURCE_DIR")"`
- `archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"`
- `tmp="$archive.partial"`
- `archives` receives the matching archive paths from `mapfile`.
- `old` holds each archive selected for removal.

#### Dependencies
- External commands: `basename`, `date`, `mkdir`, `tar`, `dirname`, `mv`, `echo`, `du`, `cut`, `ls`, `rm`.
- Bash builtins/features: `mapfile`, process substitution, array slicing, `[[ ... =~ ... ]]`.

#### Errors and exit codes
- Exits with status `2` if `KEEP` is not a positive integer or `SOURCE_DIR` does not exist as a directory.
- Missing `$1` or `$2` triggers the parameter-expansion usage error; `set -u` and `set -e` cause failures to exit.
- `EXIT` trap runs `rm -f "$tmp"` to remove the temporary archive file.
