### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:14Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Provides an OpenCL implementation for computing a 256‑bin histogram of arbitrary byte data using a two‑pass reduction strategy.

#### Purpose
- Compute byte histograms for image or generic byte streams.
- Split work into per‑group partial histograms and a global reduction.

#### Responsibilities
- **Partial histogram kernel**: build per‑work‑group local histograms and write them to a global buffer.
- **Global reduction kernel**: aggregate all partial histograms into a final 256‑bin result.

#### Key units
- `histogram_partial` – `__kernel` that fills a per‑group local histogram and copies it to a global `partial` buffer.
- `histogram_reduce` – `__kernel` that sums across all partial histograms to produce the final `histogram` buffer.
- `BINS` constant (256) defines histogram size.

#### Data flow
1. Host enqueues `histogram_partial` with input byte buffer, length, and a global `partial` buffer.
2. Each work‑group processes a strided segment of the input, atomically updates local bins, then writes its local histogram to `partial`.
3. Host enqueues `histogram_reduce` with the `partial` buffer, number of groups, and a global `histogram` buffer.
4. Each work‑item sums a single bin across all partial histograms and writes the result to `histogram`.

#### Dependencies
- OpenCL runtime functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer sized `group_count * BINS` for partial results.

#### Cross-cutting
- Uses atomic increments for thread‑safe bin updates.
- Local memory (`__local uint *local_bins`) is zeroed per work‑group before accumulation.
- Simple bounds check in `histogram_reduce` to guard against out‑of‑range bin indices.
