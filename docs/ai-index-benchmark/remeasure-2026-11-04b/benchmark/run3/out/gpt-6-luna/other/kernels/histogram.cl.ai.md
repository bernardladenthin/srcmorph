### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:07:04Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Counts occurrences of each byte value in an image or byte stream and produces a 256-bin histogram.

#### Purpose
- Computes a byte-frequency histogram in two passes: per-work-group partial counts, then per-bin reduction.
- Supports counting byte values from 0 through 255.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial(input, length, partial, local_bins)` reads `input` as `length` bytes and uses work-group local memory for bin counts.
- `histogram_reduce(partial, group_count, histogram)` reads `group_count` partial histograms.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final `BINS` counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and distributes input indices across global work-items using a stride of `gsize`.
- Each work-item atomically increments the local bin for its input byte; after synchronization, work-items copy local counts to the group’s segment in `partial`.
- `histogram_reduce` assigns one work-item to each bin, sums that bin across all partial histograms, and writes the total; work-items with `bin >= BINS` return.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — combine partial counts per bin.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling.
