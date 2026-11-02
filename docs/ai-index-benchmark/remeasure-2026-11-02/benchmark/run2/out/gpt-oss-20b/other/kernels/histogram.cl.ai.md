### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:48:49Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Uses two‑pass reduction: local partial histograms then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final histogram to `histogram`.

#### Core logic
- `histogram_partial`
  1. Zero local bins in parallel.
  2. Each work‑item atomically increments local bin for its assigned input bytes.
  3. Write local bins to global `partial` array per group.
- `histogram_reduce`
  1. Each work‑item sums the same bin across all partial histograms.
  2. Store summed count in global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – compute per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduce partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None explicitly handled; out‑of‑range bins are ignored in `histogram_reduce`.
