### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:13Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an input stream or image on the GPU in two kernel passes.

#### Purpose
- Counts occurrences of each byte value (0-255) in a buffer, such as image pixel data.
- Parallelizes counting via per-work-group partial histograms, then reduces them into a final histogram.

#### Type
- OpenCL C 1.2 source file, kernel implementation (no header).
- Two `__kernel` functions: `histogram_partial`, `histogram_reduce`.
- Macro: `BINS` = 256.

#### Input
- `histogram_partial`:
  - `__global const uchar *input`: byte stream or image data.
  - `const uint length`: number of bytes in `input`.
  - `__global uint *partial`: output buffer of size `numGroups * BINS`.
  - `__local uint *local_bins`: local memory of `BINS` counters.
  - Work-item IDs: `get_local_id(0)`, `get_local_size(0)`, `get_global_id(0)`, `get_global_size(0)`, `get_group_id(0)`.
- `histogram_reduce`:
  - `__global const uint *partial`: partial histograms from pass 1.
  - `const uint group_count`: number of work-groups in pass 1.
  - Work-item ID: `get_global_id(0)`, one per bin.

#### Output
- `histogram_partial`: writes `BINS` counters per work-group to `partial[group_id * BINS + bin]`.
- `histogram_reduce`: writes `histogram[bin]` = sum of `partial[g * BINS + bin]` over all groups `g`.
- No return values; results are stored in global buffers.

#### Core logic
- Pass 1 (`histogram_partial`):
  - Zero `local_bins` cooperatively across the work-group using stride `lsize`.
  - Barrier with `CLK_LOCAL_MEM_FENCE`.
  - Grid-stride loop over `input` (stride `gsize`); `atomic_inc` on `local_bins[input[i]]`.
  - Barrier with `CLK_LOCAL_MEM_FENCE`.
  - Copy `local_bins` to the group's slice `partial + group_id * BINS` cooperatively.
- Pass 2 (`histogram_reduce`):
  - One work-item per bin; out-of-range IDs (`bin >= BINS`) return early.
  - Sum `partial[g * BINS + bin]` for `g` in `[0, group_count)`.
  - Store result in `histogram[bin]`.
- Pass 1 and pass 2 must run as separate kernel launches to guarantee global ordering.

#### Public API
- `histogram_partial(__global const uchar* input, uint length, __global uint* partial, __local uint* local_bins) -> void`: per-group partial byte histogram.
- `histogram_reduce(__global const uint* partial, uint group_count, __global uint* histogram) -> void`: sums partial histograms into final bins.

#### Dependencies
- OpenCL C built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`.
- Macro: `BINS`.

#### Exceptions / Errors
- No explicit error handling.
- `histogram_reduce` guards against out-of-range bins via early return.
- `histogram_partial` assumes `local_bins` holds at least `BINS` `uint` entries.
- `partial` must hold `numGroups * BINS` entries; no bounds check.
