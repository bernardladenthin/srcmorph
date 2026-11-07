### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:56:40Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.

#### Purpose
- Generate a byte histogram for image or arbitrary byte data.
- Perform two‑pass reduction: per‑group partial histograms followed by global aggregation.

#### Responsibilities
- **Partial histogram kernel**: build per‑work‑group local histograms and write them to global memory.
- **Reduce kernel**: aggregate all partial histograms into a final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram builder.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction to final histogram.
- `#define BINS 256` – constant defining histogram size.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. **Partial pass**: each work‑group processes a slice of the input, atomically updates a local 256‑bin array, then writes the local bins to a global `partial` buffer.
3. **Reduce pass**: each work‑item sums the corresponding bin across all partial buffers and writes the total to the final `histogram` array.

#### Dependencies
- OpenCL C 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation → global aggregation.
- Atomic increments used for thread‑safe local bin updates.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
