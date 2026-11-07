### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:00:05Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-stream processing and storage alongside command-line utilities for backup retention and regional sales reporting.

#### Purpose
- Compute byte-frequency histograms and manage bounded FIFO byte buffers.
- Support directory backup rotation and monthly, regional order-revenue reporting.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count byte values in parallel with work-group histograms, then reduce partial counts.
- **Byte transfer (`ringbuf/`):** Allocate and manage power-of-two-capacity FIFO storage with wrapped reads and writes.
- **Operational and sales utilities (`scripts/`):** Create retained compressed backups; parse currency-filtered order CSVs and report monthly regional revenue.

#### Key units
- `histogram_partial`, `histogram_reduce`, `BINS` — build per-work-group byte counts and combine them into 256-bin totals.
- `ringbuf_t`, `ringbuf_status_t` — represent ring-buffer state and initialization outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read` — manage buffer lifecycle and transfer bytes.
- `ringbuf_used`, `ringbuf_free_space` — report buffer occupancy and writable capacity.
- `rotate_backups.sh` — create timestamped compressed archives and prune older matching backups.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — validate and filter order data, aggregate revenue, and run the report CLI.

#### Data flow
- **Histogram:** Input bytes and length → work-group partial histograms → per-bin reduction → final histogram.
- **Ring buffer:** Initialize storage → write available bytes and read stored bytes with boundary wrapping → query used and free space.
- **Backups:** Source directory → timestamped `.tar.gz` archive → newest-first archive listing → prune beyond the retention count.
- **Sales:** CSV files → validated, currency-filtered `Order` records → monthly regional totals → threshold-filtered tab-separated output.

#### Dependencies
- `kernels/` relies on OpenCL C 1.2 work-item/work-group operations, local memory, barriers, and atomic increments.
- `ringbuf/` uses its `ringbuf.h` types and status codes, standard allocation and copy functions, and `<stddef.h>` / `<stdint.h>`.
- `scripts/rotate_backups.sh` uses Bash and system utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `scripts/sales_report.py` uses Python standard-library CLI, CSV, date, decimal, and aggregation facilities.

#### Cross-cutting
- Histogram computation uses a two-pass parallel pattern with local-memory atomic updates; no explicit error handling is summarized.
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved to distinguish full from empty, and the API describes single-producer/single-consumer use.
- Backup execution enables strict Bash error handling and removes partial archives via an exit trap.
- Sales CSV parsing raises `ValueError` for missing columns or invalid dates and amounts.
