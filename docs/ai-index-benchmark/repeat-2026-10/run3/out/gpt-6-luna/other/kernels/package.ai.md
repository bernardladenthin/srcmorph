### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:23:48Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-frequency histograms by counting values in parallel work-groups and reducing their partial counts into a 256-bin result.

#### Purpose
- Count occurrences of byte values `0`–`255` in an input buffer.
- Combine per-work-group counts into a final histogram.

#### Responsibilities
- **Parallel counting:** Build a 256-bin local histogram for each work-group and write it to a partial-histogram buffer.
- **Reduction:** Sum each bin across work-groups into the final histogram.

#### Key units
- `histogram_partial` — counts input bytes into per-work-group 256-bin histograms.
- `histogram_reduce` — combines partial counts into the final 256-bin histogram.

#### Data flow
- Input bytes are distributed across work-items, counted into local bins, and written to `partial`; the reduction kernel sums each bin across groups into `histogram`.

#### Dependencies
- OpenCL C 1.2 types and built-ins, including `uchar`, `uint`, work-item and work-group ID/size functions, `barrier`, and `atomic_inc`.

#### Cross-cutting
- Uses local-memory counters, atomic increments, and work-group barriers; buffers and launch dimensions must accommodate indexed input and partial histograms.
