### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:15:37Z
- G: 
- A: 0.0.0
- X: file
---
> Generates a 256-bin byte histogram of an image or byte stream using a two-pass reduction strategy.

#### Purpose
* Implements a parallel histogram calculation for byte data.
* Uses a two-pass approach to minimize atomic contention by utilizing local memory and partial results.

#### Type
OpenCL C kernel (OpenCL 1.2).

#### Input
* `histogram_partial`: `__global const uchar *input` (source data), `uint length` (total bytes), `__global uint *partial` (global buffer for intermediate results), `__local uint *local_bins` (local memory buffer).
* `histogram_reduce`: `__global const uint *partial` (intermediate results), `uint group_count` (number of work-groups), `__global uint *histogram` (final output buffer).

#### Output
* `histogram_partial`: Writes partial histogram counts to the `partial` global buffer.
* `histogram_reduce`: Writes the final aggregated histogram counts to the `histogram` global buffer.

#### Core logic
* `histogram_partial`:
    * Initializes `local_bins` in local memory using a work-group stride.
    * Iterates through the input data, using `atomic_inc` to increment counts in `local_bins`.
    * Synchronizes work-items via `barrier`.
    * Writes the local work-group's results into a unique segment of the `partial` global buffer based on `group_id`.
* `histogram_reduce`:
    * Maps one work-item to each of the 256 bins.
    * Iterates through all work-groups to sum the values at the specific bin index.
    * Writes the final sum to the output `histogram` buffer.

#### Public API
* `histogram_partial(input, length, partial, local_bins) -> void` Performs local counting and writes partial results.
* `histogram_reduce(partial, group_count, histogram) -> void` Aggregates partial results into a final histogram.

#### Dependencies
* OpenCL C 1.2 standard library.
