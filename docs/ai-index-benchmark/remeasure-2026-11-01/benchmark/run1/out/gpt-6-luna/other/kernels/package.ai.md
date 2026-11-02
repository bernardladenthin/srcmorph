### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:23:32Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency distributions for image or byte-stream data using parallel OpenCL histogram kernels.

#### Purpose
- Produce counts for all 256 byte values.
- Split counting into work-group partial histograms and a final reduction.

#### Responsibilities
- **Partial counting:** `histogram_partial` builds one 256-bin histogram per work-group using local memory.
- **Reduction:** `histogram_reduce` combines partial counts into the final histogram, one work-item per bin.

#### Key units
- `BINS` — defines the 256 byte-value bins.
- `histogram_partial` — counts input bytes into per-work-group partial histograms.
- `histogram_reduce` — sums each bin across the partial histograms.

#### Data flow
- `input` bytes and `length` feed `histogram_partial`, which writes group-specific counts to `partial`.
- `histogram_reduce` reads `partial` and `group_count`, then writes final counts for bins 0–255 to `histogram`.

#### Dependencies
- OpenCL C 1.2 types, qualifiers, and built-ins, including work-item/group queries, barriers, and `atomic_inc`.
- Caller-provided local-memory buffer `local_bins`.

#### Cross-cutting
- Parallel work-items initialize and update local bins cooperatively; work-group barriers coordinate initialization and output.
- No host-side code, explicit error handling, or status codes.
