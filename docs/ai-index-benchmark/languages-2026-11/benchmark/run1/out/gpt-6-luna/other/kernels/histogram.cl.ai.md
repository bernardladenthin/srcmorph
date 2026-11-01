### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:01Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Builds per-work-group partial byte histograms, then reduces them into one histogram.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer and `length`; runtime local-memory buffer `local_bins`.
- `histogram_reduce`: `partial` histogram buffer and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final `BINS` counters to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and distributes input indices across global work-items with a global-size stride.
- Each input byte increments its corresponding local bin using `atomic_inc`; after a local-memory barrier, work-items write the group’s partial histogram.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all work-groups.
- Reduction work-items whose bin index is at least `BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — Count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — Sum partial counts per bin.

#### Constants and configuration
- `BINS`: `256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory fence flag: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and qualifiers: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Exceptions / Errors
- No explicit error handling or status codes.
