### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:05Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Computes a 256‑bin byte histogram of an input byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Uses two‑pass reduction: per‑group partial histograms then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions) in file `histogram.cl`.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.
- `histogram_reduce`: writes final 256‑bin histogram to `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel (`local_bins[b] = 0`).
  2. Each work‑item iterates over its assigned input indices (`i = gid; i < length; i += gsize`) and atomically increments the corresponding local bin.
  3. After synchronization, each work‑item writes its local bin values to the global `partial` buffer at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item handles one bin (`bin = get_global_id(0)`).
  2. Sums the partial counts for that bin across all groups (`sum += partial[g * BINS + bin]`).
  3. Stores the sum into the global `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram reduction.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels return early if `bin >= BINS` in `histogram_reduce`.
