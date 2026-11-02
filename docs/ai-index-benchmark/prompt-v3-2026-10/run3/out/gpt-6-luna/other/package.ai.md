### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:37Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-stream and image-data processing utilities alongside backup rotation and monthly regional sales reporting.

#### Purpose
- Bundle independent utilities for parallel byte histograms, bounded FIFO buffering, directory backup retention, and currency-filtered sales aggregation.

#### Responsibilities
- **Byte analysis:** Compute 256-bin byte-value histograms using per-work-group counts and a reduction step.
- **Byte transport:** Buffer bytes in bounded storage with FIFO reads and writes, partial transfers, and wraparound handling.
- **Operations and reporting:** Create timestamped compressed directory backups with retention cleanup; parse order CSVs and report monthly regional revenue.

#### Key units
- `histogram_partial` / `histogram_reduce` — count byte values per work group and combine partial counts into a 256-bin histogram.
- `ringbuf_t` — holds ring-buffer storage, capacity, and producer/consumer indices.
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_free` — manage buffer lifecycle and FIFO byte transfers.
- `rotate_backups.sh` — archives a source directory and retains the newest matching backups.
- `Order` — immutable sales record containing order date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV data and yields records for the selected currency.
- `revenue_by_month_and_region(orders)` — calculates sorted monthly and regional revenue totals.
- `main(argv=None)` — aggregates sales inputs, filters totals by minimum revenue, and prints a tab-separated report.

#### Data flow
- **Histogram:** Input bytes and length → per-work-group partial histograms → reduction → final 256-bin distribution.
- **Ring buffer:** Initialize validated storage → write available bytes and read occupied bytes in FIFO order → free and reset storage.
- **Backups:** Source and destination → temporary `.partial` archive → timestamped `.tar.gz` → retention cleanup.
- **Sales:** CSV files → validated, currency-filtered `Order` records → month/region totals → threshold filtering → tab-separated output.

#### Dependencies
- Histogram kernels use OpenCL C 1.2 work-item/work-group built-ins, local-memory barriers, and `atomic_inc`.
- Ring buffer uses its C API (`ringbuf.h` / `ringbuf.c`), standard allocation and copy functions, and `size_t` / `uint8_t`.
- Backup rotation uses Bash and system commands including `tar`, `date`, `mv`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, date, decimal, and aggregation facilities.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved to distinguish full from empty, and synchronization is not specified.
- Histogram counting uses local-memory initialization, atomic increments, and work-group barriers; no explicit status codes are described.
- Backup failures terminate under `set -euo pipefail`, with an exit trap for removing partial archives.
- Sales amounts use `Decimal`; malformed headers, dates, and amounts raise file-contextual `ValueError`s.
