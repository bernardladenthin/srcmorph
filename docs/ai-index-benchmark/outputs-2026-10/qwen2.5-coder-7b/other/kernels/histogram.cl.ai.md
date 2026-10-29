### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:17:57Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram of an image or byte stream using OpenCL C 1.2.

#### Purpose
- Computes a histogram of byte values in a given input stream.
- Distributes the computation across multiple work-groups for parallel processing.

#### Type
- OpenCL kernel

#### Input
- `input`: A global constant array of `uchar` representing the byte stream.
- `length`: The length of the input byte stream.
- `partial`: A global array where partial histograms will be written.
- `local_bins`: A local array used for counting byte occurrences within each work-group.

#### Output
- `partial`: Updated with partial histograms from each work-group.
- `histogram`: Updated with the final histogram after reduction.

#### Core logic
- **Pass 1**: Each work-group initializes its local histogram counters to zero. It then counts the occurrences of each byte in its slice of the input stream using atomic operations. The results are written to the `partial` array.
- **Pass 2**: A single work-item per bin sums the partial histograms from all work-groups, producing the final histogram.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void`: Counts byte occurrences in the input stream and writes partial histograms to the `partial` array.
- `histogram_reduce(partial, group_count, histogram) -> void`: Sums partial histograms to produce the final histogram.

#### Dependencies
- `uchar`: The type of elements in the input array.
- `uint`: The type of elements for length and indices.
- `__global`: Memory qualifier for global memory.
- `__local`: Memory qualifier for local memory.

#### Exceptions / Errors
- None explicitly handled or thrown.
