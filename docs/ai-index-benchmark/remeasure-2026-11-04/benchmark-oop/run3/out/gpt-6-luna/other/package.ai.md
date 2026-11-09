### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:47Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte processing and buffering utilities alongside command-line tools for backup retention and sales reporting.

#### Purpose
- Compute byte-frequency histograms and manage bounded circular byte streams.
- Automate compressed backup rotation and produce monthly regional revenue reports from order CSVs.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count byte values into 256-bin partial histograms and reduce them to final totals.
- **Byte-stream buffering (`ringbuf/`):** Allocate, query, read, and write fixed-capacity circular buffers with wraparound and partial transfers.
- **Operational scripts (`scripts/`):** Create timestamped compressed archives with retention pruning; parse currency-filtered orders and report qualifying monthly regional revenue.

#### Key units
- `histogram_partial` / `histogram_reduce` — OpenCL kernels for per-work-group byte counting and histogram reduction.
- `BINS` — Defines the 256 byte-value histogram bins.
- `ringbuf_t` / `ringbuf_status_t` — Ring-buffer state and initialization status codes.
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free` — Ring-buffer lifecycle, transfer, and capacity API.
- `rotate_backups.sh` — Creates UTC-timestamped `.tar.gz` archives and prunes older matching backups.
- `Order`, `parse_orders(path, currency)`, `revenue_by_month_and_region(orders)`, `main(argv=None)` — Sales data model, CSV filtering, aggregation, and report CLI.

#### Data flow
- **Histogram:** Input bytes and length → local work-group counters → partial histograms → reduced 256-bin output.
- **Ring buffer:** Initialize storage → write available bytes and read buffered bytes while advancing indices → query occupancy or free storage.
- **Backups:** Source directory and retention settings → compressed partial archive → timestamped archive → retention pruning.
- **Sales report:** CSV files → currency-matched `Order` records → monthly regional revenue totals → threshold filtering → tab-separated output.

#### Dependencies
- **Kernels:** OpenCL C 1.2 work-item, work-group, synchronization, and atomic built-ins.
- **Ring buffer:** `ringbuf.h`, C standard allocation and memory-copy facilities.
- **Backup rotation:** Bash utilities including `tar`, `date`, `mv`, `du`, `ls`, and `rm`.
- **Sales reporting:** Python standard library for CLI parsing, CSV input, dates, decimals, and aggregation.

#### Cross-cutting
- `ringbuf_t` uses power-of-two capacity, masked wraparound indices, and one reserved slot; its volatile indices are described for single-producer/single-consumer use.
- Histogram kernels coordinate shared local-memory counters with work-group barriers.
- Backup and sales utilities are separate command-line workflows; no shared data pipeline is specified.
