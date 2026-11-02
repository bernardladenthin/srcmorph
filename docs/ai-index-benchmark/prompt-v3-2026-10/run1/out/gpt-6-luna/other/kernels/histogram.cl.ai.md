### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:50:30Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Builds a 256-bin histogram of input bytes.
- Uses per-work-group partial histograms, then reduces them into final counts.

#### Type
- OpenCL C 1.2 kernel source.
- Defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes final byte counts to `histogram[0..255]`.

#### Core logic
- `histogram_partial` initializes 256 local counters cooperatively, then synchronizes the work-group.
- Work-items stride through input indices using global IDs and size; `atomic_inc` counts each byte in local memory.
- After a second local-memory barrier, work-items copy local counters to the work-group’s slice of `partial`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all `group_count` partial histograms.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count work-group byte frequencies.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial histograms.
- Internal helpers: none.

#### Constants and configuration
- `BINS = 256` — number of byte-value bins.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory flags/types: `CLK_LOCAL_MEM_FENCE`, `__global`, `__local`, `uchar`, `uint`.

#### Exceptions / Errors
- No explicit error handling.
- `histogram_reduce` returns early for global IDs where `bin >= BINS`.
