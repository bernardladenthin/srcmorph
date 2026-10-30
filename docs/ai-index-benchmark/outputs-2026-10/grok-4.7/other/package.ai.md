### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:46:39Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
Synthesizing a package-level summary from the provided per-file and subdirectory summaries.

The "other" package is a miscellaneous collection of three subpackages: kernels/ for OpenCL byte histograms, ringbuf/ for an SPSC byte ring buffer, and scripts/ for backup rotation and sales reporting CLIs. These are quite heterogeneous.

Drafting the lead sentence to capture the package's domain essence without inventing details. The purpose and responsibilities sections are next.

The three subpackages are independent and do not collaborate. No cross-package data flow exists, so that section will be omitted.

Dependencies will be omitted because the subpackages do not depend on each other. Cross-cutting patterns are minimal: the utilities use different languages and error handling, with no shared types.

The lead sentence is finalized. The purpose section states that the package collects unrelated tools for byte counting, single-producer single-consumer buffering, and operator backup or sales reporting. It clarifies that kernels, ringbuf, and scripts do not call each other.

The responsibilities section now names the specific units. The histogram kernels are `histogram.cl` with partial-count and reduction functions. The ring buffer is a C SPSC circular buffer with `ringbuf_t` and its lifecycle and accessors. The operator scripts include `rotate_backups.sh`.

The sales script exposes `Order`, `parse_orders`, `revenue_by_month_and_region`, and `REQUIRED_COLUMNS`. Dependencies are language-specific: OpenCL C 1.2 for kernels, stdlib for ringbuf, and bash plus Python stdlib for scripts.> Miscellaneous byte-processing and operator tools: an OpenCL 256-bin histogram, a fixed-capacity SPSC byte ring, and standalone CLIs for backup rotation and monthly regional sales totals.

#### Purpose
- Groups unrelated utilities for counting byte values, buffering bytes between one producer and one consumer, and running operator backup and sales-report jobs.
- Not a shared library: `kernels/`, `ringbuf/`, and `scripts/` do not call each other.

#### Responsibilities
- Histogram kernels (`kernels/`): two-pass OpenCL C 1.2 partial counts and reduction into a 256-bin byte histogram.
- Ring buffer (`ringbuf/`): C SPSC circular buffer that stores at most `capacity - 1` bytes and copies with wraparound.
- Operator scripts (`scripts/`): dated `.tar.gz` backup rotation (`rotate_backups.sh`) and CSV order aggregation into a monthly regional revenue TSV (`sales_report.py`).

#### Key units
- `histogram.cl` — OpenCL kernel source; constant `BINS` is 256; no host API.
- `histogram_partial` — work-group local atomic byte counts written as one 256-bin slice per group into `partial`.
- `histogram_reduce` — one work-item per bin sums `partial` across `group_count` into `histogram`.
- `ringbuf_t` — `uint8_t *data`, power-of-two `capacity`, volatile producer `head`, volatile consumer `tail`.
- `ringbuf_status_t` — `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init` / `ringbuf_free` — allocate or release the buffer and clear indexes.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue up to available bytes via at most two `memcpy` chunks.
- `ringbuf_used` / `ringbuf_free_space` — occupancy and writable space from masked `head` and `tail`.
- `rotate_backups.sh` — writes `$BACKUP_DIR/${basename}_YYYYMMDDTHHMMSSZ.tar.gz` and prunes archives beyond `KEEP`.
- `Order` — frozen dataclass of `order_date`, `region`, and `amount`.
- `parse_orders` / `revenue_by_month_and_region` / `main` — currency-filtered CSV parse, `("%Y-%m", region)` sums, and CLI report.
- `REQUIRED_COLUMNS` — `order_date`, `region`, `amount`, `currency`.

#### Dependencies
- `kernels/`: OpenCL C 1.2 (`get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`; `uchar`, `uint`, `__global`, `__local`, `__kernel`).
- `ringbuf/`: `ringbuf.c` implements `ringbuf.h`; `stddef.h`, `stdint.h`, `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`); `extern "C"` when compiled as C++.
- `scripts/`: `bash` plus `tar`, `date`, `du`, `cut`, `ls`, `mkdir`, `mv`, `rm`, `basename`, `dirname`, `mapfile`; Python `argparse`, `csv`, `sys`, `collections.defaultdict`, `dataclasses.dataclass`, `datetime.date`, `decimal.Decimal`, `decimal.InvalidOperation`.
- No stated collaboration among the three subdirectories or between the two scripts.

#### Cross-cutting
- Independent components with separate error models: kernels return void and rely on sized `local_bins`, `partial`, and matching `group_count`; ring init uses integer status codes and short copies are not errors; scripts use non-zero exits, stderr, `set -euo pipefail` plus an `EXIT` trap for partial archives, and `ValueError` (path and line) on bad CSV.
