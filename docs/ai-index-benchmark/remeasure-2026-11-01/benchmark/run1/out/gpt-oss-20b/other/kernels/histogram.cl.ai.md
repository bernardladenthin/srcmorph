### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:27:44Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Uses two‑pass reduction: per‑group partial histograms then global reduction.

#### Type
- OpenCL C 1.2 kernels (`__kernel` functions).  
- `histogram_partial` and `histogram_reduce` functions.

#### Input
- `histogram_partial`:  
  - `__global const uchar *input` – byte array.  
  - `uint length` – number of bytes.  
  - `__global uint *partial` – output buffer for per‑group histograms.  
  - `__local uint *local_bins` – local memory for per‑group bin counts.
- `histogram_reduce`:  
  - `__global const uint *partial` – per‑group histograms.  
  - `uint group_count` – number of work‑groups that produced partials.  
  - `__global uint *histogram` – final 256‑bin histogram.

#### Output
- `histogram_partial`: writes `BINS` counts per work‑group into `partial`.  
- `histogram_reduce`: writes final 256 counts into `histogram`.

#### Core logic
- **histogram_partial**  
  1. Initialize local bins to zero (stride by local size).  
  2. Iterate over input bytes in strides of global size, atomically increment local bin.  
  3. Copy local bins to global `partial` buffer at offset `group_id * BINS`.  
- **histogram_reduce**  
  1. For each global work‑item representing a bin, sum corresponding entries across all `group_count` partial histograms.  
  2. Store the sum in the final `histogram`.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.  
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction to final histogram.

#### Constants and configuration
- `#define BINS 256` – number of histogram bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.

#### Exceptions / Errors
- None explicitly handled; out‑of‑bounds checks performed in `histogram_reduce` (`if (bin >= BINS) return;`).
