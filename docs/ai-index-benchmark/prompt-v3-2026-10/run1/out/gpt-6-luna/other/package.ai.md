### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:40Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Supports byte-data analysis and transfer alongside operational backup rotation and currency-filtered regional sales reporting.

#### Purpose
- Provides parallel byte-frequency histogram computation and bounded FIFO byte-stream storage.
- Includes utilities for timestamped backup retention and monthly regional revenue reporting from order CSVs.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count input bytes into 256-bin histograms using per-work-group partial counts and a final reduction.
- **Byte transfer (`ringbuf/`):** Allocate, manage, and read or write a fixed-capacity circular buffer with partial transfers and wraparound.
- **Operations and reporting (`scripts/`):** Create compressed, retention-limited directory backups; parse currency-matched orders and report monthly revenue by region.

#### Key units
- `histogram_partial` and `histogram_reduce` — OpenCL kernels for partial byte counting and histogram aggregation; `BINS` defines the 256 bins.
- `ringbuf_t` and `ringbuf_status_t` — circular-buffer state and initialization result codes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — buffer lifecycle, transfer, and occupancy operations.
- `rotate_backups.sh` — creates timestamped compressed archives and enforces a configurable retention limit, defaulting to seven.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — represent, filter, aggregate, and report CSV order data.

#### Data flow
- **Histogram:** Input bytes and length → work-group-local 256-bin counts → partial-results buffer → reduction into final histogram.
- **Ring buffer:** Writes copy source bytes into available space; reads copy buffered bytes to a destination, splitting copies at the array boundary when needed.
- **Backups:** Source and destination arguments → temporary `.partial` tarball → final `.tar.gz` archive → newest-first retention cleanup.
- **Sales reporting:** CSV files → required-column validation and currency filtering → `Order` records → month-and-region revenue totals → threshold filtering → tab-separated output.

#### Dependencies
- OpenCL work-group primitives, barriers, local-memory atomics, and OpenCL types for histogram computation.
- `ringbuf.c` and `ringbuf.h`, plus C standard allocation and memory-copy facilities; the header supports C++ callers.
- Bash and system utilities including `tar`, `date`, `mkdir`, `mv`, `du`, and `rm` for backup rotation.
- Python standard-library facilities for CSV parsing, CLI arguments, dates, decimal arithmetic, and aggregation in sales reporting.

#### Cross-cutting
- The ring buffer reserves one slot to distinguish full from empty, requires a nonzero power-of-two capacity, and is designed for single-producer/single-consumer ownership.
- Histogram counting uses local-memory work-group histograms, synchronization barriers, and atomic increments.
- Backup timestamps use UTC, and failed execution cleans up the temporary archive.
- Sales amounts use `Decimal`; invalid dates or amounts produce file- and line-specific `ValueError`s, while no matching orders produce a stderr message and exit status `1`.
