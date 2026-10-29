### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:10Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram of an image or byte stream using two-pass OpenCL parallel reduction.

#### Purpose
Computes a 256-bin histogram from raw byte data using OpenCL parallel reduction across two kernel passes.

#### Type
OpenCL C 1.2 kernel file. Two `__kernel` functions: `histogram_partial` and `histogram_reduce`.

#### Input
- `__global const uchar *input`: pointer to source byte data (image or byte stream)
- `const uint length`: total number of bytes to process
- `__global uint *partial`: output buffer for partial histograms (size: group_count * BINS)
- `__local uint *local_bins`: local memory buffer for work-group accumulation (size: BINS)

#### Output
- `__global uint *histogram`: final 256-bin histogram accumulated across all work-groups

#### Core logic
- **Pass 1 (`histogram_partial`)**: Each work-group zeroes its local_bins, then atomically increments counters for its assigned byte range. One partial histogram per group written to global partial buffer.
- **Pass 2 (`histogram_reduce`)**: One work-item per bin iterates across all partial histograms and sums into the final histogram array.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void`: performs partial histogram computation per work-group
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void`: reduces partial histograms into final result

#### Dependencies
- OpenCL API types: `uchar`, `uint`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`
- Macro `BINS` defined as 256

#### Exceptions / Errors
- Out-of-bounds access guarded in `histogram_reduce` via `if (bin >= BINS) return;`
