### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:10:55Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256‑bin byte histogram of an image or byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram in two stages: per‑work‑group partial counts and global reduction.
- Supports arbitrary input length and parallel execution.

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
  1. Initialize local bins to zero (each work‑item handles a slice).
  2. Iterate over input in strides of global size, atomically increment local bin for each byte.
  3. Write local bins to global `partial` array at offset `group_id * BINS`.
- **histogram_reduce**
  1. Each work‑item processes one bin.
  2. Sum corresponding entries from all partial histograms.
  3. Store the sum in the final `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction.

#### Constants and configuration
- `#define BINS 256`

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels exit early if `bin >= BINS` in `histogram_reduce`.
