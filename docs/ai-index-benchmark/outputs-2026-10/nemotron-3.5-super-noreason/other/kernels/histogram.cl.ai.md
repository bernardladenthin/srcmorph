### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:03:14Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass OpenCL reduction.

#### Purpose
- Counts byte value frequencies across an input buffer in parallel.
- Produces a final 256-element histogram by reducing partial per-work-group histograms.

#### Type
OpenCL C 1.2 kernel file containing two `__kernel` functions.

#### Input
- `histogram_partial`: `__global const uchar *input` byte stream, `const uint length` number of bytes, `__global uint *partial` output buffer for partial histograms, `__local uint *local_bins` per-work-group local memory for BINS counters.
- `histogram_reduce`: `__global const uint *partial` partial histogram buffer, `const uint group_count` number of work-groups from pass 1, `__global uint *histogram` final output histogram.

#### Output
- `histogram_partial`: writes one partial histogram of BINS counters per work-group into `partial[group_id * BINS + bin]`.
- `histogram_reduce`: writes the summed 256-bin histogram into `histogram[bin]`.

#### Core logic
- `histogram_partial` initializes `local_bins` to zero across work-items in a work-group.
- Each work-item atomically increments `local_bins[input[i]]` for its assigned input elements.
- After a local memory fence, each work-item copies its assigned bins from `local_bins` to the global `partial` buffer.
- `histogram_reduce` assigns one work-item per bin (0..255).
- Each work-item sums `partial[g * BINS + bin]` across all `group_count` groups and stores the result in `histogram[bin]`.
- Work-items with `bin >= BINS` exit early.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` counts bytes into a partial histogram per work-group.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` sums partial histograms into the final 256-bin histogram.

#### Dependencies
- OpenCL C 1.2 builtins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`.
- Macro `BINS` defined as 256.

#### Exceptions / Errors
- No explicit error handling; `histogram_reduce` guards out-of-range bins with an early return.
