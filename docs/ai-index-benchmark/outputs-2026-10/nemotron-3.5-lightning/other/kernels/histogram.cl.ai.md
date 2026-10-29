### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:08:22Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin byte histogram from an image or byte stream using two-pass OpenCL reduction.

#### Purpose
- Computes 256-bin histogram for byte-level data distribution using OpenCL two-pass reduction

#### Type
- OpenCL C 1.2 kernel file with two `__kernel` functions: `histogram_partial` and `histogram_reduce`
- Functions process `__global const uchar *input` with 256 `BINS` counters

#### Input
- `__global const uchar *input`: byte stream or image data to histogram
- `const uint length`: number of bytes in input
- `__global uint *partial`: output buffer for per-group partial histograms
- `__local uint *local_bins`: per-work-group local memory for accumulation

#### Output
- `__global uint *partial`: per-group partial histograms at `partial[group_id * BINS + bin]`
- `__global uint *histogram`: final 256-bin histogram with sums across all groups

#### Core logic
- Pass 1: work-group zeroes `local_bins` via loop, `CLK_LOCAL_MEM_FENCE`, then `atomic_inc` each byte into local counters, fence, write partial slice to global `partial`
- Pass 2: each work-item iterates `g` from `0` to `group_count-1`, sums `partial[g * BINS + bin]` into `histogram[bin]`, guarded by `bin >= BINS`

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void`: accumulates partial histogram into global buffer
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void`: reduces partial histograms into final histogram

#### Dependencies
- OpenCL C 1.2
- `atomic_inc`
- Types: `uchar`, `uint`
- Constants: `BINS 256`
- Buffers: `partial`, `local_bins`, `histogram`

#### Exceptions / Errors
- No language-level exceptions thrown
- OpenCL runtime errors possible via host API
- Out-of-bounds guard `bin >= BINS` in `histogram_reduce` prevents out-of-range access
