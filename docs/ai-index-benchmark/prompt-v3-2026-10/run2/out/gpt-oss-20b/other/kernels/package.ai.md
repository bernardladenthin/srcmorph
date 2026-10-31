### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:21:41Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of a byte stream using two OpenCL kernel passes.

#### Purpose
- Build a per‑byte frequency histogram from a byte stream.
- Split the computation into a local accumulation pass and a global reduction pass.

#### Responsibilities
- **Local accumulation**: each work‑group builds a partial histogram in local memory and writes it to global memory.
- **Global reduction**: each work‑item sums a specific histogram bin across all partial histograms to produce the final result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – accumulates per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final 256‑bin histogram.
- `#define BINS 256` – constant defining the number of histogram bins.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates local bins, and writes each group’s partial histogram to `partial`.
2. `histogram_reduce` reads all partial histograms, sums each bin across groups, and writes the final histogram to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Two‑pass kernel design: local accumulation followed by global reduction.
- Atomic operations for thread‑safe bin updates.
- Barrier synchronization to ensure local memory is fully populated before writing partial results.
