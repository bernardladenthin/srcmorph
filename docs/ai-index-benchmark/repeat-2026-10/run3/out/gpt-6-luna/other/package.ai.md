### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:24:04Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-processing and streaming utilities alongside operational backup rotation and CSV-based regional revenue reporting.

#### Purpose
- Count byte values in parallel and move streamed bytes through a bounded FIFO buffer.
- Support directory backup retention and monthly regional order-revenue reports.

#### Responsibilities
- **Byte analysis (`kernels/`):** Build per-work-group byte histograms and reduce them into a 256-bin result.
- **Streaming (`ringbuf/`):** Manage a fixed-capacity byte queue with initialization, transfer, occupancy, and cleanup operations.
- **Operations and reporting (`scripts/`):** Create and rotate compressed directory backups; parse order CSVs and report currency-filtered monthly revenue by region.

#### Key units
- `histogram_partial` / `histogram_reduce` — count input bytes into work-group histograms and combine partial counts.
- `ringbuf_t` / `ringbuf_status_t` — represent circular-buffer state and operation outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read` — manage buffer lifecycle, capacity, and byte transfers.
- `rotate_backups.sh` — create UTC-timestamped `.tar.gz` backups and prune older archives to the retention limit.
- `Order` / `REQUIRED_COLUMNS` — represent parsed orders and define required CSV fields.
- `parse_orders`, `revenue_by_month_and_region`, `main` — parse matching-currency orders, aggregate totals, and run the sales-report CLI.

#### Data flow
- **Histogram:** Input bytes → per-work-group local counts → partial-histogram buffer → reduced 256-bin histogram.
- **Ring buffer:** Initialize storage → write available bytes and advance `head` → read queued bytes and advance `tail` → query occupancy or free storage.
- **Backups:** Validate source and retention → create `.partial` archive → finalize timestamped archive → remove older matching backups.
- **Sales report:** CSV files → currency-filtered `Order` records → totals grouped by month and region → minimum-revenue filter → tab-separated output.

#### Dependencies
- `kernels/` uses OpenCL C 1.2 types, built-ins, atomics, barriers, and work-group facilities.
- `ringbuf/` uses `ringbuf.h`, C standard allocation and memory-copy functions, and `stddef.h`/`stdint.h` types.
- `scripts/` uses Bash utilities and external archive/filesystem commands; the sales reporter uses Python standard-library CSV, argument parsing, dates, decimals, and aggregation utilities.

#### Cross-cutting
- The components are independent utilities; no shared package-level data flow or common interface is described.
- `ringbuf` requires a nonzero power-of-two capacity, reserves one slot to distinguish full from empty, and is intended for single-producer/single-consumer use.
- Backup rotation uses strict Bash error handling and cleans up incomplete `.partial` archives; sales parsing reports invalid matching-row data with file and line details.
