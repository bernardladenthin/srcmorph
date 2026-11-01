### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:01:48Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Builds per-work-group byte counts, then combines them into a complete histogram.
- Supports any byte stream represented as `uchar` input.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer and `length`; local-memory buffer `local_bins`.
- `histogram_reduce`: `partial` per-group histograms and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final 256 counters to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and atomically counts input bytes across global work-items.
- After another local-memory barrier, each work-group writes its local counts at `partial + group_id * BINS`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all work-groups.
- Reduction work-items with `bin >= BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial histograms.

#### Constants and configuration
- `BINS = 256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory flags: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
