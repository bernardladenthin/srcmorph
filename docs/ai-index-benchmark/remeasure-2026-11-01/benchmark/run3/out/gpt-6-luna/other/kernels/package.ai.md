### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:02:50Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a complete 256-bin frequency distribution for byte-valued image data or other byte streams.

#### Purpose
- Count occurrences of each byte value using a two-stage OpenCL histogram.
- Reduce work-group-level counts into one final histogram.

#### Responsibilities
- **Partial counting:** Build per-work-group byte counts with local-memory bins and atomic increments.
- **Histogram reduction:** Sum each bin across all work-groups.

#### Key units
- `histogram_partial` — counts input bytes into a 256-bin histogram for each work-group.
- `histogram_reduce` — combines per-work-group counts into the final histogram.
- `BINS` — constant defining the 256 byte-value bins.

#### Data flow
- `input` byte buffer and `length` feed `histogram_partial`, which writes per-work-group counts to `partial`.
- `histogram_reduce` reads `partial` and `group_count`, then writes the combined counts to `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and work-group indexing, barriers, and `atomic_inc`.
- `CLK_LOCAL_MEM_FENCE` for local-memory synchronization.

#### Cross-cutting
- Uses cooperative local-memory initialization and barriers around atomic counting.
- Provides no explicit error handling or status codes.
