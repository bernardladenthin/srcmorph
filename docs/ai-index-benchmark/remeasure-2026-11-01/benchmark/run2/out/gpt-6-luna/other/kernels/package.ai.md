### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:45:00Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency distributions for image or byte-stream data using parallel GPU histogramming.

#### Purpose
- Provides OpenCL kernels for per-work-group byte counting and aggregation into a final 256-bin histogram.

#### Responsibilities
- **Histogram computation:** Partition input-byte counting across work-groups using local-memory bins, then merge partial histograms.

#### Key units
- `histogram_partial` — counts byte values into one 256-bin partial histogram per work-group.
- `histogram_reduce` — sums each bin across partial histograms to produce the final histogram.
- `BINS` — defines the histogram size as 256 bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes per-work-group counts to `partial`; `histogram_reduce` combines those counts using `group_count` and writes the final `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and work-group indexing, barriers, and atomic increments; OpenCL local-memory fence, types, and address spaces.

#### Cross-cutting
- Both kernels rely on launch-configured global and local work sizes; no explicit error handling or status codes are provided.
