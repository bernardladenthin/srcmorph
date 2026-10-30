### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:18Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> A multi-part toolkit covering a GPU byte-histogram computation, a lock-free single-producer/single-consumer byte ring buffer, and backup-rotation and monthly sales-reporting utilities.

#### Purpose
- Computes 256-bin byte histograms on OpenCL GPUs via two-pass kernels (`histogram_partial`, `histogram_reduce`).
- Provides a C SPSC byte FIFO (`ringbuf_t`) and command-line utilities for dated backup rotation and per-region monthly revenue reporting.

#### Responsibilities
- GPU histogramming: per-work-group partial counts in local memory, then reduction into a final 256-bin histogram (`kernels/`).
- Byte queuing: fixed power-of-two circular buffer with wraparound read/write and occupancy queries (`ringbuf/`).
- Backup management: timestamped `.tar.gz` archives created atomically and pruned to the newest `KEEP` (`scripts/rotate_backups.sh`).
- Sales reporting: CSV order parsing, currency filtering, and `(YYYY-MM, region)` revenue aggregation with a minimum-revenue threshold (`scripts/sales_report.py`).

#### Key units
- `histogram_partial`: OpenCL pass-1 kernel; grid-stride byte counting with `atomic_inc` on `__local uint *local_bins`.
- `histogram_reduce`: OpenCL pass-2 kernel; sums `partial[g * BINS + bin]` across groups.
- `ringbuf_t`: C struct with `data`, `capacity`, `volatile head`, `volatile tail`.
- `ringbuf_status_t`: enum `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free`: allocate and release the backing array.
- `ringbuf_write` / `ringbuf_read`: bulk copy in and out, returning bytes transferred.
- `ringbuf_used` / `ringbuf_free_space`: readable and writable byte counts.
- `rotate_backups.sh`: Bash archive creation and pruning with EXIT trap cleanup.
- `parse_orders`: Python generator yielding `Order` for the requested currency.
- `revenue_by_month_and_region`: sums `Decimal` amounts into a sorted dict keyed by month and region.
- `Order`: frozen `dataclass` with `order_date`, `region`, `amount`.
- `main`: Python entry point combining files, applying `--min-revenue`, returning exit code.

#### Data flow
- GPU: input bytes → `histogram_partial` → `partial` (`numGroups * BINS` uints) → `histogram_reduce` → `histogram[BINS]`. Kernels are launched separately to ensure global ordering.
- Ring buffer: producer `src` → `ringbuf_write` → `data` at `head`; consumer `data` at `tail` → `ringbuf_read` → `dst`. Occupancy is `head - tail` masked; free space is `capacity - 1 - used`.
- Backup: source and backup directories → `tar -czf` to `.partial` → atomic `mv` → `ls -1t` → remove older archives.
- Sales: CSV files → `csv.DictReader` → `parse_orders` → `Order` list → `revenue_by_month_and_region` → threshold filter → stdout report.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_global_id`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`; types `uchar`, `uint`.
- C standard library: `<stdlib.h>`, `<string.h>` (`memcpy`), `<stdint.h>`, `<stddef.h>`; internal `ringbuf.c` implements `ringbuf.h`.
- Bash external tools: `tar`, `date`, `ls`, `du`, `cut`, `rm`, `mv`, `mkdir`, `mapfile`, `trap`.
- Python stdlib: `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- Internal collaborations: none between the three groups; each is independent.

#### Cross-cutting
- Synchronization: GPU uses `barrier(CLK_LOCAL_MEM_FENCE)` within groups and separate launches across groups; ring buffer is SPSC using `volatile` indices without locks or barriers.
- Capacity rule: ring buffer requires a nonzero power of two and reserves one slot, so usable size is `capacity - 1`.
- Error handling: `RINGBUF_EINVAL` / `RINGBUF_ENOMEM` status codes; Python raises `ValueError` with `path:line` context; Bash writes errors to stderr and exits 2 on usage errors.
- Validation gaps: no buffer-size checks in kernels; no NULL checks on ring buffer pointers; Python `OSError` not caught; Bash `ls` with zero matches under `pipefail` may abort pruning.
- Safety: Bash uses `set -euo pipefail` with an EXIT trap removing partial archives.
- Configuration: CLI arguments only; `KEEP` defaults to `7`, currency defaults to `EUR`.
