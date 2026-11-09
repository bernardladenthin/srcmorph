### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:55:25Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Generate a byte‑frequency histogram for image or arbitrary byte data.
- Split computation into a per‑group partial pass and a global reduction pass.

#### Responsibilities
- **Partial histogram kernel**: accumulate per‑work‑group local counts, write to global buffer.
- **Reduction kernel**: sum partial counts across groups to produce final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- `#define BINS 256` – constant for histogram bin count.

#### Data flow
1. **Input**: byte array `input` and its `length`.
2. `histogram_partial` reads `input`, atomically updates `local_bins`, copies to `partial` at `group_id * BINS`.
3. `histogram_reduce` reads `partial`, sums each bin across all groups, writes result to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier(CLK_LOCAL_MEM_FENCE)`.

#### Cross-cutting
- Two‑pass pattern: local aggregation followed by global reduction.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
