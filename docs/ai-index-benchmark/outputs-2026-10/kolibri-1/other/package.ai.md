### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T08:41:09Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A package providing byte-level histogram computation, lock-free byte streaming, and sales reporting/backup automation.

#### Purpose
- Computes 256-bin byte histograms of image or byte stream inputs
- Provides fixed-capacity single-producer single-consumer byte ring buffer
- Generates monthly sales reports from CSV order exports and rotates directory backups

#### Responsibilities
- Histogram computation: partial per-work-group histograms reduced into a final 256-bin histogram via two-pass OpenCL kernels
- Ring buffer management: bounded circular byte buffer with two-chunk boundary-spanning copies and power-of-two capacity validation
- Sales reporting: CSV order parsing, revenue aggregation by month and region, currency conversion, and filtered report printing
- Backup rotation: timestamped tar.gz archive creation from a source directory with pruning of older archives beyond a keep limit

#### Key units
- histogram_partial, histogram_reduce, histogram: OpenCL partial and final 256-bin uint histograms
- ringbuf_t, ringbuf_status_t, ringbuf_init, ringbuf_write, ringbuf_read: ring buffer struct, status enum, and lifecycle functions
- Order dataclass, parse_orders, revenue_by_month_and_region, main: order representation, CSV reader, revenue aggregation, and CLI entry point
- rotate_backups.sh: shell script for backup creation and rotation

#### Data flow
- Byte stream flows into histogram_partial per work-group, then partials reduce into final histogram
- Producer writes bytes into ringbuf_t at head, consumer reads from tail into destination pointer
- CSV files flow through parse_orders into Order objects, then revenue_by_month_and_region aggregates into (month, region) to Decimal mapping, then filtered report prints to stdout
- Source directory flows into timestamped tar.gz archive, then pruned by newest-first listing

#### Dependencies
- OpenCL C 1.2 built-ins: get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier, CLK_LOCAL_MEM_FENCE
- Python standard library: argparse, csv, sys, defaultdict, dataclass, date, Decimal, InvalidOperation
- Shell utilities: tar, date, du, ls, mapfile
- stdlib.h, string.h, stddef.h, stdint.h

#### Cross-cutting
- Two-pass OpenCL kernel strategy with per-work-group local bins and global partial array
- Power-of-two capacity validation using bitmask checks with one slot reserved
- Two-chunk copy logic for reads and writes spanning buffer boundaries
- Single-producer single-consumer ownership model for head and tail indices
- argparse-based CLI argument parsing in Python script
- set -euo pipefail error handling in shell script
- Decimal-based monetary arithmetic in Python script
- UTC timestamping in both scripts for archive naming and date parsing
- No explicit exception handling in OpenCL kernel source
