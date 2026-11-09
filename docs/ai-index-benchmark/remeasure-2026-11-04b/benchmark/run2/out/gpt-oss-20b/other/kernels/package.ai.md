### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:03:25Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or generic byte data.
- Split work into local partial histograms and a global reduction.

#### Responsibilities
- **Partial histogram generation**: per‑work‑group accumulation of byte counts.
- **Global reduction**: summing per‑group partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- Constant `BINS = 256` – number of histogram bins.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates local bins, then writes each group’s local histogram to the global `partial` array.
2. `histogram_reduce` iterates over each bin, summing the corresponding entries across all groups in `partial`, and writes the totals to the final `histogram`.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass design: local accumulation for memory locality, followed by a global reduction.
- Atomic operations and barriers ensure correct parallel updates within work‑groups.
