### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:09:41Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
## Purpose
Calculates a 256-bin byte histogram for an image or byte stream in two OpenCL passes.

## Type
Kernel / OpenCL C 1.2

## Input
- uchar *input: Input byte stream
- uint length: Length of input
- __global uint *partial: Partial histogram storage
- __local uint *local_bins: Local bin counters

## Output
- __global uint *histogram: Final 256-bin histogram

## Core logic
- Pass 1: Each work-group counts its slice into local memory, then writes partial histogram.
- Pass 2: One work-item per bin sums partial histograms from all work-groups.

## Public API
histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins) -> void
histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram) -> void

## Dependencies
None (pure OpenCL kernel)

## Exceptions / Errors
None (simple kernel, no error handling)
```
