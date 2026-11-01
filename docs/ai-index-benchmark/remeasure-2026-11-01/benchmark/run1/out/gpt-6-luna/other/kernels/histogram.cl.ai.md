### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:22:32Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Builds a 256-bin byte-frequency histogram.
- Uses per-work-group partial histograms, then reduces them into final counts.

#### Type
- OpenCL C 1.2 kernel source.
- Defines two `__kernel` functions; no host-side code.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, writable `partial` buffer, and caller-provided `local_bins` local-memory buffer.
- `histogram_reduce`: read-only `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce` writes final counts for bins 0–255 to `histogram`.

#### Core logic
- `histogram_partial` initializes all 256 local counters cooperatively across work-items and synchronizes the work-group.
- Work-items iterate over input positions in global-stride steps; each byte increments its local bin with `atomic_inc`.
- After a local-memory barrier, work-items cooperatively copy local counters to the group’s segment of `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across `group_count` partial histograms.
- Reduction work-items with `bin >= BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count per-group byte frequencies.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial counts by bin.

#### Constants and configuration
- `BINS`: `256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory fence flag: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and qualifiers: `uchar`, `uint`, `__global`, `__local`.

#### Exceptions / Errors
- No explicit error handling or status codes.
