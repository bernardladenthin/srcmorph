### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:03:36Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Perform two‑pass reduction: local partial histograms then global reduction.

#### Responsibilities
- **Partial histogram kernel**: scans input, atomically updates per‑work‑group local bins, writes partial results.
- **Reduce kernel**: aggregates partial histograms into final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction to final histogram.
- `#define BINS 256` – constant for bin count.

#### Data flow
1. `histogram_partial` reads `input`, updates `local_bins` atomically, copies to `partial`.
2. `histogram_reduce` reads `partial`, sums across `group_count` groups, writes to `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern.
- Atomic increments for thread‑safe bin updates.
- Local memory usage for per‑group partials.
