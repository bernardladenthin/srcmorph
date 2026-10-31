### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:57:02Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin frequency distribution of a byte stream using a two-pass parallel reduction.

#### Purpose
* Performs high-performance histogram generation for images or byte streams.
* Utilizes local memory atomics and global reduction to minimize contention.

#### Type
OpenCL kernel (C)

#### Input
* histogram_partial: `input` (global uchar buffer), `length` (uint), `partial` (global uint buffer), `local_bins` (local uint buffer).
* histogram_reduce: `partial` (global uint buffer containing group results), `group_count` (uint).

#### Output
* histogram_partial: Writes partial histogram counts to the `partial` global buffer.
* histogram_reduce: Writes the final aggregated 256-bin histogram to the `histogram` global buffer.

#### Core logic
* **Pass 1 (histogram_partial)**:
    * Initializes `local_bins` in local memory to zero using a strided loop.
    * Synchronizes work-group via `barrier`.
    * Iterates through the `input` buffer using a strided approach, performing `atomic_inc` on `local_bins` based on byte values.
    * Synchronizes work-group via `barrier`.
    * Writes the completed `local_bins` counts from local memory to the specific offset in the `partial` global buffer.
* **Pass 2 (histogram_reduce)**:
    * Assigns one work-item per bin.
    * Iterates through all `group_count` partial histograms.
    * Sums the values for the specific bin across all groups.
    * Writes the final sum to the `histogram` buffer.

#### Public API
* histogram_partial(input, length, partial, local_bins) -> void: Generates partial histogram counts per work-group.
* histogram_reduce(partial, group_count, histogram) -> void: Aggregates partial counts into a final histogram.

#### Exceptions / Errors
* histogram_reduce performs a bounds check to ensure `bin < BINS`.
