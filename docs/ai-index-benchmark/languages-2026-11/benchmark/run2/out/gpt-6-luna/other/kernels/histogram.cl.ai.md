### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:24Z
- G: 
- A: 0.0.0
- X: file
---
> Counts occurrences of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- `histogram.cl` computes a byte-frequency histogram in parallel.
- Uses per-work-group partial histograms, then reduces them into one result.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial`: byte buffer `input`, element count `length`, partial-histogram buffer `partial`, and caller-provided local-memory buffer `local_bins`.
- `histogram_reduce`: partial-histogram buffer `partial` and work-group count `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin histogram to `histogram`.

#### Core logic
- `histogram_partial` initializes `local_bins`, then synchronizes work-items with a local-memory barrier.
- Work-items stride across `input` by global size and atomically increment the local counter for each byte value.
- After a second barrier, work-items copy local counters to `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all `group_count` partial histograms.
- Reduction work-items with a bin index outside `BINS` return without writing.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — builds per-group counts.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines partial counts.

#### Constants and configuration
- `BINS = 256` — number of byte values and histogram counters.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory qualifiers and flags: `__global`, `__local`, `CLK_LOCAL_MEM_FENCE`.
- OpenCL types: `uchar`, `uint`.

#### Exceptions / Errors
- No explicit error handling or status codes.
