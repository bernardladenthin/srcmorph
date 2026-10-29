### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:53:58Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream on an OpenCL device using a two‑pass kernel strategy.

#### Purpose
- Build a byte histogram in two stages: per‑work‑group accumulation and global reduction.

#### Responsibilities
- **Partial histogram kernel**: aggregate local histograms per work‑group.
- **Reduce kernel**: sum local histograms into a global 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group histogram accumulation.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global histogram reduction.

#### Data flow
`input` → `histogram_partial` writes per‑group `partial` → `histogram_reduce` reads `partial` and produces final `histogram`.

#### Dependencies
- OpenCL C 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.

#### Cross-cutting
- Two‑pass reduction pattern; local memory usage for per‑group histograms; atomic increments for thread safety.
