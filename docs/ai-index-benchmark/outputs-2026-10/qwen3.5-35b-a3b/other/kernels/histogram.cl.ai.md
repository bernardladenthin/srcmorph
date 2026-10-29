### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:36:56Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates 256-bin byte histogram of image or byte stream data via two-pass OpenCL reduction.

#### Purpose
*   Compute frequency distribution of byte values in parallel.
*   Aggregate partial histograms from work-groups into final counts.

#### Type
*   OpenCL C 1.2 kernel source file.
*   Defines two `__kernel` functions for parallel reduction.

#### Input
*   `input`: `__global const uchar *` source byte stream.
*   `length`: `uint` total number of bytes to process.
*   `partial`: `__global uint *` buffer for intermediate group histograms.
*   `group_count`: `uint` number of work-groups from Pass 1.
*   `local_bins`: `__local uint *` local memory for atomic counters.

#### Output
*   `partial`: Written by `histogram_partial` (group-specific 256-bin counts).
*   `histogram`: Written by `histogram_reduce` (final 256-bin counts).
*   Side effects: Atomic increments in `local_bins` memory.

#### Core logic
*   `histogram_partial`:
    *   Initialize `local_bins` array to zero per work-group.
    *   Iterate input slice using global ID stride (`i += gsize`).
    *   Atomically increment `local_bins` at index `input[i]`.
    *   Copy `local_bins` to `partial` buffer offset by `get_group_id() * BINS`.
*   `histogram_reduce`:
    *   Assign one work-item per bin (0 to 255).
    *   Sum `partial` values for specific bin across all `group_count` groups.
    *   Write total sum to `histogram[bin]`.

#### Public API
*   `histogram_partial(input, length, partial, local_bins) -> void`
*   `histogram_reduce(partial, group_count, histogram) -> void`

#### Dependencies
*   OpenCL C 1.2 runtime.
*   `atomic_inc` function.
*   `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`.
*   `barrier` with `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
*   `histogram_reduce` skips work if `bin >= BINS`.
*   No explicit exception handling (kernel execution model).
