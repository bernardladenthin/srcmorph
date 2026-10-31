### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:12Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Counts byte-value frequencies into 256-bin histograms for images or byte streams.
#### Purpose
- Two-pass parallel byte histogram with per-work-group partials plus final reduction
#### Responsibilities
- Strided parallel counting: cooperative zeroing, strided scan with atomics, coalesced partial write via `histogram_partial`
- Bin-wise reduction: per-bin summation across work-groups into final histogram via `histogram_reduce`
#### Key units
- `histogram_partial` — builds `partial[group_id * BINS + bin]` per-group counters from `input` byte stream
- `histogram_reduce` — sums `partial[g * BINS + bin]` over groups into `histogram[bin]` output
- `BINS` — 256 macro defining histogram bins, `local_bins` size, loop bounds
- `histogram.cl` — OpenCL C 1.2 kernel file with two `__kernel` entry points
#### Data flow
- `input` + `length` -> strided scan `i = gid; i < length; i += gsize` -> `atomic_inc` on `local_bins[input[i]]` -> `partial` slice
- `partial` + `group_count` -> per-bin `sum += partial[g * BINS + bin]` -> `histogram[bin]`
#### Dependencies
- OpenCL runtime buffers: global `uchar input`, global `uint partial`, global `uint histogram`, local `uint[256] local_bins`
- OpenCL built-ins: `get_global_id`, `get_local_id`, `get_global_size`, `CLK_LOCAL_MEM_FENCE` barriers, `atomic_inc`
#### Cross-cutting
- Cooperative parallelism: `lid` stride over `BINS` for zeroing, one work-item per `bin` with early exit in reduce
- Local-memory synchronization: `CLK_LOCAL_MEM_FENCE` barriers before/after counting
