### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:50:17Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides OpenCL kernels for computing a 256-bin byte histogram of image or stream data via parallel partial counting and global reduction.

#### Purpose
- Implements GPU-accelerated histogram computation for `uchar` data streams.
- Utilizes a two-pass strategy to aggregate byte frequencies into 256 bins.

#### Responsibilities
- **Partial Counting**: Distributes input bytes across work-groups to generate intermediate per-group bin counts.
- **Global Reduction**: Aggregates partial counts from all work-groups into a single final histogram.

#### Key units
- `histogram_partial`: OpenCL kernel that initializes local bins, uses `atomic_inc` to count input bytes, and writes partial results to global memory.
- `histogram_reduce`: OpenCL kernel that sums partial counts across work-groups for each of the 256 bins to produce the final histogram.
- `BINS`: Constant defining the histogram size as 256.

#### Data flow
1. `histogram_partial` processes `input` bytes, accumulating counts in `local_bins` and writing to the `partial` global array.
2. `histogram_reduce` reads the `partial` array, sums values for each bin index across all work-groups, and writes the final result to the `histogram` global array.

#### Dependencies
- **OpenCL Built-ins**: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- **OpenCL Types**: `uchar`, `uint`, `__global`, `__local`.

#### Cross-cutting
- Relies on `barrier(CLK_LOCAL_MEM_FENCE)` for synchronization within work-groups during the partial counting phase.
- Assumes correct `length` and `group_count` parameters to prevent out-of-bounds memory access; no explicit error handling is implemented.
