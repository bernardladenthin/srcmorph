### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:53Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides high-performance parallel histogram computation, lock-free streaming buffers, and operational utilities for backup rotation and financial reporting.

#### Purpose
- Computes byte frequency distributions using parallel OpenCL reduction algorithms.
- Manages circular data streams with O(1) operations and executes automated backup/financial aggregation tasks.

#### Responsibilities
- **Parallel Processing**: Executes two-pass OpenCL kernels to aggregate local histograms into global 256-bin frequency arrays.
- **Streaming Management**: Handles single-producer/single-consumer enqueue/dequeue operations with wrap-around logic and power-of-two sizing.
- **Operational Automation**: Enforces backup retention policies via atomic archive creation and aggregates CSV order data by region and month.

#### Key units
- `histogram_partial`: Parallel kernel computing per-group partial byte counts using atomic increments in local memory.
- `histogram_reduce`: Kernel aggregating partial results from the `partial` buffer into the final `histogram` array.
- `ringbuf_t`: Struct managing circular buffer state via `head`, `tail`, and `capacity` indices.
- `ringbuf_write`: Producer function copying data with bitwise masking for index wrapping.
- `ringbuf_read`: Consumer function extracting data while handling split transfers across buffer boundaries.
- `rotate_backups.sh`: Bash utility creating timestamped `.tar.gz` archives and enforcing retention limits.
- `sales_report.py`: Python CLI parsing CSV orders to calculate revenue metrics.
- `revenue_by_month_and_region`: Aggregation function returning sorted totals by `(month, region)` keys.

#### Data flow
1. **Histogram Pipeline**: Input byte stream -> `histogram_partial` (local counting) -> `partial` buffer -> `histogram_reduce` (global summation) -> `histogram` output.
2. **Buffer Pipeline**: Validated capacity -> `malloc` allocation -> `ringbuf_write` updates `head` -> `ringbuf_read` updates `tail` -> status codes or byte counts returned.
3. **Backup Pipeline**: Input validation -> temporary `.partial` archive creation -> atomic move to timestamped name -> list existing files -> delete oldest excess.
4. **Report Pipeline**: CSV ingestion -> `parse_orders` generator -> currency filtering -> `Decimal` conversion -> aggregation dictionary -> sorted tab-separated output.

#### Dependencies
- **External Libraries**: OpenCL C 1.2 (atomic ops, barriers), Standard C (`stdlib.h`, `string.h`), Python (`argparse`, `csv`, `decimal`).
- **System Tools**: Bash utilities (`tar`, `date`, `mv`, `rm`) and Python modules (`collections`, `dataclasses`).
- **Internal Collaboration**: `histogram_partial` feeds directly into `histogram_reduce`; `ringbuf.c` relies on definitions in `ringbuf.h`.

#### Cross-cutting
- **Concurrency**: OpenCL uses `barrier(CLK_LOCAL_MEM_FENCE)` and `atomic_inc`; Ring buffer assumes single-producer/single-consumer without locks.
- **Performance Optimization**: Ring buffer uses bitwise AND (`&`) for modulo arithmetic; Histograms utilize parallel work-groups.
- **Error Handling**: OpenCL relies on runtime errors; Ring buffer returns specific `status_t` codes; Scripts enforce strict input validation and atomicity.
- **Data Integrity**: Backup scripts use atomic moves; Report script validates date/amount formats before aggregation.
