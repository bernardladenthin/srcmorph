### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:34Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Heterogeneous utilities for parallel byte-histogram analysis, SPSC byte buffering, backup rotation, and sales reporting.
#### Purpose
- Mixed utility package with no single pipeline: OpenCL byte-frequency analysis, circular FIFO buffering, filesystem backup and CSV revenue reporting.
#### Responsibilities
- Parallel histogram: per-group partial counting via `histogram_partial` plus global reduction via `histogram_reduce` into 256-bin histogram.
- Circular buffering: power-of-two SPSC FIFO lifecycle, wraparound enqueue/dequeue, occupancy tracking.
- Backup rotation: timestamped tar.gz archiving with KEEP-count retention and atomic publish.
- Sales reporting: currency-filtered CSV parsing and monthly per-region revenue aggregation.
#### Key units
- `histogram_partial` — builds `partial[group_id * BINS + bin]` from `input` byte stream.
- `histogram_reduce` — sums partials over `group_count` into `histogram[bin]`.
- `BINS`, `local_bins`, `partial` — 256 bin count, `uint[256]` local scratch, `group_count * BINS` intermediate buffer.
- `ringbuf_t` — FIFO state with `data`, `capacity`, volatile `head`/`tail`.
- `ringbuf_init`, `ringbuf_free` — power-of-two validated allocation and release.
- `ringbuf_write`, `ringbuf_read` — wraparound enqueue/dequeue with masked index advance.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_status_t` — occupancy accounting and `RINGBUF_OK/EINVAL/ENOMEM` results.
- `rotate_backups.sh` — creates `BACKUP_DIR/<basename>_YYYYMMDDTHHMMSSZ.tar.gz` with newest-first retention.
- `sales_report.py`, `Order`, `parse_orders` — CLI reporting, frozen `order_date/region/amount` record, currency-filtered `csv.DictReader` yield.
- `revenue_by_month_and_region`, `main` — `defaultdict Decimal` YYYY-MM month-region sums, `csv_files/--currency/--min-revenue` tab-separated report.
#### Data flow
- `input` + `length` -> grid-stride loop with `atomic_inc` into `local_bins` -> barrier -> `partial` slice -> per-bin accumulation -> `histogram`.
- `src/len` -> `ringbuf_write` clamped by `free_space` -> split `memcpy` wraparound -> `head` advance; buffered bytes -> `ringbuf_read` clamped by `used` -> split `memcpy` -> `dst`.
- `SOURCE_DIR` -> timestamped tar.gz `.partial` temp -> atomic publish -> `ls -1t` KEEP retention prune.
- Order CSV exports -> `parse_orders` currency filter -> `revenue_by_month_and_region` Decimal sums -> stdout report.
#### Dependencies
- `kernels/`: self-contained OpenCL C 1.2; builtins `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`, `get_global/group/local_id`, `get_local/global_size`.
- `ringbuf/`: internal `ringbuf.c` implements `ringbuf.h`; external `stdlib.h malloc/free`, `string.h memcpy`, `stddef.h size_t`, `stdint.h uint8_t`.
- `scripts/`: no internal collaboration; shell `bash/tar/date/mkdir/mv/ls/rm/du/cut/basename/dirname/trap`; Python `argparse/csv/sys/collections.defaultdict/dataclasses.dataclass/datetime.date/decimal.Decimal`.
#### Cross-cutting
- Parallel pattern: grid-stride `i = gid; i < length; i += gsize`, `barrier(CLK_LOCAL_MEM_FENCE)` sync, one work-item per `bin` reduction.
- Buffer pattern: `is_power_of_two` validation, `& (capacity-1)` modulo indexing, capacity-1 usable bytes, SPSC `head`/`tail` ownership, `min_size` clamping.
- CLI-first ops: positional/flag args, stdout reporting, non-zero exits, `set -euo pipefail` + EXIT trap, UTF-8 CSV + `Decimal` arithmetic, `KEEP ^[1-9][0-9]*$`, `SOURCE_DIR` and `REQUIRED_COLUMNS` checks with `ValueError path:line_no` wrapping.
