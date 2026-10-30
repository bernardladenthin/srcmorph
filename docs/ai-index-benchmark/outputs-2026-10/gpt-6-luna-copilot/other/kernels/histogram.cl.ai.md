### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:36:42Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Builds per-work-group partial byte counts, then combines them into a final histogram.

#### Type
- OpenCL C 1.2 kernel source; defines `BINS` as 256.

#### Input
- `histogram_partial`: byte buffer, `length`, and local-memory bins.
- `histogram_reduce`: partial histogram buffer and `group_count`.

#### Output
- `histogram_partial`: writes 256 counters per work-group to `partial`.
- `histogram_reduce`: writes 256 summed counters to `histogram`.

#### Core logic
- `histogram_partial` zeroes local bins, distributes input indices across work-items, atomically counts each byte value, then writes the group’s bins to `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all work-groups.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — counts group-local byte frequencies.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines partial histograms.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.

#### Exceptions / Errors
- `histogram_reduce` returns without writing when the global ID is outside the 256-bin range.
