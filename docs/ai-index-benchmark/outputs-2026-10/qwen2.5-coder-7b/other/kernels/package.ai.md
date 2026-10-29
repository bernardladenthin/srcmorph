### other/kernels
- H: 1.0
- C: 0E82DBF3
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:24Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Calculates a 256-bin byte histogram of an image or byte stream using OpenCL C 1.2.

#### Purpose
- Computes a histogram of byte values in a given input stream.
- Distributes the computation across multiple work-groups for parallel processing.

#### Responsibilities
- Parallel byte counting within work-groups.
- Reduction of partial histograms to produce the final histogram.

#### Key units
- `histogram_partial`: Counts byte occurrences in the input stream and writes partial histograms to the `partial` array.
- `histogram_reduce`: Sums partial histograms to produce the final histogram.

#### Data flow
- Input: `input` (global constant array of `uchar`), `length` (uint).
- Output: `partial` (global array), `histogram` (global array).

#### Dependencies
- `uchar`: The type of elements in the input array.
- `uint`: The type of elements for length and indices.
- `__global`: Memory qualifier for global memory.
- `__local`: Memory qualifier for local memory.

#### Cross-cutting
- None explicitly noted.
