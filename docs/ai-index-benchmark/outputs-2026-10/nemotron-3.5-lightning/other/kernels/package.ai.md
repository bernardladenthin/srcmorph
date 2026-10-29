### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:11:32Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes 256-bin byte-level histograms for image or byte-stream data using OpenCL two-pass reduction.

#### Purpose
- Provides OpenCL kernel implementations for computing 256-bin byte-level histograms from image or byte-stream data
- Enables parallel histogram calculation via two-pass reduction across work-groups

#### Responsibilities
- Accumulate per-work-group partial histograms into a global buffer using local memory and atomic operations
- Reduce partial histograms across all groups into a single final 256-bin histogram

#### Key units
- `histogram_partial`: OpenCL kernel that accumulates partial histograms into a global buffer per work-group
- `histogram_reduce`: OpenCL kernel that reduces partial histograms into the final 256-bin histogram
- `BINS 256`: constant defining the number of histogram bins
- `uchar`, `uint`: primary data types for input bytes and counter values
- `partial`, `local_bins`, `histogram`: buffer objects for partial and final histogram storage

#### Data flow
- Input byte stream or image data enters `histogram_partial`, which uses OpenCL work-groups to zero local counters via `CLK_LOCAL_MEM_FENCE`, accumulate byte counts with `atomic_inc`, fence, and write per-group partial slices to `partial`
- `histogram_reduce` then iterates across all partial groups, summing `partial[g * BINS + bin]` into `histogram[bin]` with an out-of-bounds guard `bin >= BINS`

#### Dependencies
- OpenCL C 1.2 runtime
- `atomic_inc` operation for per-byte accumulation
- Types: `uchar`, `uint`; constant: `BINS 256`
- Buffers: `partial`, `local_bins`, `histogram`
- OpenCL host API for runtime error handling

#### Cross-cutting
- Two-pass reduction pattern with `CLK_LOCAL_MEM_FENCE` synchronization between passes
- `atomic_inc` used for concurrent byte-level counter increments
- Out-of-bounds guard `bin >= BINS` in `histogram_reduce` prevents out-of-range access
- No language-level exceptions; OpenCL runtime errors possible via host API
