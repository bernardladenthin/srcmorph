### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:50:06Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> This package provides OpenCL kernels for parallel byte frequency histogram computation, a fixed-capacity circular byte buffer for streaming data, and scripts for backup rotation and monthly sales revenue reporting.

#### Purpose
- Generates byte frequency histograms for image or byte stream data using a two-pass OpenCL kernel approach.
- Implements a power-of-two sized ring buffer for efficient single-producer single-consumer streaming and provides backup rotation and sales performance reporting utilities.

#### Responsibilities
- Kernels: Compute per-workgroup partial histograms (histogram_partial), aggregate partial histograms across workgroups (histogram_reduce), and manage __global and __local memory buffers.
- Ringbuf: Buffer lifecycle (initialization, allocation, cleanup), read/write operations with wrap-around, status reporting via ringbuf_status_t codes, and single-producer single-consumer semantics.
- Scripts: Rotate backup archives (create dated tar.gz, retain newest N), and generate sales reports (parse CSV, aggregate revenue by month and region).

#### Key units
- histogram_partial(input, length, partial, local_bins) -> void
- histogram_reduce(partial, group_count, histogram) -> void
- histogram.cl: OpenCL C 1.2 kernel source containing two __kernel functions
- ringbuf_t struct and ringbuf_status_t enum
- ringbuf_init, ringbuf_free, ringbuf_write, ringbuf_read, ringbuf_used, ringbuf_free_space
- rotate_backups.sh: Bash script with rotate_backups.sh(source_dir, backup_dir, keep) -> void
- sales_report.py: Python module with Order dataclass, parse_orders, revenue_by_month_and_region, main

#### Data flow
- Kernels: Pass 1 resets local_bins for 256 bins, atomically increments counters per byte, writes partial histogram to global array per workgroup; Pass 2 sums partial values across group_count workgroups; output final 256-bin histogram.
- Ringbuf: ringbuf_init checks power-of-two capacity and allocates buffer; ringbuf_write copies source data into buffer at head with wrap; ringbuf_read copies stored data from tail with wrap; ringbuf_used/free_space compute used/free space; ringbuf_free releases buffer.
- Scripts: rotate_backups.sh validates inputs, creates dated archive, prints messages; sales_report.py parses CSV via parse_orders, aggregates via revenue_by_month_and_region, prints month/region/revenue lines.

#### Dependencies
- OpenCL C 1.2 runtime, atomic_inc, OpenCL built-ins (barrier, get_local_id, get_global_id, get_group_id, get_local_size, get_global_size)
- ringbuf.h, stdlib.h, string.h, stdint.h, stddef.h
- Bash built-ins (set, trap, mapfile, basename, mkdir, rm, mv) and external utilities tar, date, ls, du
- Python modules: argparse, csv, sys, collections.defaultdict, dataclasses.dataclass, datetime.date, decimal.Decimal, decimal.InvalidOperation

#### Cross-cutting
- Two-pass algorithm with __global and __local memory usage in kernels.
- Atomic increment for thread-safe counter updates.
- No explicit error handling; relies on correct OpenCL environment and valid buffers.
- Potential undefined behavior if group_count is zero or buffers are uninitialized.
- Ring buffer capacity must be a power of two; invalid capacity triggers RINGBUF_EINVAL; allocation failure triggers RINGBUF_ENOMEM.
- Single-producer single-consumer semantics without per-call dynamic allocation.
- Both scripts use exit codes to signal success (0) or errors (1 for no orders, 2 for invalid arguments or missing source).
