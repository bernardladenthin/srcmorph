### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:41:54Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides OpenCL kernels for computing a 256‑bin histogram of byte data via two‑pass reduction.

#### Purpose
- Compute a byte histogram for image or arbitrary byte streams.
- Perform a two‑pass reduction: per‑group partial histograms followed by global aggregation.

#### Responsibilities
- **Partial histogram generation** – per‑group accumulation of byte counts using local memory and atomic increments.
- **Global reduction** – summing per‑group partial histograms into a final 256‑bin histogram.

#### Key units
- `histogram_partial` kernel – builds per‑group partial histograms (`__global const uchar *input`, `uint length`, `__global uint *partial`, `__local uint *local_bins`).
- `histogram_reduce` kernel – aggregates partial histograms into the final histogram (`__global const uint *partial`, `uint group_count`, `__global uint *histogram`).
- `#define BINS 256` – constant for the number of histogram bins.

#### Data flow
1. **Input**: byte stream (`uchar *input`) → `histogram_partial`.
2. **Partial stage**: each work‑group writes its local bin counts to `partial` buffer.
3. **Reduction stage**: `histogram_reduce` reads all partial histograms, sums per bin, writes final `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires `__global` and `__local` memory spaces for input, partial, and local bins.

#### Cross-cutting
- Use of local memory (`__local uint *local_bins`) for intra‑group accumulation.
- Atomic increments (`atomic_inc`) to avoid race conditions on local bins.
- Synchronization via `barrier(CLK_LOCAL_MEM_FENCE)` after local accumulation.
- One‑work‑item-per‑bin strategy in `histogram_reduce` for efficient global summation.
