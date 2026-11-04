### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:39:02Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-stream and image-data processing alongside standalone utilities for backup retention and regional sales reporting.

#### Purpose
- Supplies a 256-bin byte histogram, a bounded FIFO byte ring buffer, and command-line tools for compressed backups and monthly sales reports.
- The components are independent; no shared data flow is described.

#### Responsibilities
- **Byte processing:** OpenCL kernels count byte values and reduce work-group histograms; C ring-buffer routines manage storage, occupancy, and FIFO transfers.
- **Operational scripts:** A Bash utility creates dated compressed archives and prunes older backups.
- **Sales reporting:** A Python CLI validates and filters order CSV data, aggregates revenue by month and region, and prints qualifying totals.

#### Key units
- `histogram_partial` — counts byte values into per-work-group 256-bin histograms.
- `histogram_reduce` — combines partial histograms into the final `histogram`.
- `BINS` — sets the histogram size to 256 byte-value bins.
- `ringbuf_t` — holds byte storage, capacity, and `head`/`tail` indices.
- `ringbuf_status_t` — represents ring-buffer initialization success or capacity/allocation errors.
- `ringbuf_init`, `ringbuf_free` — initialize and release/reset ring-buffer storage.
- `ringbuf_write`, `ringbuf_read` — transfer bytes in FIFO order, including across wraparound.
- `ringbuf_used`, `ringbuf_free_space` — report occupied and writable space.
- `rotate_backups.sh` — creates timestamped `.tar.gz` archives and retains a configured number, defaulting to seven.
- `Order` — frozen record of an order’s date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV columns and yields orders matching the selected currency.
- `revenue_by_month_and_region(orders)` — returns monthly regional revenue totals sorted by key.
- `main(argv=None)` — runs the sales-report CLI and prints totals meeting its minimum-revenue threshold.

#### Data flow
- **Histogram:** input bytes and length → work-group-local counts → per-group `partial` segments → final 256-bin `histogram`.
- **Ring buffer:** initialization validates capacity and allocates storage; writes fill available space, reads retrieve stored bytes, and occupancy queries expose used/free capacity.
- **Backups:** source directory → temporary compressed archive → dated archive in destination → prune older matching archives.
- **Sales:** CSV files → required-column validation and currency filtering → month/region aggregation → minimum-revenue filtering → tab-separated output.

#### Dependencies
- OpenCL work-item/group indexing, synchronization, and atomic operations support histogram kernels.
- `ringbuf.c` implements the API declared in `ringbuf.h`; it uses `malloc`, `free`, `memcpy`, and `uint8_t`.
- `rotate_backups.sh` uses Bash and external commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- `sales_report.py` uses Python standard-library CSV, argument-parsing, date, decimal, record, and aggregation facilities.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved to distinguish full from empty, and wraparound uses a capacity mask. The API describes single-producer/single-consumer use; synchronization is unspecified.
- Histogram construction uses local-memory initialization, work-group barriers, and atomic increments; reduction skips global IDs outside the 256 bins.
- Backup failures use strict Bash execution settings; invalid retention counts and missing source directories exit with status `2`.
- Sales parsing raises `ValueError` for missing columns or invalid dates/amounts; no matching orders writes to stderr and exits with status `1`.
