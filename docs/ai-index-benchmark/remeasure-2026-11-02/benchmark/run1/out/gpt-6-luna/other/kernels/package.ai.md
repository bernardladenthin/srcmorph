### other/kernels
- H: 1.0
- C: 7D744C6A
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T23:29:34Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes 256-bin byte-frequency histograms for image data or other byte streams using OpenCL.

#### Purpose
- Count occurrences of each byte value and produce a final histogram.

#### Responsibilities
- Perform per-work-group byte counting into partial histograms, then combine partial counts by bin.

#### Key units
- `histogram_partial` — OpenCL kernel that counts input bytes into one 256-bin histogram per work-group.
- `histogram_reduce` — OpenCL kernel that sums partial histograms into the final 256-bin `histogram`.
- `BINS` — defines the histogram size as 256 byte values.

#### Data flow
- `input` and `length` feed `histogram_partial`, which writes per-group counts to `partial`; `histogram_reduce` combines those counts into `histogram`.

#### Dependencies
- OpenCL work-item and work-group built-ins, barriers, `atomic_inc`, and `CLK_LOCAL_MEM_FENCE`.

#### Cross-cutting
- Uses caller-provided local memory for per-work-group counters and global buffers for partial and final results.
- Reduction work-items outside the 256-bin range return without writing.
