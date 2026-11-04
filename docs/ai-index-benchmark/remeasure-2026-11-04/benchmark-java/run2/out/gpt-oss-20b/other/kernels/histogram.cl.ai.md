### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:33:59Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Computes a 256‑bin byte histogram of an input byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Splits work into two passes: local partial histograms and global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions) in a single source file.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final 256‑bin histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel.
  2. Each work‑item scans input in strided fashion, atomically increments local bin for each byte.
  3. After barrier, each work‑item writes its local bin values to the global `partial` buffer at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item corresponds to a histogram bin.
  2. Sums the bin value across all partial histograms (`group_count` groups).
  3. Stores the sum in the final `histogram` array.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – compute per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduce partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling; kernels return early if `bin >= BINS` in `histogram_reduce`.
