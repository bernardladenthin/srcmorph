### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-02T00:00:02Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency distributions for image data and arbitrary byte streams.

#### Purpose
- Produces a 256-bin histogram by counting bytes in parallel across OpenCL work-groups and combining their results.

#### Responsibilities
- Per-work-group counting: cooperatively initialize local bins, count input bytes, and write partial histograms.
- Histogram reduction: sum each bin across work-group partial results into the final histogram.

#### Key units
- `histogram_partial` — counts byte values into one 256-bin partial histogram per work-group.
- `histogram_reduce` — combines partial histograms into the final 256-bin `histogram`.
- `BINS` — fixed histogram size of 256 bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes per-group counts to `partial`; `histogram_reduce` sums those counts using `group_count` and writes `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and group indexing, barriers, and atomic increments; OpenCL local/global memory qualifiers and types.

#### Cross-cutting
- Uses local-memory bins, work-group barriers, and `atomic_inc` for concurrent counting; provides no explicit error handling.
