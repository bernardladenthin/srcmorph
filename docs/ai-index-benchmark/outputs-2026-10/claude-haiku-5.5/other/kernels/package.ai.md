### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:33:04Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Two-pass OpenCL GPU kernel package that computes a 256-bin byte histogram of input data such as image pixels.

#### Purpose
- Counts occurrences of each byte value (0-255) over a buffer on the GPU.
- Uses per-work-group partial histograms, reduced into one final histogram.

#### Responsibilities
- Partial histogram generation: `histogram_partial` kernel counts bytes per work-group in local memory and stores the result in global memory.
- Final reduction: `histogram_reduce` kernel sums per-group partials into the final 256 bins, one work-item per bin.

#### Key units
- `histogram_partial`: pass 1 kernel; grid-stride byte counting with `atomic_inc` on `__local uint *local_bins`.
- `histogram_reduce`: pass 2 kernel; sums `partial[g * BINS + bin]` over `group_count` groups.
- `BINS`: macro, 256 bins.

#### Data flow
- Input bytes (`__global const uchar *input`, `length`) → `histogram_partial` → `partial` (`numGroups * BINS` uints) → `histogram_reduce` → `histogram[BINS]`.
- The two kernels must be launched separately to guarantee global ordering between passes.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`.
- No external files or host-side modules in this package.

#### Cross-cutting
- No error handling; `histogram_reduce` returns early for `bin >= BINS`.
- Buffer sizes are not checked: `local_bins` needs at least `BINS` entries, `partial` needs `numGroups * BINS`.
- Synchronization via `barrier(CLK_LOCAL_MEM_FENCE)` within each work-group; cross-group ordering relies on separate kernel launches.
