### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:14Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Builds a 256-bin histogram by accumulating per-work-group counts and reducing them into final bin totals.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: byte input buffer, element count `length`, partial-histogram output buffer, and caller-provided local-memory bins.
- `histogram_reduce`: partial-histogram buffer and work-group count `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, then synchronizes the work-group.
- Each work-item processes input indices starting at its global ID and stepping by the global work size; it atomically increments the local bin for each byte value.
- After a second local-memory barrier, work-items cooperatively copy local bins to `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all `group_count` partial histograms, and stores the total; IDs at or above `BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — build per-group histograms.
- `histogram_reduce(partial, group_count, histogram) -> void` — sum partial histograms.
- Internal helpers: none.

#### Constants and configuration
- `BINS = 256` — number of byte-value histogram bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory-space and synchronization identifiers: `__global`, `__local`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
