### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:03:03Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides byte-stream processing primitives and operational tools for backups and regional sales reporting.

#### Purpose
- Counts byte-value distributions and buffers byte data through a bounded FIFO.
- Supplies utilities for rotating dated backups and reporting monthly regional revenue from order exports.

#### Responsibilities
- **Byte processing:** Compute 256-bin histograms with OpenCL kernels and transfer bytes through a fixed-capacity circular buffer.
- **Operations and reporting:** Create compressed backups with retention cleanup; parse order CSVs, aggregate revenue by month and region, and filter results by currency and minimum revenue.

#### Key units
- `histogram_partial` — counts byte values per OpenCL work-group; `histogram_reduce` combines the partial counts into a final histogram.
- `BINS` — defines the 256 histogram bins.
- `ringbuf_t` — holds circular-buffer storage, capacity, and `head`/`tail` indices.
- `ringbuf_init`, `ringbuf_free` — initialize and release ring-buffer storage; `ringbuf_read`, `ringbuf_write` transfer bytes; `ringbuf_used`, `ringbuf_free_space` report occupancy.
- `ringbuf_status_t` — represents ring-buffer initialization outcomes, including invalid capacity and allocation failure.
- `rotate_backups.sh` — creates UTC-dated gzip archives and removes older matching archives beyond the retention limit.
- `Order` — frozen record of an order’s date, region, and decimal amount.
- `parse_orders(path, currency)` — validates CSV headers and yields orders in the selected currency.
- `revenue_by_month_and_region(orders)` — aggregates and sorts monthly regional totals; `main(argv=None)` runs the sales-report CLI.

#### Data flow
- **Histogram:** Input byte buffer and length → per-work-group partial counts → reduction across groups → final histogram.
- **Ring buffer:** Initialize validated power-of-two capacity → write available bytes or read buffered bytes with wraparound → release and reset storage.
- **Backups:** Validate source and retention settings → create temporary compressed archive → finalize timestamped archive → delete excess older archives.
- **Sales report:** CSV rows → validate required columns, currency, dates, and amounts → create `Order` records → aggregate by month and region → apply minimum-revenue filter → print tab-separated totals.

#### Dependencies
- Histogram kernels use OpenCL work-item and work-group operations, barriers, local-memory fences, and `atomic_inc`.
- Ring buffer uses `ringbuf.h`, standard `size_t` and `uint8_t` types, and C allocation and copy utilities.
- Backup script uses Bash and system commands including `tar`, `date`, `mkdir`, `mv`, `du`, `ls`, and `rm`.
- Sales reporting uses Python standard-library CSV, argument parsing, date, decimal, dataclass, and collection utilities; expected CSV columns are `order_date`, `region`, `amount`, and `currency`.

#### Cross-cutting
- Ring-buffer capacities must be powers of two; one slot is reserved to distinguish full from empty, and index calculations use capacity-based bitmasks.
- Histogram counting uses cooperative local-memory initialization, synchronization barriers, and atomic increments.
- Backup handling uses Bash strict mode and an `EXIT` trap to remove temporary output on exit.
- Sales amounts use `Decimal`; missing columns and invalid dates or amounts raise `ValueError`, with row context for parse errors.
