### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:45:27Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides parallel byte-frequency analysis, lock-free streaming buffers, and operational scripts for backup rotation and financial reporting.

#### Purpose
- Computes 256-bin byte histograms via OpenCL parallel reduction and manages high-throughput byte streams with a circular buffer.
- Automates system backup retention and aggregates financial revenue data from CSV exports.

#### Responsibilities
- **Parallel Histogramming**: Executes two-pass OpenCL kernels for local atomic counting and global reduction of byte frequencies.
- **Stream Buffering**: Manages single-producer single-consumer circular buffers with power-of-two optimized indexing and wrap-around logic.
- **Operational Automation**: Enforces backup retention policies via shell scripts and generates regional revenue reports via Python CLI.

#### Key units
- `histogram_partial`: OpenCL kernel performing local per-group byte frequency counting via `atomic_inc`.
- `histogram_reduce`: OpenCL kernel summing partial histogram entries into the final 256-bin output.
- `ringbuf_t`: Core structure managing `data`, `head`, `tail`, and `capacity` for lock-free byte streaming.
- `ringbuf_write` / `ringbuf_read`: Functions handling data transfer with automatic wrap-around in at most two chunks.
- `rotate_backups.sh`: Bash utility creating timestamped `.tar.gz` archives and enforcing retention counts.
- `sales_report.py`: Python CLI parsing `Order` dataclasses to aggregate revenue by month and region.
- `parse_orders`: Generator function yielding valid `Order` objects from CSV inputs.
- `revenue_by_month_and_region`: Aggregation function returning totals keyed by `(YYYY-MM, region)`.

#### Data flow
1. **Histogram Pipeline**: Input byte stream -> `histogram_partial` (local counting to `partial` buffer) -> `histogram_reduce` (global summation to `histogram` output).
2. **Buffer Pipeline**: `ringbuf_init` allocates memory -> `ringbuf_write` updates `head` with wrap logic -> `ringbuf_read` updates `tail` with wrap logic -> `ringbuf_free` deallocates.
3. **Backup Pipeline**: Validate inputs -> compress to `.partial` -> rename to timestamped archive -> list/delete excess files.
4. **Sales Pipeline**: Read CSV -> validate headers -> filter by currency -> parse `Order` fields -> aggregate by month/region -> sort and print.

#### Dependencies
- **OpenCL C 1.2**: Primitives for work-item indexing, barriers, and atomic operations.
- **Standard C Library**: `<stdlib.h>`, `<string.h>`, `<stddef.h>`, `<stdint.h>` for buffer memory and data handling.
- **Python Standard Lib**: `argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal` for financial logic.
- **System Utilities**: `bash`, `tar`, `date`, `ls`, `mv`, `rm` for backup operations.

#### Cross-cutting
- **Concurrency**: OpenCL kernels use `barrier` and `atomic_inc`; C buffer is lock-free for single-producer single-consumer.
- **Validation**: Strict checks for power-of-two capacity, positive integers, and CSV headers; scripts exit on failure.
- **Optimization**: Bitwise AND (`&`) used for index wrapping in `ringbuf` instead of modulo; two-pass reduction minimizes global memory contention.
- **Error Handling**: C returns status codes (`RINGBUF_EINVAL`, `RINGBUF_ENOMEM`); scripts use exit codes and `trap` for cleanup.
