### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:15Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-data processing utilities, bounded stream buffering, backup rotation, and currency-based monthly revenue reporting.

#### Purpose
- Groups low-level byte-processing components with operational backup and sales-reporting tools.
- Supports byte histogram computation, single-producer/single-consumer transfer, backup retention, and order-revenue aggregation.

#### Responsibilities
- **Byte processing:** Compute 256-bin frequency counts from image or byte-stream input using partial work-group histograms and reduction.
- **Stream buffering:** Manage a fixed-capacity circular byte queue with partial reads and writes, occupancy queries, and lifecycle operations.
- **Operations and reporting:** Create timestamped compressed backups with retention pruning; parse order CSVs and report monthly regional revenue for a selected currency.

#### Key units
- `histogram_partial`, `histogram_reduce`, `BINS` — count byte values per work group and reduce partial counts into a 256-bin histogram.
- `ringbuf_t`, `ringbuf_status_t` — represent circular buffer state and initialization outcomes.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, `ringbuf_read` — initialize, inspect, transfer data through, and release the ring buffer.
- `rotate_backups.sh` — create a compressed archive and prune older matching backups.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — represent and parse currency-matched orders, aggregate revenue, and print report results.

#### Data flow
- **Histogram:** Input bytes → work-group-local counts → partial histograms → reduced 256-bin totals.
- **Ring buffer:** Initialize power-of-two storage → write available bytes and read queued bytes with wraparound → query occupancy or release storage.
- **Backup:** Validate source and retention settings → create UTC-dated `.tar.gz` via a `.partial` path → finalize archive and prune older matches.
- **Sales report:** CSV paths and currency → validated matching orders → monthly regional totals → optional minimum-revenue filter → tab-separated output.

#### Dependencies
- Histogram kernels use OpenCL C built-ins for work-item/group indexing, barriers, and local-memory atomic increments.
- Ring buffer API is declared by `ringbuf.h`; implementation uses standard C allocation, copying, and integer types.
- Backup rotation uses shell utilities including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, date, decimal, argument-parsing, and aggregation modules.

#### Cross-cutting
- Ring buffer reserves one slot to distinguish full from empty, requires nonzero power-of-two capacity, and specifies single-producer/single-consumer ownership of `head` and `tail`.
- Backup rotation uses strict Bash mode and an `EXIT` trap to remove temporary archives on failure.
- Sales amounts use `Decimal`; invalid matching-row dates or amounts produce file- and line-specific `ValueError`s.
- The components use their respective platform conventions: OpenCL synchronization, C buffer status codes, and command-line failure handling.
