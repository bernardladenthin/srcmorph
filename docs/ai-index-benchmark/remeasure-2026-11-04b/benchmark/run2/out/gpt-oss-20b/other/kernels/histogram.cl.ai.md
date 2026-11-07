### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:02:47Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Splits work into two passes: local partial histograms and global reduction.

#### Type
- OpenCL C kernels (`__kernel` functions) in file `histogram.cl`.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bins in parallel (`local_bins[b] = 0`).
  2. Iterate over input bytes in strides of global size, atomically increment local bin for each byte.
  3. Copy local bins to global `partial` array at offset `group_id * BINS`.
- **histogram_reduce**
  1. For each bin (one work‑item per bin), sum corresponding entries across all groups in `partial`.
  2. Store the sum in the final `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – compute per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduce partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels return early if `bin >= BINS` in `histogram_reduce`.
