### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:12:33Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts the frequency of each byte value in an image or byte stream using per-work-group histograms and a final reduction.

#### Purpose
- Builds a 256-bin byte histogram in two OpenCL passes.
- Uses work-group-local counts to produce partial histograms, then combines them per bin.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` zeroes `local_bins` cooperatively across work-items and synchronizes.
- Work-items stride over input indices by global work size; each byte increments its corresponding local bin atomically.
- After a local-memory barrier, work-items copy the local counts to the work-group’s region in `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across `group_count` partial histograms, and stores the total.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts by bin.

#### Constants and configuration
- `BINS=256`.

#### Dependencies
- OpenCL types: `uchar`, `uint`.
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
