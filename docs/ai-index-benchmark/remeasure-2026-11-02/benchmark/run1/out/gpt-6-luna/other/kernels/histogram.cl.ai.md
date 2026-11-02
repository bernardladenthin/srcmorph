### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:28:39Z
- G: 
- A: 0.0.0
- X: file
- P: 43E74872
---
> Counts the frequency of each byte value in an image or byte stream and produces a 256-bin histogram.

#### Purpose
- Computes byte frequencies using per-work-group partial histograms followed by a reduction.
- Supports image data or any byte stream.

#### Type
- OpenCL C 1.2 kernel source; defines two `__kernel` entry points.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, and `partial` output buffer; `local_bins` is caller-provided local memory.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` distributes initialization and output of the 256 local counters across work-items.
- Each work-item visits input indices starting at its global ID, stepping by global work size, and atomically increments the matching local byte-value counter.
- After local counting, each work-group writes its counters at `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all `group_count` partial histograms.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — count work-group byte frequencies.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — combine partial counts by bin.

#### Constants and configuration
- `BINS` = `256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- `histogram_reduce` returns without writing when the global ID is outside the 256-bin range.
