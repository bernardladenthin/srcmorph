### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:58:55Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Counts occurrences of each byte value in an image or byte stream, producing a 256-bin histogram.

#### Purpose
- Computes a byte-frequency histogram in two passes: work-group partial counts, then per-bin reduction.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial`: `input` byte buffer and `length`; runtime-provided local memory `local_bins`.
- `histogram_reduce`: `partial` histogram buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the combined 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local counters, distributes input indices across work-items using global IDs and strides, atomically counts each byte value, then writes local counts to the work-group’s partial-histogram region.
- `histogram_reduce` assigns one work-item to each bin, sums that bin across all partial histograms, and stores the result.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — count work-group byte frequencies.
- `histogram_reduce(partial, group_count, histogram) -> void` — combine partial histograms.

#### Constants and configuration
- `BINS = 256` — histogram bin count.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL types and flags: `uchar`, `uint`, `__global`, `__local`, `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling.
