### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:34:35Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.

#### Purpose
- Build a byte histogram for image or generic byte data.
- Split work into local partial histograms and a global reduction.

#### Responsibilities
- **Partial histogram generation** – `histogram_partial` kernel creates per‑group partial histograms.
- **Global reduction** – `histogram_reduce` kernel aggregates partials into the final 256‑bin histogram.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – computes per‑group partial histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – reduces partial histograms into the final result.
- `#define BINS 256` – constant for histogram bin count.

#### Data flow
1. Host enqueues `histogram_partial` with input data, length, a global `partial` buffer, and local memory for bins.
2. Each work‑group builds a local histogram, writes it to `partial` at an offset based on `group_id`.
3. Host enqueues `histogram_reduce` with the `partial` buffer, number of groups, and a global `histogram` buffer.
4. Kernel sums each bin across all partial histograms and writes the final counts to `histogram`.

#### Dependencies
- OpenCL 1.2 built‑ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer sized `group_count * BINS` for partial results and a 256‑element global buffer for the final histogram.

#### Cross-cutting
- Uses atomic increments for thread‑safe bin updates.
- Relies on local memory for per‑group histograms to reduce global memory traffic.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
