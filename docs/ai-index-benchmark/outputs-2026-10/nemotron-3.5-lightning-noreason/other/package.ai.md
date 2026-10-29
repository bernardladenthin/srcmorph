### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:57Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Calculates histograms, buffers byte-streams, and automates backups/reporting for data pipelines.

#### Purpose
Package provides parallel OpenCL histogram computation, a lock-free ring buffer for byte-stream throughput, and scripts for backup rotation and sales aggregation from CSV data.

#### Responsibilities
- **Parallel histogram computation**: Two-pass OpenCL kernel design (`histogram_partial`, `histogram_reduce`) for 256-bin byte histogramming across image and stream inputs.
- **Lock-free byte-stream buffering**: Fixed-capacity power-of-two ring buffer (`ringbuf_t`) with volatile head/tail indices for single-producer/single-consumer throughput.
- **Data preservation and financial reporting**: Backup rotation (`rotate_backups.sh`) and CSV-based sales aggregation (`sales_report.py`) with retention and currency filtering.

#### Key units
- `histogram_partial`: Pass 1 OpenCL kernel accumulating per-work-group partial histograms into local memory and global partial buffer via `atomic_inc`.
- `histogram_reduce`: Pass 2 OpenCL kernel reducing all partial histograms into the final 256-bin output buffer.
- `ringbuf_t`: Central struct with data buffer, volatile head/tail indices, and capacity for lock-free single-producer/single-consumer access.
- `ringbuf_write`: Writes bytes into buffer with wrap-around via power-of-two masking; returns actual bytes copied.
- `ringbuf_read`: Reads bytes from buffer with wrap-around via power-of-two masking; returns actual bytes copied.
- `rotate_backups.sh`: Script automating timestamped archive creation, retention iteration, and storage pruning.
- `sales_report.py`: Aggregates monthly revenue from CSV exports with currency and threshold filtering.

#### Data flow
- Histogram: Raw byte input → `histogram_partial` (Pass 1, local + partial buffer accumulation) → `histogram_reduce` (Pass 2, global reduction) → final 256-bin output buffer.
- Ring buffer: Producer calls `ringbuf_write` (copies data, advances volatile head via power-of-two mask) → Consumer calls `ringbuf_read` (copies data, advances volatile tail via power-of-two mask) → Queries `ringbuf_used`/`ringbuf_free_space` read head/tail state.
- Sales report: CSV ingestion → `parse_orders` generator → `revenue_by_month_and_region` aggregation (keyed by `(month, region)`) → filtered output by `--currency` and `--min-revenue` threshold.
- Backup: `source_dir` → timestamped `tar.gz` archive → `backup_dir` → sorted archive iteration → removal of excess beyond `keep` count.

#### Dependencies
- OpenCL C 1.2 runtime with `atomic_inc` and `barrier(CLK_LOCAL_MEM_FENCE)` for histogram kernels.
- Power-of-two capacity constraint and volatile indices for ring buffer; `<stdlib.h>`, `<string.h>` for memory and copy operations.
- Script dependencies: `bash`, `tar`, `mv`, `ls`, `rm`, `du`, `date`, `mapfile`; `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`.

#### Cross-cutting
- Two-pass design pattern: histogram uses parallel reduction; ring buffer uses wrap-around copy pattern for boundary crossing.
- Input validation and status-coded error handling: histogram guards against out-of-bounds reads; ring buffer returns `RINGBUF_EINVAL`/`RINGBUF_ENOMEM`; scripts exit with code 2 on invalid retention or missing data.
- Power-of-two sizing constraint: ring buffer reserves one slot to distinguish full/empty; histogram bin count is fixed at 256.
- Thread-safety via atomic operations (histogram local increments) and volatile indices (ring buffer producer/consumer).
