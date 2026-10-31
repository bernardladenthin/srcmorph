### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:42:39Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data, suitable for image processing or generic stream analysis.

#### Purpose
- Compute byte histograms efficiently on GPU.
- Split work into per‑group partial histograms and a final reduction.

#### Responsibilities
- **Partial histogram kernel**: per‑work‑group accumulation of byte counts into local memory, then spill to global buffer.
- **Reduce kernel**: aggregate partial results across groups into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. Host enqueues `histogram_partial` with input data, length, a global `partial` buffer, and local memory for bins.
2. Each work‑group writes its local histogram to `partial` at an offset based on `group_id`.
3. Host enqueues `histogram_reduce` with the `partial` buffer, number of groups, and a global `histogram` buffer.
4. Kernel sums across groups per bin and writes the final histogram.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation → global aggregation.
- Atomic increments used for thread‑safe bin updates.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
