### other
- H: 1.0
- C: 7465CD55
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:14Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides low-level byte-processing utilities and operational tools for backups and regional sales reporting.

#### Purpose
- Supplies parallel byte-frequency analysis and bounded FIFO byte buffering.
- Includes command-line utilities for rotating compressed backups and summarizing currency-filtered sales.

#### Responsibilities
- **Byte processing:** Count byte values into 256-bin histograms using OpenCL work-groups, then reduce partial counts.
- **Stream buffering:** Initialize, read, write, and query a fixed-capacity circular byte buffer.
- **Operations and reporting:** Create and prune timestamped `.tar.gz` backups; parse order CSVs and report monthly regional revenue.

#### Key units
- `histogram_partial` / `histogram_reduce` — compute per-work-group byte counts and combine them into a final histogram.
- `ringbuf_t`, `ringbuf_init`, `ringbuf_write`, `ringbuf_read` — represent and operate on a bounded FIFO byte buffer.
- `rotate_backups.sh` — creates timestamped archives and enforces an archive-retention limit.
- `Order`, `parse_orders`, `revenue_by_month_and_region`, `main` — parse matching orders, aggregate revenue, and print filtered reports.

#### Data flow
- **Histogram:** Input bytes and length → per-work-group partial histograms → reduction into final bin counts.
- **Ring buffer:** Initialize allocated storage → write or read bytes across wraparound → query occupancy and free space.
- **Backup:** Source directory and settings → temporary archive → timestamped archive → prune older matching archives.
- **Sales:** CSV files → validated currency-matched orders → monthly regional totals → optional minimum-revenue filter → tab-separated output.

#### Dependencies
- OpenCL built-ins and memory qualifiers for histogram computation.
- `ringbuf.h`, C standard allocation and copying libraries for the byte buffer.
- Bash and system commands including `tar`, `date`, `mv`, `du`, `ls`, and `rm` for backup rotation.
- Python standard-library CSV, argument parsing, date, decimal, dataclass, and aggregation utilities for sales reporting.

#### Cross-cutting
- Command-line utilities accept inputs and report results through standard output; the sales tool also reports missing matches to standard error.
- Ring-buffer capacities must be nonzero powers of two; one slot is reserved, and the documented usage is single-producer/single-consumer without specified atomic operations.
- Backup execution uses strict Bash settings and an exit trap to remove partial archives; sales input errors identify the file and line.
