### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts the frequency of each byte value in an image or byte stream using parallel work-group histograms and a reduction pass.

#### Purpose
- Computes a 256-bin byte histogram in two OpenCL passes.
- Produces per-work-group partial counts, then combines them into final bin totals.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: byte buffer `input`, element count `length`, output buffer `partial`, and work-group local-memory buffer `local_bins`.
- `histogram_reduce`: partial histogram buffer `partial` and number of work-groups `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final `BINS` counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and counts input bytes using strided global-ID traversal and local atomic increments.
- After a second local-memory barrier, work-items cooperatively write the local counts to the work-group’s partial-histogram region.
- `histogram_reduce` assigns one work-item to each bin, sums that bin across all work-groups, and stores the total; IDs outside the bin range return immediately.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts per bin.

#### Constants and configuration
- `BINS=256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory fence flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling.
