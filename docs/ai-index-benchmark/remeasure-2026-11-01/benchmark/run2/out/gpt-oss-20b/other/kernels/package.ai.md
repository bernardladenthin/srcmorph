### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:48:17Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin histogram of a byte stream using two OpenCL passes.

#### Purpose
- Build a byte histogram for images or arbitrary byte streams.
- Perform reduction in two stages: per‑group partials then global aggregation.

#### Responsibilities
- **Partial histogram kernel**: generate per‑work‑group local histograms from the input stream.
- **Reduce kernel**: aggregate all partial histograms into the final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partials into the final histogram.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. `histogram_partial` reads the input byte stream, atomically updates a local 256‑bin array, then writes each group’s partial histogram to the global `partial` buffer.
2. `histogram_reduce` iterates over each bin, summing the corresponding counts from all partial histograms, and writes the summed counts to the global `histogram` buffer.

#### Dependencies
- OpenCL built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer sized `group_count × BINS` for partials and a 256‑element global buffer for the final histogram.

#### Cross-cutting
- Two‑pass reduction pattern: local partials followed by global aggregation.
- Atomic increments and local memory usage for efficient per‑group histogram construction.
- Minimal error handling; bounds check in `histogram_reduce` guards against excess global IDs.
