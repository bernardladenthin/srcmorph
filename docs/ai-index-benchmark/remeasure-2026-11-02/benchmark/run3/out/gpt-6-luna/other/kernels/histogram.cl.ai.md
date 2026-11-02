### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:59:08Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Counts the frequency of every byte value in an image or byte stream using a 256-bin histogram.

#### Purpose
- Builds per-work-group byte counts, then combines them into one histogram.
- Supports arbitrary byte-stream input through two OpenCL passes.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and caller-provided `local_bins` storage.
- `histogram_reduce`: `partial` histograms and `group_count`.

#### Output
- `histogram_partial` writes `BINS` counters per work-group to `partial`.
- `histogram_reduce` writes the final `BINS`-counter result to `histogram`.

#### Core logic
- `histogram_partial` initializes local-memory bins cooperatively, then synchronizes the work-group.
- Work-items traverse input in global-size strides; each byte increments its value’s local bin with `atomic_inc`.
- After a second local-memory barrier, work-items copy the local bins to the group’s segment in `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across `group_count` partial histograms, and writes the total. Work-items with `bin >= BINS` return.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — counts bytes per work-group.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines group histograms.

#### Constants and configuration
- `BINS` = `256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and qualifiers: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Exceptions / Errors
- No explicit error handling.
