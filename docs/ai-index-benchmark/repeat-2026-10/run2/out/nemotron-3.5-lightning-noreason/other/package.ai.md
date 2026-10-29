### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:54Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A collection of tools for data processing, streaming buffers, and financial reporting across image kernels, circular buffers, and backup/revenue scripts.

#### Purpose
This package provides three distinct functional units: a parallel OpenCL histogram computation for image/byte data, a fixed-capacity single-producer/consumer ring buffer for streaming, and shell/Python scripts for data management and financial reporting.

#### Responsibilities
- **Parallel histogram computation**: Executes a two-pass OpenCL kernel algorithm to count byte frequency distributions across large datasets using work-group accumulation and global reduction.
- **Streaming ring buffer**: Provides a fixed-size, power-of-two-constrained circular buffer implementation for efficient data transfer between a single producer and consumer.
- **Data management and reporting**: Manages backup retention via timestamped archives and generates monthly sales revenue reports from CSV order exports.

#### Key units
- `histogram_partial`: First OpenCL kernel pass that counts byte values into local memory via `atomic_inc` and writes partial histogram rows to global memory.
- `histogram_reduce`: Second OpenCL kernel pass that sums one bin across all partial histograms from all work-groups to produce the final 256-bin histogram.
- `ringbuf_t`: Struct defining buffer state containing data array, capacity, head and tail indices, and status flag.
- `ringbuf_status_t`: Enum with status codes `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init()`: Initializes buffer with power-of-two capacity validation and memory allocation.
- `ringbuf_write()`: Writes bytes from source into buffer, returning actual byte count copied.
- `ringbuf_read()`: Reads bytes from buffer into destination, returning actual byte count copied.
- `rotate_backups.sh`: Shell script that creates a timestamped `.tar.gz` archive of a source directory and deletes older backups to maintain a configurable retention count.
- `sales_report.py`: Python script that reads CSV order data, validates required columns (`order_date`, `region`, `amount`, `currency`), aggregates revenue into a `defaultdict` keyed by `(month, region)`, and prints a sorted report to stdout.

#### Data flow
- **Histogram**: Input byte data flows through a two-stage OpenCL pipeline: `histogram_partial` partitions bytes into work-group local buffers using `atomic_inc`, writes partial results to global memory; `histogram_reduce` iterates across all partial histograms, summing corresponding bins to accumulate the final 256-bin output.
- **Ring buffer**: Producer calls `ringbuf_write()` with source data and length; data copies into buffer at head index (up to two chunks if wrapping), head advances modulo capacity, returns byte count limited by available space. Consumer calls `ringbuf_read()` with destination buffer and length; data copies from tail index (up to two chunks), tail advances modulo capacity, returns byte count limited by used space.
- **Sales report**: `sales_report.py` takes CSV files, parses each row, skips currency mismatches, validates required columns and data formats, accumulates `Decimal` totals in a `defaultdict`, sorts results by `(month, region)`, and prints rows meeting the revenue threshold.
- **Backup rotation**: `rotate_backups.sh` takes `SOURCE_DIR`, `BACKUP_DIR`, and optional `KEEP`; validates inputs, creates a compressed archive, lists existing archives sorted by modification time, and removes those exceeding the retention limit.

#### Dependencies
- **kernels/**: OpenCL C 1.2 standard, `atomic_inc` for thread-safe local memory updates, `barrier(CLK_LOCAL_MEM_FENCE)` for work-group synchronization, global memory buffers for `partial` and `histogram` output.
- **ringbuf/**: `ringbuf.h` definitions, `<stdlib.h>` for `malloc`/`free`, `<string.h>` for `memcpy`, compiler support for `volatile` on head/tail.
- **scripts/**: `rotate_backups.sh` depends on bash, `tar`, `date`, `du`, `ls`, `mv`, `rm`; `sales_report.py` depends on `argparse`, `csv`, `sys`, `collections.defaultdict`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
- Error handling via specific status codes and guard conditions (e.g., `if (bin >= BINS) return;` in reduction, capacity validation in ring buffer init, CSV column validation in sales report).
- Two-kernel pattern in histogram enabling parallel efficiency for large byte streams.
- Power-of-two constraint in ring buffer enabling mask-based `(index) & (capacity - 1)` arithmetic.
- Both scripts enforce input validation and use exit codes to signal success or failure.
