### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:27Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Computes a 256-bin frequency histogram of byte values from an image or other byte stream.

#### Purpose
- Counts occurrences of each byte value using per-work-group partial histograms, then reduces them into a final histogram.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer of per-group counts and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, then each work-item processes input indices in a global-size stride and atomically increments the matching byte-value bin.
- After a local-memory barrier, work-items copy the local histogram to the group’s segment of `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all work-groups, and stores the result; work-items with bin indices outside `BINS` return.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts by bin.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`.
- OpenCL synchronization and atomics: `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- OpenCL types and address spaces: `uchar`, `uint`, `__global`, `__local`.

#### Exceptions / Errors
- No explicit error handling or error codes.
