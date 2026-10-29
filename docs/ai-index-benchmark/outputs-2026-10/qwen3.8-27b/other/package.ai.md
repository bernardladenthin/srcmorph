### other
- H: 1.0
- C: 142CEE21
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:46:47Z
- G: 
- A: 0.0.0
- X: package
- F: [kernels/](kernels/package.ai.md)
- F: [ringbuf/](ringbuf/package.ai.md)
- F: [scripts/](scripts/package.ai.md)
---
> Independent collection of performance-critical data-processing primitives (GPU histogram, lock-free ring buffer) and standalone operational CLI scripts (backup rotation, sales revenue reporting).

#### Purpose
- Bundles three self-contained sub-packages with no shared code: an OpenCL histogram kernel, a C SPSC ring buffer, and a pair of shell/Python utility scripts.
- Each sub-package solves a distinct problem with no cross-dependency.

#### Responsibilities
- **GPU histogram** (`kernels/`) — two-pass OpenCL kernel computing a 256-bin byte histogram over arbitrary `uchar` buffers via local-memory accumulation then cross-group reduction.
- **Lock-free ring buffer** (`ringbuf/`) — fixed-capacity, power-of-two SPSC circular byte buffer for inter-thread FIFO exchange without mutexes.
- **Operational scripts** (`scripts/`) — bash backup-rotation with retention policy; Python sales-revenue aggregation by month and region from CSV exports.

#### Key units
- `histogram_partial` / `histogram_reduce` — OpenCL `__kernel` functions for per-group accumulation and final reduction into `uint[256]`.
- `ringbuf_t` — struct with `volatile size_t head/tail`, `uint8_t *data`, `size_t capacity`; core SPSC data structure.
- `ringbuf_init` / `ringbuf_free` / `ringbuf_write` / `ringbuf_read` / `ringbuf_used` / `ringbuf_free_space` — full lifecycle and data-transfer API.
- `rotate_backups.sh` — bash CLI; `tar -czf` + atomic `mv` + `ls -1t` retention pruning.
- `sales_report.py` — Python CLI; `Order` dataclass, `parse_orders`, `revenue_by_month_and_region` (Decimal aggregation in `defaultdict`).

#### Data flow
- **Histogram**: `uchar* input` → pass 1 `__local uint[256]` accumulation → `partial[group*256+bin]` → pass 2 serial sum → `histogram[256]`.
- **Ring buffer**: producer `ringbuf_write` → `data[head..]` (≤2 `memcpy`) → `head++`; consumer `ringbuf_read` → `data[tail..]` (≤2 `memcpy`) → `tail++`.
- **Backup**: source dir → `.partial` temp → atomic `mv` to timestamped `.tar.gz` → prune beyond `KEEP`.
- **Sales**: CSV → `parse_orders` (filter, validate) → `revenue_by_month_and_region` (Decimal sum) → threshold filter → TSV stdout.

#### Dependencies
- `kernels/` — OpenCL C 1.2 runtime only; host allocates `partial` buffer and manages NDRange sizing.
- `ringbuf/` — `stdlib.h`, `string.h`, `stddef.h`, `stdint.h`; no external libraries.
- `scripts/` — system utilities (`tar`, `gzip`, `ls`, `mv`, `rm`); Python stdlib (`argparse`, `csv`, `collections`, `dataclasses`, `datetime`, `decimal`).
- No cross-dependency between the three sub-packages.

#### Cross-cutting
- **Concurrency**: ring buffer uses `volatile` for SPSC visibility (no atomics, no locks); histogram uses `barrier(CLK_LOCAL_MEM_FENCE)` for intra-group sync.
- **Error handling**: ring buffer clamps silently on full/empty; scripts use explicit exit codes (2 for validation, 1 for no-data); kernel has no internal error path.
- **Sizing invariants**: ring buffer enforces power-of-two capacity with bitmask arithmetic; histogram pass 2 guards `bin >= 256`.
- **Standalone design**: no shared base types, common interfaces, or configuration across sub-packages.
