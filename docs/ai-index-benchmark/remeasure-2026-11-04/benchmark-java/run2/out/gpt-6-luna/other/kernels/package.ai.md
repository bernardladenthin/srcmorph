### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:31:33Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin frequency distribution for byte data, combining per-work-group counts into a final histogram.

#### Purpose
- Count occurrences of each byte value in an input buffer.
- Support arbitrary input lengths through partial histograms and a reduction pass.

#### Responsibilities
- Build per-work-group byte counts using local-memory bins and atomic increments.
- Sum partial counts across work-groups to produce the final histogram.

#### Key units
- `histogram_partial` — generates one 256-bin partial histogram per work-group.
- `histogram_reduce` — combines partial histograms into 256 final counts.
- `BINS` — defines the histogram size as 256.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes per-group counts to `partial`.
- `histogram_reduce` reads `partial` and `group_count`, then writes the final counts to `histogram`.

#### Dependencies
- OpenCL C 1.2 types, work-item and group built-ins, barriers, and `atomic_inc`.

#### Cross-cutting
- Uses local-memory atomics and work-group barriers for parallel counting.
- Buffer sizes, `length`, and `group_count` are not explicitly validated.
