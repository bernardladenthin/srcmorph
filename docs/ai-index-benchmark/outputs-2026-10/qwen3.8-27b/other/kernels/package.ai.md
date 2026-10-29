### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:46:12Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> GPU-accelerated 256-bin byte histogram computation via a two-pass OpenCL kernel (local-memory accumulation then cross-group reduction).

#### Purpose
- Provides a high-throughput histogram kernel for arbitrary `uchar` buffers (images, raw byte streams) where single-pass global atomic operations would be too slow.

#### Responsibilities
- **Pass 1 – per-group local accumulation:** each work-group cooperatively counts byte values into `__local` scratch using `atomic_inc`, then flushes its 256 counters to a global `partial` buffer.
- **Pass 2 – cross-group reduction:** one work-item per bin serially sums all group partials into the final 256-bin `histogram`.

#### Key units
- `histogram_partial` (`__kernel`) – per-group local-memory histogram accumulation; writes `partial[group_id * 256 + bin]`.
- `histogram_reduce` (`__kernel`) – cross-group reduction; writes final `histogram[bin]`.

#### Data flow
`input` (global `uchar*`) → pass 1 reads bytes, accumulates in `__local uint local_bins[256]` via `atomic_inc`, flushes to `partial` (global `uint*`, size `group_count * 256`) → pass 2 reads `partial`, sums across groups, writes `histogram` (global `uint[256]`).

#### Dependencies
- OpenCL C 1.2 runtime only; no external includes.
- Host must allocate `partial` buffer (`group_count * 256 * sizeof(uint)`) and launch pass 1 with a 1D NDRange whose `local_size` divides 256, and pass 2 with `global_size = 256`.

#### Cross-cutting
- No error handling inside the kernel; host is responsible for bounds correctness (`length` must match buffer size).
- Pass 2 guards against over-sized grids (early return if `bin >= 256`).
- Synchronization relies on `barrier(CLK_LOCAL_MEM_FENCE)` between zeroing and accumulation phases in pass 1.
