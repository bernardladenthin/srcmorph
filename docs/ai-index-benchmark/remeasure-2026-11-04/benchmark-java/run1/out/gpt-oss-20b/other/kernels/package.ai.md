### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:18:49Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or arbitrary byte data.
- Perform two‑pass reduction: per‑group partial histograms then global reduction.

#### Responsibilities
- **Partial histogram kernel**: scans input, atomically updates per‑group local bins, writes partial results to global memory.
- **Global reduction kernel**: aggregates per‑group partial histograms into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- `#define BINS 256` – constant defining histogram size.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. `histogram_partial` produces `partial` array of size `group_count * BINS`.
3. `histogram_reduce` consumes `partial` and outputs final `histogram` array of 256 bins.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation followed by global aggregation.
- Atomic operations for thread‑safe bin updates.
- Use of local memory (`__local`) for per‑group bin storage.
