### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:39Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Computes a 256-bin byte histogram, partitioning counts across work-groups before reducing them.
- Supports image data or any byte stream.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and caller-provided `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin histogram to `histogram`.

#### Core logic
- `histogram_partial` initializes local counters cooperatively, then synchronizes the work-group.
- Each work-item processes input indices in strides of the global work size and atomically increments the local counter for each byte value.
- After another local-memory barrier, work-items cooperatively copy local counters to the group’s slice of `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across `group_count` partial histograms, and stores the result in `histogram`; out-of-range bin IDs return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts per bin.

#### Constants and configuration
- `BINS = 256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory fences: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- `histogram_reduce` skips work-items whose bin index is `>= BINS`.
