### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:51Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Calculates a 256-bin byte histogram for an image or byte stream using OpenCL parallel reduction across two passes.

#### Purpose
Computes a 256-bin byte histogram from raw image or stream data using a two-pass OpenCL kernel design for parallel efficiency.

#### Responsibilities
- **Pass 1 accumulation**: `histogram_partial` kernel launches work-groups that zero local bins, accumulate partial counts atomically per work-item, and write group slices to a shared partial buffer.
- **Pass 2 reduction**: `histogram_reduce` kernel performs a global reduction across all partial histograms to produce the final 256-bin result.
- **Parallel efficiency**: Two-pass design overlaps work-group synchronization with atomic operations to scale across byte-stream and image inputs.

#### Key units
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins)`: Pass 1 kernel that accumulates per-work-group partial histograms into local memory and the global partial buffer.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram)`: Pass 2 kernel that reduces all partial histograms into the final 256-bin output buffer.
- `BINS = 256`: fixed bin count for byte-value histogramming.

#### Data flow
1. Pass 1 reads strided byte segments via `histogram_partial`, accumulates counts in `__local uint *local_bins` using `atomic_inc`, then writes each work-group's slice to `__global uint *partial[group_id * BINS + bin]`.
2. Pass 2 invokes `histogram_reduce`, where one work-item per bin iterates over `group_count` partial entries and sums into `__global uint *histogram[bin]`.

#### Dependencies
- OpenCL C 1.2 runtime
- `atomic_inc` for thread-safe local memory increments
- `barrier(CLK_LOCAL_MEM_FENCE)` for work-group synchronization
- Pre-allocated `partial` and `histogram` output buffers

#### Cross-cutting
- Local memory zeroing loop with stride `lsize` ensures work-group isolation before accumulation.
- Out-of-bounds guard `if (bin >= BINS) return` in `histogram_reduce` prevents overread beyond the 256-bin boundary.
