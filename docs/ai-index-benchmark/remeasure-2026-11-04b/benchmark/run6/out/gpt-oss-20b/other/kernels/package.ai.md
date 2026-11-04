### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:58:10Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Generate a byte histogram for image or generic byte data.
- Split computation into local partial counting and global reduction.

#### Responsibilities
- **Partial counting**: per‑work‑group accumulation of byte frequencies.
- **Global reduction**: summing per‑group partials into final histogram.

#### Key units
- `histogram_partial` – OpenCL kernel that builds per‑group partial histograms.
- `histogram_reduce` – OpenCL kernel that reduces partial histograms into the final 256‑bin result.
- `BINS` – constant defining histogram size (256).

#### Data flow
1. **Input**: `__global const uchar *input`, `uint length`.
2. `histogram_partial` writes per‑group partial counts to `__global uint *partial`.
3. `histogram_reduce` reads `partial`, aggregates across `group_count`, writes final `__global uint *histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses local memory (`__local uint *local_bins`) for intra‑group accumulation.
- Atomic increments guard concurrent updates to local bins.
- Barrier synchronization ensures all work‑items finish local accumulation before copying to global partial buffer.
