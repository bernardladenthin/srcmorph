### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:55:13Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of arbitrary byte data using two OpenCL kernel passes.

#### Purpose
- Produce a frequency histogram of byte values.
- Support large buffers or image data via GPU acceleration.

#### Responsibilities
- **Partial histogram generation** – per‑work‑group accumulation of byte counts.
- **Global reduction** – summing per‑group partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial` kernel – scans input, atomically updates local bins, writes per‑group partials.
- `histogram_reduce` kernel – aggregates partials across groups into the final histogram array.
- Constant `BINS` (256) – defines histogram size.

#### Data flow
1. **Input**: `__global const uchar *input`, `uint length`.
2. `histogram_partial` writes per‑group partial histograms to `__global uint *partial`.
3. `histogram_reduce` reads `partial`, sums across groups, outputs final `__global uint *histogram`.

#### Dependencies
- OpenCL runtime functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Local memory buffer `__local uint *local_bins` sized to `BINS`.

#### Cross-cutting
- Two‑pass reduction pattern: local accumulation → global aggregation.
- Atomic operations for thread‑safe bin updates.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
