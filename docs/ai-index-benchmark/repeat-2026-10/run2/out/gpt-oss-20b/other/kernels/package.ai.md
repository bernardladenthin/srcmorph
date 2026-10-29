### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:17:41Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or generic byte data.
- Split work into per‑group partial histograms and a global reduction.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel creates per‑work‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- Constant `BINS = 256`.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates local bins, and writes each work‑group’s partial histogram to `partial`.
2. `histogram_reduce` reads all partial histograms, sums each bin across groups, and writes the final histogram to `histogram`.

#### Dependencies
- OpenCL built‑in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Input: `__global const uchar *input`; Output: `__global uint *histogram`.

#### Cross-cutting
- Two‑pass kernel design: local accumulation followed by global reduction.
- Use of atomic operations and local memory for efficient parallel histogramming.
- Fixed bin count (`BINS = 256`) ensures deterministic array sizes.
