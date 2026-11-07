### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:11:48Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of an image or byte stream using two OpenCL kernel passes.

#### Purpose
- Parallel construction of a byte histogram for arbitrary‑length data.
- Two‑stage pipeline: per‑work‑group partial accumulation followed by global reduction.

#### Responsibilities
- **Partial histogram kernel**: local binning per work‑group, atomic updates, writes to global partial array.
- **Reduction kernel**: summing partial histograms into final 256‑bin result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group partial histogram kernel.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – global reduction kernel.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. **Input**: byte stream (`uchar *input`) and its length.
2. **Partial pass**: each work‑group processes a slice, atomically updates `local_bins`, writes to `partial[group_id * BINS]`.
3. **Reduction pass**: each work‑item sums across all `partial` entries for a specific bin, writes to `histogram[bin]`.

#### Dependencies
- OpenCL runtime functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer `partial` sized `group_count * BINS` and a final `histogram` buffer of 256 `uint`s.

#### Cross-cutting
- Uses atomic increments for thread‑safe local bin updates.
- Employs local memory (`__local uint *local_bins`) for intra‑group cooperation.
- No explicit error handling; kernels exit early if a bin index exceeds `BINS`.
