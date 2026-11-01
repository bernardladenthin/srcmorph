### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:21:25Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two‑pass OpenCL kernels.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Perform reduction in two stages: per‑group partial histograms then global aggregation.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel accumulates local counts per work‑group.
- **Global reduction** – `histogram_reduce` kernel sums partial results into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final histogram kernel.
- `#define BINS 256` – constant defining histogram size.

#### Data flow
1. **Input**: byte array `input` of length `length`.
2. `histogram_partial` writes per‑group partial histograms to `partial`.
3. `histogram_reduce` reads `partial`, aggregates across `group_count`, writes final `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation → global summation.
- Atomic operations for thread‑safe bin updates.
- No explicit error handling; kernels exit early if bin index exceeds `BINS`.
