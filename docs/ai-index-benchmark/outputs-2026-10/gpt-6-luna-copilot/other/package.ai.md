### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:37:30Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-processing primitives and operational utilities for backup rotation and regional sales reporting.

#### Purpose
- Compute byte histograms and move bytes through bounded FIFO storage.
- Support directory backup retention and monthly regional revenue reporting.

#### Responsibilities
- **Byte processing:** Build and reduce 256-bin histograms using OpenCL kernels.
- **Byte transfer:** Manage a bounded circular buffer with FIFO reads and writes.
- **Operations and reporting:** Create dated compressed backups, prune old archives, and aggregate currency-filtered order revenue by month and region.

#### Key units
- `histogram_partial` / `histogram_reduce` — compute per-work-group byte counts and combine them into a final histogram.
- `ringbuf_t`, `ringbuf_status_t` — represent circular-buffer storage and initialization status.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — manage buffer lifecycle, transfers, and occupancy.
- `rotate_backups.sh` — create compressed directory archives and enforce retention.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse matching-currency orders, aggregate totals, and produce a filtered report.

#### Data flow
- **Histograms:** Input bytes → per-work-group partial counts → final 256-bin histogram.
- **Ring buffer:** Initialize storage → write bytes into available slots → read bytes in FIFO order.
- **Backups:** Source directory → temporary archive → finalized archive → retention pruning.
- **Reports:** CSV files → currency-filtered orders → monthly regional totals → minimum-revenue filter → tab-separated output.

#### Dependencies
- OpenCL built-ins for work-item and group indexing, barriers, and atomic increments.
- Ring buffer API in `ringbuf.h` and standard C headers.
- Backup utilities including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm`; sales reporting uses Python standard-library modules for CLI parsing, CSV, dates, decimals, and aggregation.

#### Cross-cutting
- Ring buffer uses power-of-two capacity, reserves one slot, and supports partial reads and writes; designed for single-producer/single-consumer use.
- Backup and report utilities validate inputs and signal failures; backup retention defaults to 7 archives, while reporting defaults to `EUR` and minimum revenue `0`.
