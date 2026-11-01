### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:59:59Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the occurrences of each byte value in an image or byte stream using a two-pass GPU histogram.

#### Purpose
- Builds a 256-bin byte histogram by accumulating per-work-group counts and reducing them into final counts.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: `partial` per-group histograms and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin result to `histogram`.

#### Core logic
- `histogram_partial` initializes `local_bins` cooperatively, synchronizes, then each work-item strides through input indices by global work size and atomically increments the bin for each byte.
- After a local-memory barrier, work-items cooperatively copy the local histogram to `partial + group_id * BINS`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all work-groups, and writes the total to `histogram`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — sum per-group histograms.
- Internal helpers: none.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
