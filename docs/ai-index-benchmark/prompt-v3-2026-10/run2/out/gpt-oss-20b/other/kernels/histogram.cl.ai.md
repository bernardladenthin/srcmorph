### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:20:37Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin byte histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a per‑byte frequency histogram.
- Splits work into two passes: local accumulation then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions) in a single source file.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bins in local memory.
  2. Each work‑item iterates over its assigned input indices, atomically increments local bin counts.
  3. After barrier, each work‑item writes its local bin counts to the global `partial` buffer at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item corresponds to a histogram bin.
  2. Sums the partial counts for that bin across all work‑groups.
  3. Stores the sum in the global `histogram` buffer.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – Accumulates per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – Reduces partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – Number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling; out‑of‑range bin indices are guarded by `if (bin >= BINS)` in `histogram_reduce`.
