### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:53:57Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram for an image or byte stream using OpenCL parallel reduction across two passes.

#### Purpose
Computes a 256-bin histogram from raw byte data using a two-pass OpenCL kernel design for parallel efficiency.

#### Type
OpenCL C 1.2 kernel file; implements two `__kernel` functions: `histogram_partial` and `histogram_reduce`.

#### Input
- `__global const uchar *input`: pointer to source byte data (image or byte stream)
- `const uint length`: total number of bytes to process
- `__global uint *partial`: pre-allocated output buffer for partial histograms
- `__local uint *local_bins`: local memory buffer for work-group accumulation (size BINS=256)

#### Output
- `__global uint *histogram`: final accumulated 256-bin histogram (Pass 2 output)
- Partial histograms written to `partial` buffer during Pass 1

#### Core logic
- **Pass 1 (`histogram_partial`)**: 
  - Each work-group zeroes its local `local_bins` via a loop over BINS with stride `lsize`
  - Barrier synchronizes local memory
  - Each work-item atomically increments `local_bins[input[i]]` for its assigned bytes (strided by `gsize`)
  - Barrier synchronizes local memory
  - Each work-group writes its partial histogram slice to `partial[group_id * BINS + bin]` from local memory
- **Pass 2 (`histogram_reduce`)**: 
  - One work-item per bin iterates over all `group_count` partial histograms
  - Sums values at offset `g * BINS + bin` into a single `histogram[bin]`

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void`: performs Pass 1 partial histogram accumulation
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void`: performs Pass 2 global reduction across partial histograms

#### Dependencies
- OpenCL C 1.2 runtime
- `atomic_inc` for thread-safe local memory increments
- `barrier(CLK_LOCAL_MEM_FENCE)` for work-group synchronization

#### Exceptions / Errors
- Out-of-bounds access guarded in `histogram_reduce` via `if (bin >= BINS) return;`
