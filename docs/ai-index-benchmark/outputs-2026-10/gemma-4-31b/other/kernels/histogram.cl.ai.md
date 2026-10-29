### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:36:39Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of a byte stream using a two-pass parallel reduction.

#### Purpose
* Generate a frequency distribution of byte values (0-255) from a source buffer.
* Optimize throughput using local memory and atomic operations to minimize global memory contention.

#### Type
OpenCL kernels (C 1.2).

#### Input
* `input`: Global buffer of `uchar` bytes to be indexed.
* `length`: Total number of bytes to process.
* `partial`: Global buffer for intermediate group-level histograms.
* `local_bins`: Local memory buffer for per-group counting.
* `group_count`: Number of work-groups used in the first pass.

#### Output
* `histogram`: Global buffer containing the final 256-bin counts.
* `partial`: Intermediate state written by `histogram_partial`.

#### Core logic
* Pass 1 (`histogram_partial`):
    * Initializes `local_bins` to zero using work-group threads.
    * Iterates through `input` bytes using global ID and global size strides.
    * Increments `local_bins` using `atomic_inc` based on the byte value.
    * Copies the resulting local histogram to a specific segment of the `partial` global buffer based on `get_group_id(0)`.
* Pass 2 (`histogram_reduce`):
    * Assigns one work-item per bin (0-255).
    * Sums the values for the assigned bin across all `group_count` partial histograms.
    * Writes the final aggregated sum to the `histogram` buffer.

#### Public API
* `histogram_partial(input, length, partial, local_bins) -> void`: Computes partial histograms per work-group.
* `histogram_reduce(partial, group_count, histogram) -> void`: Sums partial histograms into final result.
