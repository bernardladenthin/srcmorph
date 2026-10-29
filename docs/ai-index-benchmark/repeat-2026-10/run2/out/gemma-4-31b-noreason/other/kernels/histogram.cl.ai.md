### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:06Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass parallel reduction.

#### Purpose
* Generate a frequency distribution of byte values (0-255) from a large input buffer.
* Optimize performance using local memory for partial counts before global aggregation.

#### Type
OpenCL C 1.2 Kernels.

#### Input
* `input` (__global const uchar*): Source byte stream.
* `length` (uint): Total number of bytes to process.
* `partial` (__global uint*): Intermediate buffer storing per-work-group histograms.
* `local_bins` (__local uint*): Local memory scratchpad for atomic increments.
* `group_count` (uint): Number of work-groups that executed the first pass.

#### Output
* `partial` (__global uint*): Written partial counts indexed by `group_id * BINS + bin`.
* `histogram` (__global uint*): Final aggregated 256-bin histogram.

#### Core logic
* **Pass 1 (`histogram_partial`):**
    * Initializes `local_bins` to zero across the work-group.
    * Iterates through `input` using a global stride, performing `atomic_inc` on `local_bins` based on byte values.
    * Synchronizes via `barrier` to ensure all increments are complete.
    * Copies the local histogram results into a specific segment of the `partial` global buffer.
* **Pass 2 (`histogram_reduce`):**
    * Assigns one work-item per bin (up to 256).
    * Sums the specific bin value across all `group_count` partial histograms.
    * Writes the final total to the `histogram` output buffer.

#### Public API
* `histogram_partial(input, length, partial, local_bins) -> void`: Computes local group histograms.
* `histogram_reduce(partial, group_count, histogram) -> void`: Aggregates partials into final histogram.
