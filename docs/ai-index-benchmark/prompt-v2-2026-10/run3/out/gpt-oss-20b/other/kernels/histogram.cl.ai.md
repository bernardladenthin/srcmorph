### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:09:28Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte frequency histogram for image or arbitrary byte data.
- Uses two‑pass reduction to avoid global atomics.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).  
- `histogram_partial` and `histogram_reduce` functions.

#### Input
- `histogram_partial`: `__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`.  
- `histogram_reduce`: `__global const uint *partial`, `uint group_count`, `__global uint *histogram`.

#### Output
- `histogram_partial`: writes per‑group partial histograms to `partial`.  
- `histogram_reduce`: writes final 256‑bin histogram to `histogram`.

#### Core logic
- **Partial kernel**  
  - Initialize local bin counters to 0.  
  - Each work‑item atomically increments local counter for its input bytes.  
  - After processing, write local bins to global `partial` array at offset `group_id * BINS`.  
- **Reduce kernel**  
  - Each work‑item corresponds to one bin.  
  - Sum the bin value across all partial histograms (`group_count` groups).  
  - Store the sum in the final `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` → writes per‑group partial histograms.  
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` → aggregates partials into final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- None explicitly handled; kernels return early if `bin >= BINS` in `histogram_reduce`.
