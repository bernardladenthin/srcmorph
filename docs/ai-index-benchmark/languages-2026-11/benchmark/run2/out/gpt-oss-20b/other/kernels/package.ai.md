### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:43:41Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of byte streams via two‑pass OpenCL kernels.

#### Purpose
- Builds a byte frequency histogram for image or arbitrary byte data.
- Implements a two‑pass reduction: per‑group partial histograms followed by global aggregation.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel computes per‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- `#define BINS 256` – constant defining the number of histogram bins.

#### Data flow
1. **Input**: byte stream (`__global const uchar *input`) and its length.
2. **Partial pass**: each work‑group writes a local histogram to the `partial` buffer.
3. **Reduction pass**: each work‑item sums a specific bin across all partial histograms, producing the final `histogram` buffer.

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`.
- Synchronization primitives: `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Memory qualifiers: `__global`, `__local`.

#### Cross-cutting
- Two‑pass reduction pattern with local memory aggregation.
- Atomic increments for thread‑safe bin updates.
- Barrier synchronization to ensure local histograms are fully populated before copying to global memory.
