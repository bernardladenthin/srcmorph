### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:47:32Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Counts byte-value frequencies in image or byte-stream data through parallel OpenCL histogram computation.

#### Purpose
- Produces a 256-bin frequency histogram for byte input.
- Uses per-work-group histograms followed by a reduction into final counts.

#### Responsibilities
- **Partial counting:** `histogram_partial` initializes local bins, atomically counts input bytes, and writes one partial histogram per work-group.
- **Reduction:** `histogram_reduce` sums each bin across the work-group partial histograms.

#### Key units
- `histogram_partial` — counts byte values into a work-group’s 256-bin partial histogram.
- `histogram_reduce` — combines partial histograms into the final 256-bin `histogram`.
- `BINS` — constant defining the 256 byte-value counters.

#### Data flow
- `input` bytes are counted into local bins, then written to `partial`; `histogram_reduce` sums those partials into `histogram`.

#### Dependencies
- OpenCL built-ins for work-item and work-group indexing, barriers, and atomic increments.
- OpenCL `uchar` and `uint` types, global/local memory qualifiers, and `CLK_LOCAL_MEM_FENCE`.
