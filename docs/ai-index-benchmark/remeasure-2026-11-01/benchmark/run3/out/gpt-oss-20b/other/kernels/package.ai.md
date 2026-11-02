### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:26Z
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
- **Partial histogram generation** – `histogram_partial` kernel accumulates per‑work‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel sums all partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- `#define BINS 256` – constant for histogram bin count.

#### Data flow
1. **Input**: byte stream (`input`) and its length.  
2. `histogram_partial` writes per‑group partial histograms to `partial`.  
3. `histogram_reduce` reads `partial`, aggregates across groups, and writes the final histogram to `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation followed by global summation.  
- Atomic operations (`atomic_inc`) used for thread‑safe bin updates.  
- No explicit error handling; bounds checks guard against out‑of‑range indices.
