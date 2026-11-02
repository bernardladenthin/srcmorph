### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:49:28Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or generic byte data.
- Perform two‑pass reduction: local partial histograms then global reduction.

#### Responsibilities
- **Partial histogram kernel**: per‑work‑group accumulation of byte counts into local memory, then write to global partial array.
- **Reduce kernel**: aggregate per‑group partial histograms into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- `#define BINS 256` – constant for histogram size.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. **Partial pass**: each work‑item atomically increments a local bin for its assigned bytes; after barrier, local bins are written to `partial`.
3. **Reduce pass**: each work‑item sums the same bin across all partial histograms; result stored in `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer for partial histograms and a global buffer for the final histogram.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation followed by global reduction.
- Atomic operations used for thread‑safe bin updates.
- No explicit error handling; out‑of‑range bins are silently ignored during reduction.
