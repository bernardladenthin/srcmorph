### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:22Z
- G: 
- A: 0.0.0
- X: file
---
> Counts occurrences of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Build per-work-group partial byte histograms, then combine them into one complete histogram.
- OpenCL C 1.2 implementation for image or arbitrary byte-stream data.

#### Type
- OpenCL C kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final `BINS` counters to `histogram`.

#### Core logic
- `histogram_partial` initializes local-memory bins cooperatively, then synchronizes the work-group.
- Each work-item processes input indices starting at its global ID, stepping by global size; it atomically increments the local bin indexed by each byte value.
- After a local-memory barrier, work-items cooperatively copy the local bins to the work-group’s segment of `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all `group_count` partial histograms.
- Reduction work-items with bin IDs at or above `BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — counts bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines partial histograms.

#### Constants and configuration
- `BINS = 256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory flags: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
