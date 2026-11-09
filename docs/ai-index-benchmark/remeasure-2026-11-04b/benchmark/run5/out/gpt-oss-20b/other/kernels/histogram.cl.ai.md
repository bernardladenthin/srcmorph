### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:54:51Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Split work into two passes: per‑group partial counts, then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions) in file `histogram.cl`.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms into `partial` buffer.
- `histogram_reduce`: writes final 256‑bin histogram into `histogram` buffer.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel (`local_bins[b] = 0`).
  2. Iterate over input bytes in strides of global size, atomically increment local bin for each byte.
  3. After all work-items finish, copy local bins to global `partial` at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work-item handles one bin (`bin = get_global_id(0)`).
  2. Sum the corresponding bin across all partial histograms (`partial[g * BINS + bin]`).
  3. Store the sum into `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram reduction.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.

#### Exceptions / Errors
- No explicit error handling; kernels return early if `bin >= BINS` in `histogram_reduce`.
