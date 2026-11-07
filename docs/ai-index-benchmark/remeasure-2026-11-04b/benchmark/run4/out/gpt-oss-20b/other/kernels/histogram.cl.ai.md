### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:51:50Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Uses two‑pass reduction: local partial histograms then global sum.

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
  1. Zero local bin array in parallel.
  2. Iterate over input bytes in strided global work‑items, atomically increment local bin counts.
  3. Write local bin counts to global `partial` buffer per group.
- **histogram_reduce**
  1. Each global work‑item handles one bin.
  2. Sum corresponding bin across all partial histograms.
  3. Store result in final `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – compute per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduce partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.

#### Exceptions / Errors
- None; kernels exit early if `bin >= BINS` in `histogram_reduce`.
