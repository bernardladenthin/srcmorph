### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:13:45Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> This package provides low-level byte-processing utilities and command-line workflows for histogram analysis, FIFO buffering, backup rotation, and regional sales reporting.

#### Purpose
- Compute byte-value distributions with parallel OpenCL kernels and move bytes through a bounded ring buffer.
- Automate compressed backup retention and produce monthly, regional revenue summaries from order CSVs.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count input byte values into 256 bins using work-group histograms and a reduction pass.
- **FIFO storage (`ringbuf/`):** Provide bounded, dynamically allocated byte storage with partial reads and writes, occupancy measurements, and cleanup.
- **Operations and reporting (`scripts/`):** Create timestamped compressed backups with retention limits; parse currency-filtered orders and report qualifying monthly regional totals.

#### Key units
- `histogram_partial` / `histogram_reduce` — build per-work-group byte counts and combine them into a final histogram; `BINS` defines the 256 byte-value bins.
- `ringbuf_t` — holds byte storage, capacity, and volatile `head`/`tail` indices; `ringbuf_status_t` defines operation status codes.
- `ringbuf_init`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_free` — initialize, transfer, measure, and release ring-buffer storage.
- `rotate_backups.sh` — creates UTC-dated `.tar.gz` archives and removes older matching archives beyond the configured retention.
- `Order`, `parse_orders(path, currency)`, `revenue_by_month_and_region(orders)`, `main(argv=None)` — parse orders, aggregate monthly regional revenue, and print threshold-qualified totals.

#### Data flow
- **Histogram:** Input bytes and length → per-work-group local histograms in `partial` → reduction across groups → final 256-bin histogram.
- **Ring buffer:** Initialization validates capacity and allocates storage; writes advance `head`, reads advance `tail`, and wraparound transfers use up to two copies.
- **Backup:** Source directory → temporary `.partial` archive → final timestamped archive → newest-first retention check and removal.
- **Sales report:** CSV inputs → header validation and currency filtering → `Order` records → monthly regional sums → threshold-filtered tab-separated output.

#### Dependencies
- `kernels/` uses OpenCL C 1.2 types, built-ins, barriers, atomic increments, and local-memory fences.
- `ringbuf.c` implements the API in `ringbuf.h`; uses standard `malloc`, `free`, and `memcpy`.
- Backup rotation uses Bash and external `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm` commands.
- Sales reporting uses Python standard-library CSV, argument parsing, date, decimal, and collection utilities.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one allocated slot is reserved to distinguish full from empty, and the API is designed for single-producer/single-consumer use.
- Histogram processing uses local-memory accumulation followed by a separate reduction pass.
- Backup scripts use strict Bash settings and an `EXIT` cleanup trap; sales parsing reports invalid headers, dates, or amounts with file context.
