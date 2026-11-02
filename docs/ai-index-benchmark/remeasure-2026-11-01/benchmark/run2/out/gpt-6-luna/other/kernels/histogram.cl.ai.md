### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:44:03Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Computes per-byte frequencies with OpenCL work-groups, then combines their partial histograms.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` functions.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and dynamically supplied `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.
- Both kernels rely on global and local work sizes set by the launch configuration.

#### Output
- `histogram_partial` writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local-memory bins cooperatively, synchronizes the work-group, and distributes input indices across global work-items using a global-size stride.
- Each input byte increments its corresponding local bin atomically; after synchronization, work-items cooperatively copy local bins to the work-group’s segment of `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across `group_count` partial histograms.
- Reduction work-items with bin indices at or above `BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count input bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts by bin.

#### Constants and configuration
- `BINS` = `256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and address spaces: `uchar`, `uint`, `__global`, `__local`.

#### Exceptions / Errors
- No explicit error handling or status codes.
