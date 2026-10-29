### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:59:03Z
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
* histogram_partial: input byte stream (uchar*), stream length (uint), partial histogram storage (uint*), local bin storage (local uint*).
* histogram_reduce: partial histogram data (uint*), total number of work-groups (uint), final histogram output (uint*).

#### Output
* histogram_partial: Stores partial histograms from each work-group.
* histogram_reduce: Stores the final summed histogram counts.

#### Core logic
* histogram_partial:
    * Initializes local_bins array to zero per work-group.
    * Iterates over input stream chunks assigned to the work-group.
    * Uses atomic_inc on local_bins based on input byte value.
    * Writes the final local_bins to the designated slot in the partial histogram array.
* histogram_reduce:
    * Each work-item targets a specific bin index (0 to 255).
    * Iterates over all partial histograms (group_count) to sum counts for that bin.
    * Stores the total sum in the final histogram array.

#### Public API
histogram_partial(uchar* input, uint length, uint* partial, local uint* local_bins) -> void: Calculates partial bin counts.
histogram_reduce(uint* partial, uint group_count, uint* histogram) -> void: Reduces partial histograms to final counts.

#### Dependencies
CLK_LOCAL_MEM_FENCE

#### Exceptions / Errors
None specified.
