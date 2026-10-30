### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:46:15Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Builds a 256-bin byte histogram of an image or byte stream with a two-pass OpenCL partial-count and reduction.

#### Purpose
- Count occurrences of each byte value (0–255) in an input buffer.
- Two-pass OpenCL C 1.2 design: work-group local atomic counts, then bin-wise sum of partial histograms.

#### Responsibilities
- Partial histogram: `histogram_partial` zeros local bins, atomically counts bytes, and writes one 256-bin slice per work-group.
- Reduction: `histogram_reduce` sums those slices into a final 256-bin histogram, one work-item per bin.

#### Key units
- `histogram.cl`: OpenCL C kernel source; two `__kernel` functions, no host API; constant `BINS` is 256.
- `histogram_partial(input, length, partial, local_bins)`: work-group partial byte counts into `partial[get_group_id(0) * BINS + bin]`.
- `histogram_reduce(partial, group_count, histogram)`: sums `partial[g * BINS + bin]` across `group_count` groups into `histogram[bin]`.

#### Data flow
- Pass 1 reads `__global const uchar *input` over `length`, uses `__local uint *local_bins` scratch, and stores per-group histograms in `__global uint *partial`.
- Each local work-item zeros a strided slice of `local_bins`, then `barrier(CLK_LOCAL_MEM_FENCE)`; a grid-stride loop does `atomic_inc(&local_bins[input[i]])`; a second barrier precedes the group write to `partial`.
- Pass 2 reads `partial` (layout `group_id * BINS + bin`) and writes the summed 256-bin result to `__global uint *histogram`; bins `>= BINS` return immediately.
- Work-item IDs come from `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, and `get_group_id`. Side effects are global stores; kernels return void.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Cross-cutting
- No explicit error returns. Correctness depends on `local_bins` covering 256 bins, `partial` sized for all groups, and matching `group_count`.
