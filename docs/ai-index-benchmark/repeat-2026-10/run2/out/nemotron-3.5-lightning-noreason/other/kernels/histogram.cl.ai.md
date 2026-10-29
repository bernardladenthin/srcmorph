### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:02:20Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram from image data or byte streams using a two-pass OpenCL kernel approach.

#### Purpose
Computes a 256-bin histogram of byte values from input data using two OpenCL kernel passes for parallel efficiency.

#### Type
OpenCL C 1.2 kernel file; two `__kernel` functions implementing a parallel histogram algorithm.

#### Input
- `__global const uchar *input`: pointer to input byte data (image or byte stream)
- `const uint length`: total number of bytes to process
- `__global uint *partial`: output buffer for partial histograms (size: group_count * BINS)
- `__local uint *local_bins`: local memory buffer for work-group accumulation (size BINS)

#### Output
- `__global uint *histogram`: final 256-bin histogram accumulated across all work-groups

#### Core logic
- **Pass 1 (histogram_partial)**: Each work-group counts its assigned slice into local memory using `atomic_inc`, then writes one partial histogram row to global memory
- **Pass 2 (histogram_reduce)**: Each work-item sums one bin across all partial histograms from all work-groups to produce the final histogram

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void`: performs partial histogram computation in first pass
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void`: sums partial histograms into final result

#### Dependencies
- OpenCL C 1.2 standard
- `atomic_inc` for thread-safe local memory updates
- `barrier(CLK_LOCAL_MEM_FENCE)` for work-group synchronization

#### Exceptions / Errors
- Out-of-bounds access handled via `if (bin >= BINS) return;` guard in histogram_reduce
