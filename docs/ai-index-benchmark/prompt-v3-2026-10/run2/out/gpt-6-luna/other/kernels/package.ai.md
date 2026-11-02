### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:13:40Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency counts on the GPU by building per-work-group histograms and reducing them into a final 256-bin histogram.

#### Purpose
- Provide a two-pass OpenCL histogram for byte buffers, including image data.
- Count each byte value from 0–255 and return its total frequency.

#### Responsibilities
- **Per-work-group counting:** Initialize local bins, distribute input bytes across work-items, and accumulate counts with atomic increments.
- **Histogram reduction:** Sum each bin across work-group partial histograms.

#### Key units
- `histogram_partial` — writes one 256-bin byte-frequency histogram per work-group.
- `histogram_reduce` — combines per-group counts into the final histogram.
- `BINS` — constant defining the 256 byte-value bins.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes group histograms to `partial`.
- `histogram_reduce` sums `partial` across `group_count` and writes the final counts to `histogram`.

#### Dependencies
- OpenCL C 1.2 work-item and work-group built-ins, barriers, local-memory atomics, and `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses work-group local memory for partial counts, followed by a separate global reduction pass.
- Provides no explicit error handling or status codes.
