### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:44Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Supports byte-stream processing and operational data tasks: parallel byte-frequency counting, bounded FIFO buffering, backup rotation, and monthly order-revenue reporting.

#### Purpose
- Provides utilities for processing byte data and managing buffered streams.
- Automates directory backup retention and currency-filtered sales reporting.

#### Responsibilities
- **Byte histograms (`kernels/`):** Count occurrences of each byte value using per-work-group histograms followed by reduction.
- **FIFO buffering (`ringbuf/`):** Manage bounded circular-buffer storage, byte transfers, and occupancy.
- **Operations and reporting (`scripts/`):** Rotate compressed directory backups and aggregate qualifying CSV order revenue by month and region.

#### Key units
- `BINS`, `histogram_partial`, `histogram_reduce` — define and compute the 256-bin byte histogram.
- `ringbuf_t`, `ringbuf_status_t` — represent circular-buffer state and initialization outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — manage buffer lifecycle, transfers, and capacity queries.
- `rotate_backups.sh` — creates UTC-dated compressed archives and enforces retention.
- `Order`, `parse_orders(path, currency)`, `revenue_by_month_and_region(orders)`, `main(argv=None)` — parse currency-matched orders, aggregate monthly regional revenue, and produce filtered reports.

#### Data flow
- **Histogram:** Input bytes and length feed `histogram_partial`, which writes work-group counts to `partial`; `histogram_reduce` combines them into the final 256-bin histogram.
- **Ring buffer:** Initialization validates capacity and allocates storage; writes and reads copy available bytes while advancing wrapping `head` and `tail` indices; queries derive used and free space from those indices.
- **Backups:** A source directory is archived to a temporary compressed file, moved to a timestamped archive, then older matching archives are removed according to retention settings.
- **Revenue report:** CSV inputs are validated and filtered by currency, converted to `Order` records, aggregated by month and region, threshold-filtered, and printed as tab-separated output.

#### Dependencies
- **Kernels:** OpenCL C 1.2 built-ins and caller-provided local-memory buffer.
- **Ring buffer:** `ringbuf.c` implements `ringbuf.h`; uses standard allocation, copy, and byte/integer types.
- **Scripts:** Bash and external archive, filesystem, and date commands; Python standard-library CSV, argument parsing, date, decimal, and aggregation utilities.

#### Cross-cutting
- Histogram work-items cooperate through local memory and barriers; reduction assigns work across bins.
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved, and `volatile` producer/consumer-owned indices track progress.
- Backup rotation uses strict Bash mode and an exit trap to remove partial archives.
- Reporting uses `Decimal` and includes file context in invalid-header, date, or amount errors.
