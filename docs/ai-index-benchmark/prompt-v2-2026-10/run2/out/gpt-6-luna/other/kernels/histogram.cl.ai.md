### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:40Z
- G: 
- A: 0.0.0
- X: file
---
> Computes the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Counts occurrences of all 256 byte values.
- Builds per-work-group partial histograms, then combines them into one histogram.

#### Type
- OpenCL C 1.2 kernel source.
- Defines two `__kernel` entry points; no host-side types.

#### Input
- `histogram_partial`: byte buffer `input`, element count `length`, and work-group local memory `local_bins`.
- `histogram_reduce`: partial histogram buffer `partial` and number of work-groups `group_count`.

#### Output
- `histogram_partial` writes 256 counters per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin counts to `histogram`.

#### Core logic
- `histogram_partial` distributes initialization of 256 local counters across work-items, then synchronizes the work-group.
- Each work-item processes input indices `gid, gid + gsize, ...` while below `length`, atomically incrementing the local counter for each byte value.
- After a second local-memory barrier, work-items copy local counters to the work-group’s slice at `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin; each sums that bin’s counter across `group_count` partial histograms.
- Reduction work-items with bin indices at or above `BINS` return without writing.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — count bytes per work-group.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — combine partial histograms.
- Internal helpers: none.

#### Constants and configuration
- `BINS = 256` — number of byte-value counters.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory flags: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and qualifiers: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Exceptions / Errors
- No explicit error handling or error codes.
