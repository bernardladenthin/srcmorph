### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:30:19Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or byte stream using a two-pass parallel reduction.

#### Purpose
* Generate frequency counts for every byte value (0-255) in a given input buffer.
* Optimize throughput using local memory atomics and a final reduction pass.

#### Type
OpenCL C 1.2 Kernels.

#### Input
* `input`: `__global const uchar*` byte stream to be analyzed.
* `length`: `uint` total number of bytes in the input.
* `partial`: `__global uint*` buffer storing intermediate histogram results per work-group.
* `local_bins`: `__local uint*` scratchpad for work-group local counting.
* `group_count`: `uint` number of work-groups that participated in the first pass.

#### Output
* `partial`: Updated with partial bin counts at `group_id * BINS + bin`.
* `histogram`: `__global uint*` final aggregated 256-bin histogram.

#### Core logic
* **Pass 1 (`histogram_partial`)**:
    * Initializes `local_bins` to zero across the work-group.
    * Iterates through the `input` buffer using a global stride, performing `atomic_inc` on the `local_bins` index corresponding to the byte value.
    * Synchronizes via `barrier` to ensure all counts are complete.
    * Writes the local group results into the `partial` global buffer.
* **Pass 2 (`histogram_reduce`)**:
    * Assigns one work-item per bin (up to 256).
    * Sums the specific bin value across all `group_count` partial histograms.
    * Writes the final total to the `histogram` output buffer.

#### Public API
* `histogram_partial(input, length, partial, local_bins) -> void`: Computes local group histograms.
* `histogram_reduce(partial, group_count, histogram) -> void`: Aggregates partials into final histogram.

#### Dependencies
* OpenCL C 1.2 runtime.
