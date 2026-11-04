### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:37:43Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts occurrences of each byte value in an image or byte stream and combines per-work-group counts into a 256-bin histogram.

#### Purpose
- Computes a byte-value frequency histogram in two passes: work-group partial counts, then bin-wise reduction.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: `partial` counts buffer and `group_count`.

#### Output
- `histogram_partial` writes 256 counters per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, synchronizes the work-group, and has each work-item stride through input indices using global size.
- Input bytes select local bins, incremented with `atomic_inc`; after synchronization, work-items copy local counts to the group’s segment of `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across `group_count` partial histograms, and stores the result.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts per bin.

#### Constants and configuration
- `BINS = 256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- `histogram_reduce` returns without writing when the global ID is outside `BINS`.
