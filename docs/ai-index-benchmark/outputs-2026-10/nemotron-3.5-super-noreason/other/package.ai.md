### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:04:59Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Aggregates monthly regional revenue from order CSVs, manages timestamped directory backups with retention limits, computes 256-bin byte histograms via OpenCL, and provides a fixed-capacity circular byte buffer for streaming.

#### Purpose
- Provides image byte-histogram computation, circular byte buffering, sales reporting, and backup rotation utilities within a single package.

#### Responsibilities
- Parallel 256-bin byte histogram computation using two-pass OpenCL reduction.
- Fixed-capacity circular byte buffer for producer-consumer streaming with power-of-two capacity.
- Monthly regional revenue aggregation from order CSV exports with currency and revenue filtering.
- Timestamped directory backups with per-directory retention enforcement.

#### Key units
- `histogram_partial`: OpenCL kernel that counts bytes per work-group using local memory and atomics, writing partial histograms to global memory.
- `histogram_reduce`: OpenCL kernel that sums partial histograms across work-groups into a final 256-bin output.
- `ringbuf_t`: struct with `data`, `capacity`, volatile `head`, and volatile `tail` indices.
- `ringbuf_status_t`: enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM`.
- `ringbuf_init`, `ringbuf_free`, `ringbuf_write`, `ringbuf_read`, `ringbuf_used`, `ringbuf_free_space`: ring buffer lifecycle and streaming operations.
- `sales_report.py`: `Order` dataclass, `parse_orders`, `revenue_by_month_and_region`, and `main` CLI entry point.
- `rotate_backups.sh`: shell utility taking `<source_dir> <backup_dir> [keep=7]` for tar-based archiving and retention cleanup.

#### Data flow
- Histogram: input byte stream feeds `histogram_partial` to produce per-work-group partial histograms, which `histogram_reduce` sums into a single 256-bin histogram.
- Ring buffer: producers write bytes via `ringbuf_write` into the backing array in up to two wrapped chunks; consumers read via `ringbuf_read` in up to two wrapped chunks; callers check `ringbuf_used` and `ringbuf_free_space` to bound operations.
- Sales report: CSV rows are parsed into `Order` objects, aggregated by `("%Y-%m", region)` in a `defaultdict`, filtered by `--min-revenue`, and printed as `month\tregion\trevenue currency`.
- Backup rotation: a source directory is tarred into a `.partial` temp file, renamed to a UTC-timestamped archive, then archives beyond the keep limit are deleted newest-first.

#### Dependencies
- OpenCL C 1.2 builtins and `BINS` macro (256) for histogram kernels.
- `ringbuf.h` / `ringbuf.c`, `stdlib.h`, `string.h`, `<stddef.h>`, `<stdint.h>` for the ring buffer.
- `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation` for `sales_report.py`.
- Bash builtins and external `tar`, `mv`, `rm`, `ls`, `du`, `cut`, `mkdir` for `rotate_backups.sh`.

#### Cross-cutting
- Two-pass reduction pattern in histogram kernels with local memory fences and out-of-range bin guards.
- Ring buffer reserves one slot to distinguish full from empty, yielding capacity - 1 usable bytes; documented as thread-unsafe despite header claims of thread-safety.
- Both script units validate inputs and exit non-zero on failure; `sales_report.py` returns 1 with `no orders found` on stderr, `rotate_backups.sh` returns 2 with a usage message and uses an EXIT trap to remove `.partial` temp files.
