### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T02:32:15Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram of an image or any byte stream using OpenCL C.

#### Purpose
- **Pass 1**: Counts byte occurrences in chunks assigned to each work-group, storing partial histograms in `partial`.
- **Pass 2**: Sums the partial histograms from all work-groups into the final `histogram`.

#### Type
- **Language**: OpenCL C
- **Kernel**: `__kernel`

#### Input
- **Pass 1**:
  - `input`: Global memory, byte array.
  - `length`: Global, length of the input array.
  - `partial`: Global memory, array to store partial histograms.
  - `local_bins`: Local memory, array for each work-group's histogram bins.
- **Pass 2**:
  - `partial`: Global memory, array containing partial histograms from each work-group.
  - `group_count`: Global, number of work-groups.
  - `histogram`: Global memory, array to store the final histogram.

#### Output
- **Pass 1**:
  - `partial`: Updated with partial histograms from each work-group.
- **Pass 2**:
  - `histogram`: Updated with the total histogram counts.

#### Core logic
- **Pass 1**:
  - Initialize local bins to zero.
  - Each work-item in a work-group reads its portion of the input and increments the corresponding bin in local memory.
  - After all work-items complete, write the local histogram to global memory.
- **Pass 2**:
  - Each work-item sums the corresponding bins from all work-groups' partial histograms.
  - Write the result to the final histogram array.

#### Public API
- **histogram_partial(input: __global const uchar *, length: uint, partial: __global uint *, local_bins: __local uint *) -> void**: Computes partial histograms for each work-group.
- **histogram_reduce(partial: __global const uint *, group_count: uint, histogram: __global uint *) -> void**: Sums partial histograms to produce the final histogram.

#### Dependencies
- None

#### Exceptions / Errors
- **Pass 1**: None explicitly handled.
- **Pass 2**: If `bin` exceeds `BINS`, the function returns early.
