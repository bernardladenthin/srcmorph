### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:47Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-stream processing and buffering alongside operational utilities for backup rotation and regional sales reporting.

#### Purpose
- Groups utilities for byte-frequency histogram computation, circular byte buffering, compressed backups, and CSV-based sales analysis.
- Supports both low-level data handling and command-line operational reporting.

#### Responsibilities
- **Byte analysis (`kernels/`):** Count input byte values into 256-bin histograms using OpenCL partial counts and reduction.
- **Byte transport (`ringbuf/`):** Manage a fixed-capacity, single-producer/single-consumer ring buffer with wraparound reads and writes.
- **Operations and reporting (`scripts/`):** Rotate timestamped `.tar.gz` backups and aggregate currency-filtered order data into monthly regional revenue totals.

#### Key units
- `histogram_partial` / `histogram_reduce` — compute per-work-group byte counts and combine them into a final 256-bin histogram.
- `ringbuf_t` — stores ring-buffer bytes, capacity, and read/write indices; `ringbuf_init`, `ringbuf_read`, `ringbuf_write`, and occupancy queries manage its lifecycle and transfers.
- `rotate_backups.sh` — creates timestamped compressed archives and enforces a configurable retention limit.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — represent, parse, aggregate, filter, and report sales orders.

#### Data flow
- **Histogram:** Input bytes and length → per-work-group partial histograms → reduced 256-bin output.
- **Ring buffer:** Initialize storage → write available bytes and read stored bytes across wraparound → query occupancy or release storage.
- **Backup:** Source directory → temporary archive → finalized timestamped `.tar.gz` → retention cleanup.
- **Sales:** CSV files → validated orders in the selected currency → monthly regional totals → minimum-revenue filtering → tab-separated output.

#### Dependencies
- OpenCL work-item/work-group primitives and local/global memory for histogram kernels.
- C standard allocation and copy facilities for the ring buffer.
- Bash and system commands including `tar`, `date`, `mkdir`, `mv`, `ls`, and `rm` for backup rotation.
- Python standard-library CSV, argument parsing, date, decimal, and aggregation utilities for sales reporting.

#### Cross-cutting
- Ring-buffer capacity must be a nonzero power of two; one slot is reserved to distinguish full from empty.
- Backup execution treats command failures as fatal and removes partial archives on exit.
- Sales calculations use `Decimal`; malformed columns, dates, or amounts yield file-specific errors.
