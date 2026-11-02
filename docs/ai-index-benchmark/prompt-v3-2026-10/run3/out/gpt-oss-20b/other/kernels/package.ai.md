### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:46:23Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL passes.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Perform two‑pass reduction: local partial histograms then global aggregation.

#### Responsibilities
- **Partial histogram kernel**: scans input, atomically updates per‑group local bins, writes partial results.
- **Reduce kernel**: sums partial counts per bin across all groups to produce final histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – builds per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – aggregates partials into final 256‑bin histogram.
- `#define BINS 256` – constant for histogram size.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. **Partial pass**: each work‑group processes a strided segment, updates local bins, writes to `partial`.
3. **Reduce pass**: each work‑item sums a specific bin across all groups, writes to `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑stage reduction pattern (local then global).
- Atomic increments for thread‑safe bin updates.
- Use of local memory (`__local`) for per‑group bin storage.
