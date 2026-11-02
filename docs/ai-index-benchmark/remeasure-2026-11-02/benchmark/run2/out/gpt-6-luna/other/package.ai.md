### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:46:20Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-stream processing and buffering alongside command-line utilities for backup rotation and regional sales reporting.

#### Purpose
- Groups an OpenCL byte-histogram kernel, a bounded C FIFO byte buffer, and standalone backup and sales-reporting utilities.
- Supports byte-frequency analysis, partial byte transfers, compressed backup retention, and currency-filtered monthly revenue summaries.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count byte values into 256-bin histograms using per-work-group partial counts and a reduction.
- **Byte buffering (`ringbuf/`):** Allocate and manage a fixed-capacity FIFO ring buffer with wraparound and partial reads or writes.
- **Operations and reporting (`scripts/`):** Create and rotate compressed directory backups; parse order CSVs and report monthly regional revenue for a selected currency.

#### Key units
- `histogram_partial` / `histogram_reduce` — compute per-work-group byte counts and combine them into a final histogram.
- `BINS` — defines the 256 histogram bins.
- `ringbuf_t` / `ringbuf_status_t` — represent ring-buffer state and initialization outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read` — initialize, release, enqueue, and dequeue ring-buffer data.
- `ringbuf_used` / `ringbuf_free_space` — report buffered and writable byte counts.
- `rotate_backups.sh` — create timestamped gzip tar archives and enforce a retention limit.
- `Order` — frozen sales record containing date, region, and decimal amount.
- `parse_orders` / `revenue_by_month_and_region` — parse currency-matched orders and aggregate revenue by month and region.
- `main` — parse sales-report options, filter totals, and print results.

#### Data flow
- **Histogram:** Input byte buffer and length → work-group partial histograms → per-bin reduction → final histogram.
- **Ring buffer:** Initialize allocated storage → write and read bytes in FIFO order with wraparound → query occupancy or release storage.
- **Backups:** Source directory → temporary `.tar.gz.partial` archive → renamed archive → retention scan → removal of excess archives.
- **Sales report:** CSV files → validated, currency-matched `Order` records → monthly and regional totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- **Internal:** `ringbuf.c` implements the API declared in `ringbuf.h`; the package indexes `kernels/`, `ringbuf/`, and `scripts/` as separate functional areas.
- **External:** OpenCL built-ins and memory spaces; C standard-library allocation and copying; Bash and system utilities (`tar`, `date`, `mkdir`, `mv`, `du`, `ls`, `rm`); Python standard-library CSV, argument parsing, dates, decimals, and collections.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved, and the API is intended for single-producer/single-consumer use.
- Histogram kernels synchronize local-memory initialization and counting with barriers and use atomic increments.
- Backup execution uses strict Bash settings and an `EXIT` trap to remove incomplete archives.
- Sales parsing reports malformed matching rows with file and line context; missing columns raise `ValueError`.
