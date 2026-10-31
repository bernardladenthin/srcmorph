### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:43Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass GPU histogram.

#### Purpose
- Builds a 256-bin histogram by accumulating per-work-group counts, then reducing them into final bin totals.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: byte buffer `input`, element `length`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: partial histogram buffer `partial` and number of work-groups `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and distributes input indices across work-items using a global-stride loop.
- Each input byte atomically increments its matching local bin; after another work-group barrier, work-items copy local bins to the group’s slice in `partial`.
- `histogram_reduce` assigns one work-item to each bin and sums that bin across all work-group partial histograms.
- Reduction work-items with a bin index of 256 or greater return without writing.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — combine partial counts per bin.

#### Constants and configuration
- `BINS = 256` — number of byte-value histogram bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory fence flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
