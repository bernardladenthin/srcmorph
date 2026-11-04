### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:41Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256‑bin byte histogram of an input byte stream using two OpenCL kernel passes.

#### Purpose
- Build a 256‑bin histogram of byte values from a global byte buffer.
- Split work into per‑group partial accumulation and a global reduction.

#### Responsibilities
- **Partial accumulation**: each work‑group builds a local histogram of its assigned bytes and writes it to a global partial buffer.
- **Global reduction**: each work‑item aggregates a single histogram bin across all partial histograms to produce the final result.

#### Key units
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins)` – per‑group kernel that fills `partial` with local histograms.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram)` – kernel that sums across `partial` to produce the final 256‑bin histogram.
- Constant `#define BINS 256` – number of histogram bins.

#### Data flow
1. **Input**: global byte buffer `input` and its `length`.
2. **Partial pass**: each work‑group processes a slice of `input`, atomically updates `local_bins`, then writes its local histogram to `partial`.
3. **Reduction pass**: each work‑item sums the same bin across all entries in `partial` and writes the total to `histogram`.

#### Dependencies
- OpenCL runtime functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Requires a global buffer `partial` sized `group_count * BINS` and a global buffer `histogram` sized `BINS`.

#### Cross-cutting
- Uses atomic increments for thread‑safe bin updates.
- Synchronization via `barrier(CLK_LOCAL_MEM_FENCE)` to ensure local bins are fully populated before writing to global memory.
- Guard against out‑of‑range indices in the reduction kernel with `if (bin >= BINS)`.
