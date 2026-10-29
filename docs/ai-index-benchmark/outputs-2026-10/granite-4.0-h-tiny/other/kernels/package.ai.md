### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:10:14Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> A pure OpenCL kernel for computing a 256-bin byte histogram of an input byte stream in two passes.

#### Purpose
- Calculates a 256-bin byte histogram for an image or byte stream in two OpenCL passes.

#### Responsibilities
- `histogram_partial`: Performs the first pass, counting input bytes into local bins and writing partial histograms to global memory.
- `histogram_reduce`: Performs the second pass, summing partial histograms from all work-groups to produce the final histogram.

#### Key units
- `histogram_partial`: Takes input byte stream, length, partial histogram storage, and local bin counters; performs the first pass of histogram calculation.
- `histogram_reduce`: Takes partial histogram storage, group count, and produces the final 256-bin histogram.

#### Data flow
1. `histogram_partial` processes the input byte stream in parallel, counting bytes into local bins and writing partial histograms to global memory.
2. `histogram_reduce` then processes the partial histograms from all work-groups in parallel, summing them to produce the final histogram.

#### Dependencies
- None (pure OpenCL kernel)

#### Cross-cutting
- Pure OpenCL kernel with no error handling or exceptions.
