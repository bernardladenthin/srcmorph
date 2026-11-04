### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:30:29Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Computes a byte-frequency histogram by combining per-work-group counts.
- Supports input buffers of arbitrary length.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and caller-provided `local_bins` local-memory buffer.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `histogram_partial` writes one 256-counter partial histogram per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` initializes local counters across work-items, then synchronizes the work-group.
- Work-items traverse input indices in global-size strides and atomically increment the local counter for each byte value.
- After a second local-memory barrier, work-items write local counters to the work-group’s slice of `partial`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all partial histograms, and stores the result; work-items with bin indices outside the 256-bin range return.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — builds per-group counts.
- `histogram_reduce(partial, group_count, histogram) -> void` — combines per-group counts.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL types: `uchar`, `uint`.
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.

#### Exceptions / Errors
- `histogram_reduce` returns without writing when the global ID is `>= BINS`.
- No explicit validation of buffer sizes, `length`, or `group_count`.
