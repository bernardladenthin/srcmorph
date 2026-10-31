### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:26Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides OpenCL GPU kernels that compute 256-bin histograms of raw byte streams via a two-pass parallel reduction strategy.

#### Purpose
- Exposes a reusable GPU-accelerated histogram primitive for arbitrary byte inputs (images or raw streams) using OpenCL C 1.2 kernels.
- Splits histogram computation into two kernel passes for work-group parallelism and efficient global reduction.

#### Responsibilities
- `histogram.cl` hosts both kernel stages: per-group partial counting and final cross-group reduction.

#### Key units
- `histogram_partial` — first-pass kernel; each work-group zeroes local bins, atomically increments bins per input byte, then writes its 256-bin slice to the partial buffer.
- `histogram_reduce` — second-pass kernel; one work-item per bin sums partial counts across all groups and writes the final histogram.

#### Data flow
- Input byte buffer → `histogram_partial(input, length, partial, local_bins)` → `partial[group_id * BINS + bin]` per group → `histogram_reduce(partial, group_count, histogram)` → final `histogram[bin]` (256 bins).

#### Dependencies
- OpenCL C standard built-ins: `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`.
- Macro `BINS` (defined as 256) used by both kernels.

#### Cross-cutting
- No explicit error handling or exception paths; correctness relies on OpenCL memory fencing and invocation semantics.
- Concurrency handled via local-memory barriers (`CLK_LOCAL_MEM_FENCE`) and `atomic_inc` for contention-safe bin updates.
