### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:11Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-processing utilities alongside operational backup rotation and currency-filtered regional sales reporting.

#### Purpose
- Groups GPU byte-histogram kernels, a single-producer/single-consumer byte ring buffer, and scripts for backup retention and monthly revenue reporting.
- Supports byte-stream processing and practical data-management tasks.

#### Responsibilities
- **Parallel byte analysis (`kernels/`):** Count byte values into 256-bin histograms using per-work-group partial counts and reduction.
- **FIFO byte buffering (`ringbuf/`):** Manage buffer lifecycle, wrapped byte reads and writes, and occupancy queries with one slot reserved to distinguish full from empty.
- **Operational and reporting scripts (`scripts/`):** Create and rotate compressed directory archives; validate order CSVs and print monthly regional revenue totals filtered by currency and minimum amount.

#### Key units
- `histogram_partial` — counts byte values into a partial histogram per work-group.
- `histogram_reduce` — combines partial counts into the final 256-bin histogram.
- `ringbuf_t` — holds byte storage, capacity, and producer/consumer indices.
- `ringbuf_init`, `ringbuf_free` — initialize and release ring-buffer storage.
- `ringbuf_write`, `ringbuf_read` — enqueue and dequeue bytes, including wrapped transfers.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable capacity.
- `ringbuf_status_t` — represents success, invalid capacity, and allocation failure.
- `rotate_backups.sh` — creates timestamped gzip archives and enforces a configurable retention limit.
- `Order` — immutable order date, region, and decimal amount record.
- `parse_orders(path, currency)` — validates CSV data and yields matching-currency orders.
- `revenue_by_month_and_region(orders)` — aggregates and sorts totals by month and region.
- `main(argv=None)` — parses report options and prints the filtered tab-separated results.

#### Data flow
- **Histogram:** Input bytes and length → per-work-group partial histograms → reduction to the final histogram.
- **Ring buffer:** Initialize power-of-two storage → copy bytes into or out of available circular-buffer space → update masked indices and report occupancy.
- **Backups:** Source directory → temporary partial tarball → timestamped `.tar.gz` archive → newest-first retention cleanup.
- **Sales report:** CSV files → validation and currency filtering → `Order` records → monthly regional aggregation → threshold filtering → tab-separated output.

#### Dependencies
- OpenCL built-ins and launch configuration for histogram indexing, synchronization, and atomic increments.
- `ringbuf.h` API and standard C allocation, memory-copy, and integer/size types for the ring buffer.
- Bash utilities including `tar`, `date`, `ls`, `du`, and `rm` for backup rotation.
- Python standard-library CSV, argument parsing, date, decimal, and collection modules for sales reporting.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; its usable capacity is one less than its configured capacity.
- Histogram kernels rely on configured global and local work sizes and provide no explicit status codes.
- Backup rotation uses strict Bash settings and an `EXIT` trap to remove temporary archives.
- Sales reporting uses `Decimal` amounts and includes file and line context for malformed rows.
