### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:57:41Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Splits work into two passes: local partial counts then global reduction.

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
  2. Each work‑item iterates over its assigned input indices, atomically increments corresponding local bin.
  3. After synchronization, each work‑item copies its local bin slice to the global `partial` buffer at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item handles one bin index.
  2. Sums the bin value across all partial histograms (`group_count` groups).
  3. Stores the sum into the final `histogram` array.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – partial histogram per work‑group.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram reduction.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None declared; kernels return early if `bin >= BINS` in `histogram_reduce`.
