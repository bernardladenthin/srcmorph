#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
#
# SPDX-License-Identifier: Apache-2.0
#
# Creates a compressed, dated backup of one directory and keeps only the newest N archives.
# Usage: rotate_backups.sh <source_dir> <backup_dir> [keep=7]
set -euo pipefail

SOURCE_DIR="${1:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"
BACKUP_DIR="${2:?usage: rotate_backups.sh <source_dir> <backup_dir> [keep]}"
KEEP="${3:-7}"

if ! [[ "$KEEP" =~ ^[1-9][0-9]*$ ]]; then
  echo "keep must be a positive integer, got: $KEEP" >&2
  exit 2
fi
[ -d "$SOURCE_DIR" ] || { echo "no such directory: $SOURCE_DIR" >&2; exit 2; }
mkdir -p "$BACKUP_DIR"

name="$(basename "$SOURCE_DIR")"
archive="$BACKUP_DIR/${name}_$(date -u +%Y%m%dT%H%M%SZ).tar.gz"
tmp="$archive.partial"
trap 'rm -f "$tmp"' EXIT

tar -C "$(dirname "$SOURCE_DIR")" -czf "$tmp" "$name"
mv "$tmp" "$archive"
echo "created $archive ($(du -h "$archive" | cut -f1))"

# Newest first; delete everything after the first KEEP archives of this source.
mapfile -t archives < <(ls -1t "$BACKUP_DIR/${name}"_*.tar.gz)
for old in "${archives[@]:$KEEP}"; do
  rm -f -- "$old"
  echo "removed $old"
done
