### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:06:23Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes, avoiding global atomics through per‑group partial reductions.

#### Purpose
- Build a byte frequency histogram for image or generic byte data.
- Perform the computation in two passes to keep local memory usage low.

#### Responsibilities
- **Partial histogram generation**: `histogram_partial` scans input, updates per‑work‑group local bins, and writes them to a global partial buffer.
- **Global reduction**: `histogram_reduce` aggregates all partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – final reduction kernel.
- `#define BINS 256` – constant defining histogram size.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. **Partial pass**: each work‑group processes a strided segment, updates local bins, writes to `partial`.
3. **Reduce pass**: one work‑item per bin sums across all groups, writes to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses local memory (`__local uint *local_bins`) for per‑group accumulation.
- Avoids global atomics by performing a two‑stage reduction.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
