### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:45:05Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Counts the frequency of each byte value in an image or byte stream and writes a 256-bin histogram.

#### Purpose
- Computes a byte-value frequency histogram in two passes: per-work-group partial counts, then per-bin reduction.
- Supports image data or any input byte stream.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer and `length`; caller-provided `partial` output buffer and `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group into `partial`.
- `histogram_reduce` writes the final `BINS` counters to `histogram`.

#### Core logic
- `histogram_partial` initializes local-memory bins cooperatively, synchronizes the work-group, and atomically increments the bin matching each assigned input byte.
- Input indices are traversed by global-ID stride (`i += gsize`); each work-item processes indices starting at its global ID.
- After a second local-memory barrier, work-items cooperatively copy local counts to `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across `group_count` partial histograms, and stores the total. Work-items with `bin >= BINS` return.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — generate per-group byte counts.
- `histogram_reduce(partial, group_count, histogram) -> void` — sum partial counts into final bins.

#### Constants and configuration
- `BINS = 256` — histogram bin count.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory spaces and types: `__global`, `__local`, `uchar`, `uint`; barrier flag `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- No explicit error handling or status codes.
