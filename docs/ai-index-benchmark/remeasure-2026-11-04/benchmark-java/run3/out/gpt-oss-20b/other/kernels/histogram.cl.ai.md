### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:01Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Computes a 256‑bin byte histogram of an input byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a 256‑bin histogram of byte values.
- Operates in two passes: per‑group partial accumulation and global reduction.

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
  2. Each work‑item atomically increments local bin for its assigned input bytes.
  3. After synchronization, each work‑item writes its local bin counts to the global `partial` buffer.
- **histogram_reduce**
  1. Each work‑item corresponds to one histogram bin.
  2. Sums the counts for that bin across all partial histograms.
  3. Stores the sum in the global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram reduction.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None explicitly handled; out‑of‑range bin indices are guarded by `if (bin >= BINS)` in `histogram_reduce`.
