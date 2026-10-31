### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:55:44Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Generate a byte histogram for image or generic byte data.
- Perform two‑pass reduction: local partial histograms followed by global aggregation.

#### Responsibilities
- **Partial histogram kernel**: builds per‑work‑group local histograms from a byte stream.
- **Reduce kernel**: aggregates all partial histograms into a final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – counts bytes locally, writes partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – aggregates partial histograms into final result.
- `#define BINS 256` – constant for bin count.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates local bins, and writes each work‑group’s partial histogram to `partial`.
2. `histogram_reduce` reads all partial histograms, sums each bin across groups, and writes the final histogram to `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation → global reduction.
- Atomic operations for thread‑safe bin updates.
- Use of local memory (`__local`) for per‑group histograms.
