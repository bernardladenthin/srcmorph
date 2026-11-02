### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:33:00Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Perform two‑pass reduction: local partial histograms then global sum.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel creates per‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials to final histogram.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. Host copies input byte stream to device global memory.
2. `histogram_partial` runs: each work‑group builds a local 256‑bin array, atomically increments bins, then writes the local array to the global `partial` buffer.
3. `histogram_reduce` runs: each work‑item sums a specific bin across all partial histograms and writes the result to the global `histogram` buffer.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer for partial histograms sized `group_count * BINS`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation followed by global reduction.
- Atomic operations used for thread‑safe bin updates.
- No explicit error handling; kernels guard against out‑of‑range bins.
