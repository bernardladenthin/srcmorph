### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:57:08Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of byte data, suitable for image processing or generic stream analysis.

#### Purpose
- Compute byte histograms efficiently on GPU.
- Split computation into per‑group partials and a global reduction.

#### Responsibilities
- **Partial histogram kernel**: builds per‑work‑group local histograms and writes them to global memory.
- **Reduction kernel**: aggregates all partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – produces per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – sums partials into the final histogram.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates local bins, and copies each group’s local histogram to the `partial` buffer.
2. `histogram_reduce` reads all partial histograms, sums each of the 256 bins across groups, and writes the final counts to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses local memory for per‑group accumulation to reduce global memory traffic.
- Atomic increments ensure correct counting across work‑items.
- No explicit error handling; assumes valid pointers and sizes.
