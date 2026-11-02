### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:44:31Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Uses two‑pass reduction: local partial histograms then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions) in a source file.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bins in parallel.
  2. Each work‑item scans input in strided fashion, atomically increments local bin for each byte.
  3. After barrier, each work‑item writes its local bin counts to global `partial` at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item corresponds to a histogram bin.
  2. Sums the partial counts for that bin across all groups.
  3. Stores the sum in the global `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` → builds partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` → aggregates partials into final histogram.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels return early if bin index exceeds `BINS`.
