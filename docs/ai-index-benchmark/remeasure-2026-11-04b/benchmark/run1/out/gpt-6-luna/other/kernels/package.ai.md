### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:50:27Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Counts byte-value frequencies across an input stream, producing a 256-bin histogram.

#### Purpose
- Compute byte frequencies using per-work-group partial histograms and a final reduction.

#### Responsibilities
- Build partial histograms with local-memory bins and atomic increments.
- Reduce per-group counts into one final count for each byte value.

#### Key units
- `histogram_partial` — counts input bytes into a 256-bin histogram for each work-group.
- `histogram_reduce` — sums each bin across work-groups into the final `histogram`.
- `BINS` — constant defining the 256 byte-value bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes per-group counts to `partial`.
- `histogram_reduce` combines `partial` using `group_count` and writes the final histogram.

#### Dependencies
- OpenCL C 1.2 built-ins for work-item and work-group indexing, barriers, local-memory synchronization, and atomic increments.

#### Cross-cutting
- Uses local-memory partial counts and `atomic_inc` to coordinate parallel bin updates.
- Provides no explicit error handling or error codes.
