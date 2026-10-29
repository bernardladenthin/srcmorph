### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:59:11Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte frequency distribution from an input byte stream using a two-pass OpenCL kernel approach.

#### Purpose
* Implements a two-pass histogram calculation for byte streams.
* Provides kernel functions for partial counting and final reduction.

#### Type
Kernel (OpenCL C 1.2)

#### Input
* histogram_partial: input byte stream (uchar *input), stream length (uint length), partial histogram storage (uint *partial), local bin storage (local uint *local_bins).
* histogram_reduce: partial histogram data (uint *partial), total number of work-groups (uint group_count), final histogram output (uint *histogram).

#### Output
* histogram_partial: Writes partial bin counts per work-group to global memory.
* histogram_reduce: Writes final summed bin counts to the output histogram array.

#### Core logic
* histogram_partial:
    * Initializes local_bins array to zero.
    * Iterates over input stream chunks (gid to length) and atomically increments the corresponding bin in local_bins based on input byte value.
    * Writes the final local_bins to the designated slot in the global partial histogram array.
* histogram_reduce:
    * Each work-item targets a specific bin index (bin).
    * Iterates through all partial histograms (group_count) and sums the value for that bin across all groups.
    * Stores the total sum in the final histogram array.

#### Public API
histogram_partial(const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void: Calculates partial bin counts per work-group.
histogram_reduce(const uint *partial, const uint group_count, __global uint *histogram) -> void: Sums partial histograms to produce final counts.

#### Dependencies
CLK_LOCAL_MEM_FENCE

#### Exceptions / Errors
No explicit exceptions handled; relies on boundary checks (bin >= BINS) in histogram_reduce.
