### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:48Z
- G: 
- A: 0.0.0
- X: file
---
> Computes the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Builds a 256-bin histogram of input bytes.
- Uses per-work-group partial histograms, then reduces them into final counts.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial`: input byte buffer `input`, byte count `length`, partial-histogram buffer `partial`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: partial-histogram buffer `partial` and work-group count `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin histogram to `histogram`.

#### Core logic
- `histogram_partial` distributes initialization of `local_bins` across work-items, then synchronizes the work-group.
- Work-items process input indices in a global-stride loop and atomically increment the local bin for each byte value.
- After another local-memory barrier, work-items write local counters to `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all `group_count` partial histograms.
- Reduction work-items return when their global ID is outside the 256-bin range.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — counts bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines partial counts.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory qualifiers and fence: `__global`, `__local`, `CLK_LOCAL_MEM_FENCE`.
- OpenCL types: `uchar`, `uint`.

#### Exceptions / Errors
- No explicit error handling.
