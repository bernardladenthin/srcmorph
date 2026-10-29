### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:06:15Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram from an input byte stream using a two-pass OpenCL kernel approach.

#### Purpose
* Implements a two-pass mechanism to compute a byte histogram from input data.
* Provides kernel functions for partial counting and final reduction of the histogram.

#### Type
Kernel (OpenCL C 1.2)

#### Input
* histogram_partial: input byte stream (uchar *input), stream length (uint length), partial histogram storage (uint *partial), local bin counters (local uint *local_bins).
* histogram_reduce: partial histogram data (uint *partial), total number of work-groups (uint group_count), final histogram output (uint *histogram).

#### Output
* histogram_partial: Writes partial bin counts to global memory.
* histogram_reduce: Writes final summed bin counts to the output histogram array.

#### Core logic
* histogram_partial:
    * Initializes local_bins array to zero.
    * Iterates over input data (gid < length), atomically incrementing the count for the byte value in local_bins.
    * Writes the final local_bins counts to the designated slice in the global partial histogram array.
* histogram_reduce:
    * Each work-item targets a specific bin (bin = global_id(0)).
    * Iterates through all partial histograms (group_count), summing the corresponding bin value from each group.
    * Stores the total sum in the final histogram array.

#### Public API
histogram_partial(const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void: Calculates partial bin counts for a slice.
histogram_reduce(const uint *partial, const uint group_count, __global uint *histogram) -> void: Sums partial histograms into final bins.

#### Dependencies
CLK_LOCAL_MEM_FENCE

#### Exceptions / Errors
None explicitly handled; relies on bounds checking (bin >= BINS) in histogram_reduce.
