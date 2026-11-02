### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:54Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-processing utilities for GPU histogramming and producer–consumer buffering, alongside operational backup rotation and regional sales reporting.

#### Purpose
- Count byte frequencies on the GPU and move bytes through a bounded circular queue.
- Support directory backup retention and monthly revenue reporting from order CSV exports.

#### Responsibilities
- **Byte processing:** Compute 256-bin histograms using per-work-group counts and reduction; buffer byte streams with partial reads and writes.
- **Operations and reporting:** Create timestamped compressed backups with retention cleanup; validate order CSVs and aggregate currency-matched revenue by month and region.

#### Key units
- `histogram_partial` / `histogram_reduce` — build per-work-group byte histograms and combine them into final counts.
- `ringbuf_t` — circular byte queue state with capacity, producer `head`, and consumer `tail`.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read` — manage queue lifecycle, occupancy, and transfers.
- `ringbuf_status_t` — initialization outcomes: `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `rotate_backups.sh` — create timestamped `.tar.gz` archives and enforce a retention count.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — represent, parse, aggregate, and report sales data.

#### Data flow
- **GPU histogram:** Input bytes and length → per-work-group partial histograms → reduction across groups → final 256-bin counts.
- **Ring buffer:** Initialize capacity → write up to available space and read up to buffered data; occupancy queries report used and free bytes.
- **Backup:** Source directory and destination → temporary archive → timestamped archive → retention cleanup.
- **Sales report:** CSV files → validated matching-currency `Order` records → month/region totals → minimum-revenue filtering → tab-separated output.

#### Dependencies
- GPU kernels use OpenCL C 1.2 work-group facilities, local-memory atomics, and barriers.
- Ring buffer uses `ringbuf.h` and standard C allocation and memory-copy facilities.
- Backup script uses Bash and system commands including `tar`, `date`, `mv`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, date, decimal, and aggregation utilities.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved, and `volatile size_t` indices wrap with a capacity mask.
- GPU histogramming separates local partial counting from global reduction.
- Backup execution uses `set -euo pipefail` and a cleanup trap for temporary archives; sales amounts use `Decimal` and parse errors include file and line context.
