### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:51:54Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernels.

#### Purpose
- Builds a byte histogram in two passes.
- First pass aggregates per‑work‑group local histograms.
- Second pass reduces local histograms into a global histogram.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Initialize local histogram bins to zero.
  2. Iterate over input bytes in a strided fashion, atomically increment local bin counts.
  3. Copy local histogram to global `partial` buffer per work‑group.
- **histogram_reduce**
  1. For each bin (one work‑item per bin), sum corresponding entries across all partial histograms.
  2. Store the summed count in the global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins) -> void` – per‑group histogram accumulation.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram) -> void` – global histogram reduction.

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.

#### Exceptions / Errors
- None explicitly thrown; relies on OpenCL runtime for out‑of‑bounds or memory errors.
