### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:44Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-data processing and buffering alongside operational backup rotation and regional sales reporting utilities.

#### Purpose
- Counts byte-value frequencies in parallel and supports bounded FIFO byte transfer.
- Includes standalone tools for compressed directory backups and monthly regional revenue reports from order CSVs.

#### Responsibilities
- **Byte processing:** OpenCL kernels compute 256-bin histograms using per-work-group counts and reduction.
- **Byte buffering:** C ring-buffer API handles allocation, reads, writes, and occupancy for a single-producer/single-consumer arrangement.
- **Operations and reporting:** Bash backup rotation creates timestamped archives and enforces retention; Python reporting validates CSV orders and aggregates currency-matched revenue by month and region.

#### Key units
- `histogram_partial`, `histogram_reduce`, `BINS` — count byte values into partial histograms and combine them into final bins.
- `ringbuf_t`, `ringbuf_status_t` — represent ring-buffer storage and operation status.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space` — manage buffer lifecycle, transfers, and occupancy.
- `rotate_backups.sh` — create UTC-dated compressed archives and remove older matching backups beyond the retention limit.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — validate and filter CSV orders, aggregate revenue, and print qualifying report rows.

#### Data flow
- **Histogram:** Input bytes → work-group-local 256-bin counts → partial histograms → reduced final histogram.
- **Ring buffer:** Initialization allocates storage; writes and reads transfer available bytes across wraparound; occupancy queries report used and free capacity.
- **Backups:** Source directory → temporary `.partial` archive → timestamped `.tar.gz` archive → retention cleanup.
- **Revenue report:** CSV files → validated, currency-matched `Order` records → monthly regional totals → minimum-revenue filtering and formatted output.

#### Dependencies
- Histogram kernels use OpenCL work-item and work-group facilities, atomic increments, barriers, and `uchar`/`uint` types.
- Ring buffer implementation uses standard C allocation, copy, byte, and size facilities.
- Backup script uses Bash and system commands including `tar`, `date`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, date, and decimal support.

#### Cross-cutting
- Ring-buffer capacities must be nonzero powers of two; one slot is reserved to distinguish full from empty, and transfers may be partial.
- Ring-buffer metadata uses volatile producer-owned `head` and consumer-owned `tail`; no other concurrency guarantees are specified.
- Backup script uses strict shell mode and removes unfinished `.partial` archives via an exit trap.
- Sales reporting uses `Decimal`; invalid required headers, dates, or amounts raise `ValueError`.
