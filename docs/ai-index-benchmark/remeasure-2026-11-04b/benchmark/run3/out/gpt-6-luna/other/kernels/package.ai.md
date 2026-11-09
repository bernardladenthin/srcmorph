### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:08:01Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes frequency counts for all 256 byte values in image or byte-stream data.

#### Purpose
- Produces a 256-bin byte histogram using per-work-group counts followed by a reduction.

#### Responsibilities
- Count input bytes into local work-group histograms and combine partial counts into final totals.

#### Key units
- `histogram_partial` — counts byte values per work-group and writes 256 counters per group to `partial`.
- `histogram_reduce` — sums each bin across partial histograms into the final `histogram`.
- `BINS` — fixed histogram size of 256.

#### Data flow
- `input` bytes are counted into work-group-local bins, copied to `partial`, then reduced across groups into `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and group indexing, barriers, and local-memory atomic increments.

#### Cross-cutting
- Uses OpenCL C 1.2, local-memory synchronization, and `atomic_inc`; no explicit error handling.
