### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:04:51Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of image data or streams using two-pass OpenCL kernel reduction.

#### Purpose
- Performs initial binning and local counting of byte values in Pass 1.
- Aggregates partial counts from work-groups to produce final histogram in Pass 2.

#### Type
OpenCL kernel (C 1.2)

#### Input
- `input`: global byte array containing data to histogram.
- `length`: total number of bytes in input.
- `partial`: global array for storing partial counts from Pass 1.
- `group_count`: number of work-groups in Pass 2.

#### Output
- `histogram`: global array containing final 256-bin counts.
- Side effect: writes partial counts to `partial` in Pass 1.

#### Core logic
- **Pass 1**: Each work-group initializes local bins, increments counters for input bytes within its slice, and writes partial sums to `partial` array.
- **Pass 2**: Each work-item sums partial counts for its corresponding bin across all work-groups to compute final histogram values.

#### Public API
- `histogram_partial(input, length, partial, local_bins)` -> void
- `histogram_reduce(partial, group_count, histogram)` -> void

#### Dependencies
- OpenCL runtime functions (`get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `barrier`, `atomic_inc`).
- Constants: `BINS` (256).

#### Exceptions / Errors
- None declared; assumes valid input dimensions and memory alignment.
