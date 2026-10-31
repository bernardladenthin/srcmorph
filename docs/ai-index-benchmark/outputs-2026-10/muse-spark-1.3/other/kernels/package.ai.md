### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:11Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Counts 256-bin byte-value frequencies for images and byte streams via parallel two-pass reduction.
#### Purpose
- Parallel byte histogram for image / byte-stream frequency analysis in OpenCL.
#### Responsibilities
- Per-work-group partial counting via `histogram_partial` with local scratch and atomics.
- Global reduction via `histogram_reduce` summing partials into final 256-bin histogram.
#### Key units
- `histogram_partial` — builds `partial[group_id * BINS + bin]` from `input` byte stream.
- `histogram_reduce` — sums `partial[g * BINS + bin]` over `group_count` into `histogram[bin]`.
- `BINS` — macro 256 defining bin count.
- `local_bins` — `uint[256]` local-memory per-group scratch.
- `partial` — `group_count * BINS` intermediate counters buffer.
#### Data flow
- `input` + `length` -> grid-stride loop with `atomic_inc` into `local_bins` -> barrier -> `partial` group slice -> `histogram_reduce` per-bin accumulation -> `histogram`.
#### Dependencies
- Self-contained OpenCL C 1.2, no includes/imports.
- Builtins: `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`, `get_global_id`, `get_group_id`, `get_local_id`, `get_local_size`, `get_global_size`.
#### Cross-cutting
- Grid-stride looping `i = gid; i < length; i += gsize` for variable input sizes.
- `barrier(CLK_LOCAL_MEM_FENCE)` synchronizing local zeroing and counting phases.
- One work-item per `bin` reduction pattern in second pass.
