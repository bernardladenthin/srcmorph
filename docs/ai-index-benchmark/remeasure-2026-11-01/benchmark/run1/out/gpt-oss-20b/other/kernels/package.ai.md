### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:28:53Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream in two OpenCL kernel passes.

#### Purpose
- Builds a byte histogram for image or arbitrary byte data.
- Implements a two‑pass reduction: per‑group partial histograms followed by a global reduction.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel computes per‑group local histograms.
- **Global histogram reduction** – `histogram_reduce` kernel aggregates partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- `#define BINS 256` – constant defining histogram bin count.

#### Data flow
1. **Input**: byte array (`input`) and its length (`length`).
2. **Partial pass**: each work‑group writes its local bin counts into `partial`.
3. **Reduction pass**: sums all partial histograms into `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.
- Uses global and local memory buffers (`__global`, `__local`).

#### Cross-cutting
- Two‑pass reduction pattern across kernels.
- Atomic increments and local memory barriers for thread‑safe bin updates.
- Consistent use of the `BINS` constant for bounds checking.
