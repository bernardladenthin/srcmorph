### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:38:21Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a complete 256-bin frequency distribution of byte values from image data or arbitrary byte streams.

#### Purpose
- Generate byte-value histograms in parallel using per-work-group partial counts and a final reduction.
- Provide OpenCL C 1.2 kernels for histogram counting and aggregation.

#### Responsibilities
- **Partial counting:** `histogram_partial` initializes local bins, counts input bytes per work-group, and writes each group’s 256 counters to `partial`.
- **Aggregation:** `histogram_reduce` sums each bin across the partial histograms into the final `histogram`.

#### Key units
- `BINS` — constant defining 256 byte-value bins.
- `histogram_partial` — counts input bytes into per-work-group histograms.
- `histogram_reduce` — combines per-work-group histograms into the final result.

#### Data flow
- `input` and `length` feed `histogram_partial`; each group counts byte values into `local_bins` and writes its counts to `partial`.
- `partial` and `group_count` feed `histogram_reduce`, which writes the summed bins to `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and work-group IDs and sizes, barriers, and `atomic_inc`.
- OpenCL local-memory fence flag `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses local-memory initialization, atomic increments, and work-group barriers for parallel counting.
- No explicit error handling or status codes.
