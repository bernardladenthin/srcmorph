### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:32:22Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Uses two‑pass reduction: local partial histograms then global sum.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final 256‑bin histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel.
  2. Iterate over input bytes in strides of global size, atomically increment local bin counts.
  3. Copy local bins to global `partial` array at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item handles one bin (if `bin < BINS`).
  2. Sum corresponding bin counts across all partial histograms.
  3. Store result in global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – compute per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduce partials to final histogram.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels guard against out‑of‑range bins (`if (bin >= BINS) return;`).
