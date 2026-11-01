### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:05:42Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte frequency histogram for image or generic byte data.
- Uses two‑pass reduction to avoid global atomics.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `partial`: per‑group partial histograms (size `group_count * BINS`).
- `histogram`: final 256‑bin histogram.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel.
  2. Each work‑item scans input in strided fashion, atomically incrementing local bin counts.
  3. Write local bins to global `partial` buffer per group.
- **histogram_reduce**
  1. One work‑item per bin.
  2. Sum corresponding bin across all partial histograms.
  3. Store result in global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernel exits early if `bin >= BINS` in `histogram_reduce`.
