### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:59:52Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-frequency distributions for image data or other byte streams.

#### Purpose
- Produces a 256-bin histogram using work-group partial counts followed by a reduction.

#### Responsibilities
- Count byte values into per-work-group histograms using local memory and atomic increments.
- Combine partial histograms into final per-byte totals.

#### Key units
- `histogram_partial` — counts input bytes into one 256-bin histogram per work-group.
- `histogram_reduce` — sums each bin across work-group histograms into the final histogram.
- `BINS` — constant defining the 256 histogram bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes partial histograms to `partial`; `histogram_reduce` combines them using `group_count` and writes `histogram`.

#### Dependencies
- OpenCL C 1.2 built-ins for work-item and work-group indexing, barriers, and atomic increments.
- OpenCL types, address spaces, and `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses a two-pass parallel pattern: local work-group counting, then per-bin reduction.
- Uses local memory and `atomic_inc` for concurrent bin updates; no explicit error handling.
