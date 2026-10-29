### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:22:49Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin frequency histogram for image bytes or any byte stream using parallel work-group counts and a reduction pass.

#### Purpose
- Count occurrences of each byte value (`0`–`255`) in an input buffer.
- Use partial work-group histograms, then combine them into one histogram.

#### Type
- OpenCL C 1.2 kernel source; defines `BINS` as `256`.

#### Input
- `histogram_partial`: input byte buffer, byte `length`, partial-histogram output buffer, and per-work-group local memory for 256 counters.
- `histogram_reduce`: partial-histogram buffer and `group_count`.

#### Output
- `histogram_partial` writes 256 counters per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local counters across work-items and synchronizes the work-group.
- Each work-item processes input indices in strides of the global work size; `atomic_inc` updates the local bin for each byte.
- After synchronization, work-items copy local counts to the group’s region in `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all groups, and stores the result; work-items beyond bin 255 return.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count bytes per work-group
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL types: `uchar`, `uint`.

#### Exceptions / Errors
- No explicit error handling; buffers and launch dimensions must support the indexed input and partial histograms.
