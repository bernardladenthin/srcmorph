### other
- H: 1.0
- C: FC5D91C6
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:50:21Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Provides GPU-accelerated 256-bin histogram computation for byte streams (e.g., image pixels), a lock-free SPSC circular buffer for inter-thread streaming, and standalone CLI tools for backup rotation and monthly sales reporting.

#### Purpose
- Supply low-level compute and concurrency primitives for byte-oriented data pipelines (GPU histogramming, SPSC ring buffer)
- Provide operational CLI utilities for infrastructure maintenance (backup rotation) and business analytics (sales reporting)

#### Responsibilities
- **GPU histogram pipeline**: two-pass OpenCL kernels (`histogram_partial` atomic local accumulation, `histogram_reduce` global summation) producing 256-bin frequency histograms of uchar inputs
- **Lock-free streaming**: power-of-two SPSC `ringbuf_t` with bitmask wraparound, `volatile` head/tail indices, and clamped two-chunk `memcpy` transfers
- **Operational scripts**: UTC-timestamped `tar.gz` backup rotation with retention pruning; CSV parsing and monthly/region revenue aggregation with currency filtering

#### Key units
- `histogram_partial` — OpenCL `__kernel`: grid-stride traversal, `atomic_inc` into `__local uint[256]`, export per-group partials
- `histogram_reduce` — OpenCL `__kernel`: sums partials across `group_count` into final `histogram[bin]`
- `BINS` — macro constant (256) defining histogram resolution and local array size
- `ringbuf_t` — struct: `uint8_t *data`, `size_t capacity`, `volatile size_t head` (producer), `volatile size_t tail` (consumer)
- `ringbuf_status_t` — enum: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`
- `ringbuf_init/free/write/read/used/free_space` — API for allocation, validation, and wraparound-safe data transfer
- `rotate_backups.sh` — Bash executable taking `SOURCE_DIR`, `BACKUP_DIR`, `KEEP`; produces timestamped archives and prunes old ones
- `sales_report.py` — Python module with `Order` frozen dataclass, `parse_orders` generator, `revenue_by_month_and_region` aggregator, `main` CLI

#### Data flow
- **Histogram**: `input`/`length` → `histogram_partial` (atomic local bins → per-group partial buffers) → `histogram_reduce` (sum across `group_count`) → final `histogram[256]`
- **Ring buffer**: producer `ringbuf_write` (clamp to free space, two `memcpy` chunks across wraparound, advance `head` via bitmask) ↔ consumer `ringbuf_read` (clamp to used, two `memcpy` chunks, advance `tail`)
- **Backups**: source dir → `tar -czf` to `.partial` → atomic `mv` to `${name}_<UTC-timestamp>.tar.gz` → `ls -1t` newest-first → `rm` beyond `KEEP` retention
- **Sales**: CSV paths → `csv.DictReader` validation → currency-filtered `Order` objects (`Decimal`, ISO dates) → `defaultdict` keyed by `(YYYY-MM, region)` → sorted TSV stdout

#### Dependencies
- OpenCL 1.2 C built-ins: `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`, work-group query functions (`get_local_id`, `get_group_id`, etc.)
- C standard library: `<stdlib.h>`, `<string.h>`, `<stdint.h>`, `<stddef.h>`
- Python standard library: `argparse`, `csv`, `dataclasses`, `datetime.date`, `decimal.Decimal`, `collections.defaultdict`
- System utilities: Coreutils (`date`, `ls`, `mv`, `rm`, `tar`, `basename`, `du`), Bash builtins (`set`, `trap`, `mapfile`, arrays)
- Internal: `kernels/`, `ringbuf/`, `scripts/` are independent subpackages with no cross-imports

#### Cross-cutting
- Power-of-two bitmask arithmetic: `BINS=256` and ringbuf capacity use `& (n-1)` for wraparound/indexing instead of modulo
- Atomic/volatile concurrency: `atomic_inc` + double `barrier(CLK_LOCAL_MEM_FENCE)` in kernels; `volatile` head/tail with one-slot-empty invariant in SPSC ringbuf
- Two-phase chunked processing: partial-then-reduce kernels, two-`memcpy` wraparound copies, `.partial` temp-file-then-rename backup writes
- Strict exit-code contracts: 0 success, 1 empty results (Python), 2 invalid args (Bash); `set -euo pipefail` with `trap` cleanup; contextual `ValueError` with `path:line_no`
- Temporal handling: UTC `%Y%m%dT%H%M%SZ` for backup filenames; ISO 8601 `date.fromisoformat` for order dates
