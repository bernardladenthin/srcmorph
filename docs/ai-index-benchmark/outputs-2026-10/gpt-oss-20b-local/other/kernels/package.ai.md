### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:21:53Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Generate a byte‑frequency histogram for image or arbitrary byte data.
- Perform reduction in two stages: local partial histograms then global aggregation.

#### Responsibilities
- **Partial accumulation**: per‑work‑group local histograms, atomically updated.
- **Global reduction**: summing partial histograms into final 256‑bin result.

#### Key units
- `histogram_partial` (`__kernel`): scans input, updates local bins, writes per‑group partials to global memory.
- `histogram_reduce` (`__kernel`): aggregates partial histograms across groups, produces final histogram.

#### Data flow
1. `histogram_partial` reads `input` bytes, updates `local_bins`, writes to `partial` buffer.
2. `histogram_reduce` reads `partial`, sums across `group_count` for each bin, writes to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local partials → global sum.
- Atomic operations for thread‑safe bin updates.
- No explicit error handling; relies on correct kernel launch configuration.
