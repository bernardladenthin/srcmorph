### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:11:58Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides a two‑pass OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data, avoiding global atomics by first generating per‑group partial histograms and then reducing them into the final result.

#### Purpose
- Compute byte frequency histograms for images or generic byte streams.
- Avoid global atomics via a two‑pass reduction strategy.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel builds per‑work‑group local histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates per‑group partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – writes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – sums partials into the final histogram.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically increments local bin counters, and writes each work‑group’s local histogram to the global `partial` array.
2. `histogram_reduce` reads all partial histograms, sums each bin across groups, and writes the final 256‑bin histogram to `histogram`.

#### Dependencies
- OpenCL C 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global `partial` buffer sized `group_count * BINS` and a global `histogram` buffer of size `BINS`.

#### Cross-cutting
- Uses local memory (`__local uint *local_bins`) for per‑group accumulation.
- Employs atomic increments within local memory to avoid race conditions.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
