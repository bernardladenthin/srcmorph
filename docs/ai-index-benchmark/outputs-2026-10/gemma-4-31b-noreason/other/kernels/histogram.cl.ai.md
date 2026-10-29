### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:54:03Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass parallel reduction.

#### Purpose
* Generate a frequency distribution of byte values (0-255) from a source buffer.
* Optimize throughput using local memory atomics and a final reduction pass.

#### Type
OpenCL C 1.2 Kernels.

#### Input
* `input` (__global const uchar*): Source byte stream.
* `length` (uint): Total number of bytes to process.
* `partial` (__global uint*): Intermediate buffer storing per-work-group histograms.
* `local_bins` (__local uint*): Local memory scratchpad for atomic increments.
* `group_count` (uint): Number of work-groups that contributed to the partial buffer.

#### Output
* `partial` (__global uint*): Written partial counts per work-group.
* `histogram` (__global uint*): Final aggregated 256-bin histogram.

#### Core logic
* **Pass 1 (histogram_partial):**
    * Initializes `local_bins` to zero using work-group threads.
    * Iterates through `input` buffer using global ID and stride, performing `atomic_inc` on `local_bins` based on byte value.
    * Synchronizes via `barrier` to ensure all local counts are complete.
    * Writes the local histogram results into a specific segment of the `partial` global buffer based on `get_group_id(0)`.
* **Pass 2 (histogram_reduce):**
    * Assigns one work-item per bin (up to 256).
    * Sums the value of a specific bin across all `group_count` partial histograms.
    * Writes the final total to the `histogram` output buffer.

#### Public API
* `histogram_partial(input, length, partial, local_bins) -> void`: Computes partial histogram per group.
* `histogram_reduce(partial, group_count, histogram) -> void`: Aggregates partials into final histogram.
