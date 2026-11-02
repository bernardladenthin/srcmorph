### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T02:01:22Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin frequency distribution of byte values in an image or byte stream using GPU work-groups.

#### Purpose
- Produces a final byte histogram from per-work-group counts.

#### Responsibilities
- Count byte occurrences into local 256-bin histograms and write them as partial results.
- Reduce per-group histograms into the final 256-bin output.

#### Key units
- `histogram_partial` — counts input bytes per work-group using local-memory bins.
- `histogram_reduce` — sums each bin across work-groups into the final histogram.
- `BINS` — defines the histogram size as 256.

#### Data flow
- `input` and `length` feed `histogram_partial`, which writes per-group counts to `partial`; `histogram_reduce` consumes those counts and writes `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and work-group indexing, barriers, and atomic increments; `CLK_LOCAL_MEM_FENCE` for local-memory synchronization.

#### Cross-cutting
- Uses a two-pass GPU pattern: local per-work-group accumulation followed by reduction.
- No explicit error handling or status codes.
