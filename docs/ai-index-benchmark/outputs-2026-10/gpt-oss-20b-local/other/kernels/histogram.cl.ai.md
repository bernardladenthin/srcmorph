### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:18:11Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Uses two‑pass reduction: local partial histograms then global sum.

#### Type
- OpenCL C 1.2 kernels.  
  - `histogram_partial`: class `__kernel`.  
  - `histogram_reduce`: class `__kernel`.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.  
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.  
- `histogram_reduce`: writes final 256‑bin histogram to `histogram`.

#### Core logic
- **Partial kernel**  
  - Zero local bins in parallel.  
  - Each work‑item scans input in strided fashion, atomically increments local bin.  
  - After barrier, each work‑item copies its local bins to global `partial` at offset `group_id * BINS`.  
- **Reduce kernel**  
  - Each work‑item handles one bin (`bin = get_global_id(0)`).  
  - Sums across all `group_count` partial histograms for that bin.  
  - Stores result in `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` → void  
  *Accumulates per‑group byte counts.*

- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` → void  
  *Sums partial histograms into final result.*

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None; relies on correct kernel launch parameters and buffer sizes.
