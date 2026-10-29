### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:42Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides a heterogeneous toolkit combining GPU-accelerated histogram computation, a lock-free ring buffer for byte streaming, and operational scripts for backup rotation and financial sales reporting.

#### Purpose
- Implements high-performance data processing primitives (OpenCL kernels, C ring buffer) alongside operational automation utilities (Bash/Python scripts).
- Serves distinct domains: real-time data aggregation, low-level memory management, and administrative data reporting.

#### Responsibilities
- **GPU Histogramming**: Computes 256-bin byte histograms using parallel partial counting and global reduction via OpenCL.
- **Circular Buffering**: Manages fixed-capacity, power-of-two byte storage with lock-free single-producer/single-consumer semantics.
- **Operational Automation**: Executes timestamped directory backup rotation with retention policies and generates monthly revenue reports from order CSVs.

#### Key units
- `histogram_partial` / `histogram_reduce`: OpenCL kernels for two-pass byte frequency aggregation into `BINS` (256) slots.
- `ringbuf_t`: Core C struct for circular byte storage with `head`/`tail` indices and power-of-two `capacity`.
- `ringbuf_init` / `ringbuf_write` / `ringbuf_read`: C functions for buffer lifecycle, chunked `memcpy` I/O with wrap-around, and state inspection.
- `rotate_backups.sh`: Bash script for atomic `.tar.gz` creation and retention-based pruning of old archives.
- `sales_report.py`: Python module with `Order` dataclass and aggregation functions for currency-filtered revenue analysis.

#### Data flow
- **Histogram Pipeline**: Input `uchar` stream → `histogram_partial` (local atomic counting) → `partial` global array → `histogram_reduce` (cross-group summation) → final `histogram` array.
- **Ring Buffer I/O**: Producer bytes → `ringbuf_write` (chunked copy to `data` buffer, advance `head`) → Consumer reads via `ringbuf_read` (chunked copy from `data`, advance `tail`).
- **Backup Flow**: `SOURCE_DIR` → `tar` archive to `.partial` temp file → atomic rename to timestamped `.tar.gz` → prune old files based on `KEEP` count.
- **Sales Flow**: CSV files → `parse_orders` (filter by `currency`) → `revenue_by_month_and_region` (aggregate via `defaultdict`) → filter by `--min-revenue` → tab-separated output.

#### Dependencies
- **OpenCL**: Built-ins (`atomic_inc`, `barrier`, `get_local_id`) and types (`uchar`, `uint`, `__global`, `__local`).
- **C Standard Library**: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`), `stdint.h` (`uint8_t`).
- **Unix Utilities**: `tar`, `date`, `du`, `ls`, `mv`, `rm`, `mkdir` for backup operations.
- **Python Standard Library**: `argparse`, `csv`, `collections.defaultdict`, `dataclasses`, `datetime`, `decimal`.

#### Cross-cutting
- **Concurrency**: Ring buffer relies on `volatile` indices for lock-free SPSC visibility; OpenCL kernels use `barrier(CLK_LOCAL_MEM_FENCE)` for work-group synchronization.
- **Error Handling**: Ring buffer uses explicit `ringbuf_status_t` for init errors and return values for partial I/O; scripts use strict validation (`set -euo pipefail`, `ValueError`) and cleanup traps.
- **Configuration**: All components support parameterization via CLI arguments (`KEEP`, `--currency`, `--min-revenue`) or function parameters (`capacity`, `length`, `group_count`).
