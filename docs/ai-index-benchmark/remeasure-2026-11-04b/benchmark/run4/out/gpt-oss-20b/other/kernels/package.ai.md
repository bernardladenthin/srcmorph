### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:52:47Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or generic byte data.
- Perform two‑pass reduction: local partial histograms then global sum.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel creates per‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- `#define BINS 256` – constant defining histogram size.

#### Data flow
1. Host copies input byte stream to device global memory.
2. `histogram_partial` runs: each work‑group builds a local histogram, writes it to `partial`.
3. `histogram_reduce` runs: each work‑item sums a specific bin across all partial histograms, writes the result to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.

#### Cross-cutting
- Two‑stage reduction pattern (local partials → global sum).
- Atomic increments for thread‑safe bin updates.
- Fixed 256‑bin configuration via macro.
