### rotate_backups.sh
- H: 1.0
- C: F50CF6FE
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:14:53Z
- G: 
- A: 0.0.0
- X: file
---
> Creates a compressed, dated backup of a directory and retains only the most recent N backups

#### Purpose
- Generates a timestamped, compressed backup of a source directory  
- Automatically removes old backups to maintain a limited retention window

#### Type
Script (bash) with trap and command substitution

#### Input
- Source directory path (required)  
- Backup directory path (required)  
- Optional keep count (default 7; must be positive integer)

#### Output
- A compressed tar.gz archive with timestamped name in the backup directory  
- Removed old archives beyond the specified keep count  
- Logs creation and deletion actions to stdout

#### Core logic
- Validates input arguments and ensures keep is a positive integer  
- Checks that source directory exists  
- Creates a temporary partial archive and moves it to final location  
- Lists all archives matching the source name in chronological order (newest first)  
- Deletes archives beyond the keep threshold  

#### Public API
- rotate_backups.sh(source_dir, backup_dir, [keep=7]) → void  
  Creates and rotates backups of a directory  

#### Dependencies
- GNU tar  
- GNU date  
- bash (with array expansion and trap support)

#### Exceptions / Errors
- Exits with code 2 if source or backup directories don't exist  
- Exits with code 2 if keep is not a positive integer  
- Logs error messages to stderr on failure
