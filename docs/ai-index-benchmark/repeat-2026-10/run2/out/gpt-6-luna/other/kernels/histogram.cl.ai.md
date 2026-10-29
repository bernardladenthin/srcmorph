### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:50:52Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Builds per-work-group byte counts, then combines them into a complete histogram.
- Supports histogramming arbitrary byte buffers, including image data.

#### Type
- OpenCL C 1.2 kernel source; defines `BINS` as 256.

#### Input
- `histogram_partial`: input byte buffer and `length`; runtime work-group and global sizes determine how input indices are distributed.
- `histogram_reduce`: partial histograms and `group_count`.

#### Output
- `histogram_partial`: writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce`: writes the combined 256-bin result to `histogram`.

#### Core logic
- `histogram_partial` initializes local-memory bins cooperatively, then synchronizes the work-group.
- Work-items stride across the input by global size and atomically increment the local bin for each byte value.
- After a second local-memory barrier, work-items cooperatively copy local bins to the work-group’s slice of `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all work-groups.
- Reduction work-items with bin indices at or above 256 return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial histograms.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL constants: `CLK_LOCAL_MEM_FENCE`.
