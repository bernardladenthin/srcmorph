### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:54:48Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or generic byte data.
- Splits work into two passes: per‑group partial histograms and global reduction.

#### Type
- OpenCL C kernels (OpenCL 1.2).  
- `histogram_partial` and `histogram_reduce` functions.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.  
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms into `partial`.  
- `histogram_reduce`: writes final 256‑bin histogram into `histogram`.

#### Core logic
- **histogram_partial**
  1. Zero local bin array in parallel.  
  2. Iterate over input bytes in strided global work‑items, atomically increment local bin counts.  
  3. Copy local bins to global `partial` buffer per group.
- **histogram_reduce**
  1. Each global work‑item handles one bin.  
  2. Sum corresponding bin values across all partial histograms.  
  3. Store summed count in final `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` → writes per‑group partial histograms.  
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` → aggregates partials into final histogram.

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; kernels assume valid pointers and sizes.
