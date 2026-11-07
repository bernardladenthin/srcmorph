### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:53:34Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes byte-value frequency distributions for image data or arbitrary byte streams.

#### Purpose
- Produces a 256-bin histogram using per-work-group counts followed by reduction.
- Supports histogramming any byte buffer, including image data.

#### Responsibilities
- **Partial counting:** `histogram_partial` counts byte values into work-group-local bins and writes each group’s partial histogram.
- **Reduction:** `histogram_reduce` sums each bin across work groups into the final histogram.

#### Key units
- `BINS` — constant defining 256 byte-value bins.
- `histogram_partial` — OpenCL kernel that partitions input processing across work-items and emits one partial histogram per work group.
- `histogram_reduce` — OpenCL kernel that combines partial histograms into the final 256-bin result.

#### Data flow
- `input` bytes and `length` → work-group-local counters in `local_bins` → per-group slices in `partial` → reduced counts in `histogram`.

#### Dependencies
- OpenCL C 1.2 built-ins for work-item, work-group, synchronization, and atomic operations; uses `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses cooperative local-memory initialization and copying, with work-group barriers around shared counter updates.
- `histogram_reduce` skips work-items whose bin index is outside the 256-bin range.
