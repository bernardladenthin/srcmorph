### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:38:48Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes the frequency distribution of byte values in image or stream data, producing a 256-bin histogram.

#### Purpose
- Builds a histogram using work-group partial counts followed by a reduction across groups.

#### Responsibilities
- `histogram.cl` defines OpenCL kernels to count input bytes and combine partial histograms.

#### Key units
- `histogram_partial` — counts byte values into 256 local bins per work-group and writes group counts to `partial`.
- `histogram_reduce` — sums each bin across work-groups into the final `histogram`.
- `BINS` — sets the histogram size to 256 byte-value bins.

#### Data flow
- `input` bytes and `length` → work-group-local counts → per-group segments in `partial` → `histogram` reduction.

#### Dependencies
- OpenCL built-ins for work-item and group indexing, synchronization, and atomic increments; `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses cooperative local-memory initialization, work-group barriers, and atomic increments to form partial counts.
- `histogram_reduce` skips writes for global IDs outside the 256 bins.
