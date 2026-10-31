### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T19:51:27Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-frequency histograms for image or byte-stream data using parallel OpenCL counting and reduction.

#### Purpose
- Produce a 256-bin count of byte values from an input buffer.
- Scale counting across work-groups using partial histograms followed by reduction.

#### Responsibilities
- **Parallel counting:** Build one local 256-bin histogram per work-group and write it to a partial-results buffer.
- **Histogram reduction:** Sum each bin across work-group partial histograms into final counts.

#### Key units
- `histogram_partial` — OpenCL kernel that counts input bytes into per-work-group partial histograms.
- `histogram_reduce` — OpenCL kernel that combines partial histograms into final byte counts.
- `BINS` — constant defining the 256 byte-value bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`; work-groups count into local memory and write slices to `partial`.
- `histogram_reduce` reads `partial` across `group_count` work-groups and writes final counts to `histogram[0..255]`.

#### Dependencies
- OpenCL built-ins for work-item and work-group indexing, barriers, and local-memory atomic increments.
- OpenCL types and memory qualifiers including `uchar`, `uint`, `__global`, and `__local`.

#### Cross-cutting
- Uses local-memory work-group histograms, synchronization barriers, and atomic increments for parallel counting.
- No explicit error handling; reduction work-items return when their bin index is outside the 256-bin range.
