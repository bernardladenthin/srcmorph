### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:39:35Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-data processing and stream utilities alongside operational tools for rotating backups and reporting filtered order revenue.

#### Purpose
- Covers parallel byte-frequency analysis, bounded FIFO byte buffering, compressed backup rotation, and monthly regional sales reporting.

#### Responsibilities
- **Byte histogramming:** Count input byte values into 256 bins using OpenCL work-group histograms and a final reduction.
- **Byte-stream buffering:** Manage a fixed-capacity SPSC ring buffer with partial reads and writes, wraparound handling, and occupancy reporting.
- **Operational scripts:** Archive directories with retention limits and aggregate CSV orders by month and region for a selected currency.

#### Key units
- `histogram_partial` / `histogram_reduce` — compute per-work-group byte counts and combine them into a 256-bin histogram.
- `ringbuf_t` / `ringbuf_status_t` — represent ring-buffer state and initialization outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read` — initialize, manage, measure, and transfer bytes through the ring buffer.
- `rotate_backups.sh` — create dated `.tar.gz` archives and prune older matching backups beyond `KEEP`.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse currency-filtered CSV orders, aggregate revenue, and produce CLI output.

#### Data flow
- Byte input → per-work-group histogram → final 256-bin counts.
- Ring-buffer writes copy available bytes into circular storage; reads return stored bytes, with both operations handling wraparound.
- Backup source directory → temporary archive → finalized dated archive → retention-based pruning.
- CSV inputs → validated, currency-filtered `Order` records → month/region totals → minimum-revenue filtering → tab-separated output.

#### Dependencies
- Histogram kernels use OpenCL C 1.2 primitives, memory qualifiers, barriers, and `atomic_inc`.
- Ring buffer uses C standard allocation and copy routines; its header exposes `size_t` and `uint8_t` types.
- Backup rotation uses Bash and external commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, CLI, date, decimal, dataclass, and aggregation facilities.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved to distinguish full from empty, and APIs report actual transferred bytes.
- Backup creation cleans up a partial archive on exit; sales reporting returns status `1` and writes `no orders found` to standard error when no matching orders exist.
