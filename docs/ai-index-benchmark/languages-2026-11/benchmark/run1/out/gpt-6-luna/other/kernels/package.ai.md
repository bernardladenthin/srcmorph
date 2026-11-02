### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:54Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin frequency distribution of byte values across an image or byte stream.

#### Purpose
- Produces a final byte histogram by combining per-work-group partial counts.

#### Responsibilities
- **Partial counting:** Build one 256-bin histogram per work-group from the input byte buffer.
- **Reduction:** Sum corresponding bins across work-groups into the final histogram.

#### Key units
- `histogram_partial` — Counts input bytes into work-group-local bins and writes partial histograms.
- `histogram_reduce` — Reduces partial histograms into one 256-bin `histogram`.
- `BINS` — Defines the histogram size as 256.

#### Data flow
- `input` and `length` feed `histogram_partial`, which writes per-group counts to `partial`; `histogram_reduce` consumes `partial` and `group_count` to produce `histogram`.

#### Dependencies
- OpenCL C 1.2 built-ins, types, memory qualifiers, and `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Work-group synchronization and local-memory `atomic_inc` coordinate partial counting; reduction assigns one work-item per bin.
- No explicit error handling or status codes.
