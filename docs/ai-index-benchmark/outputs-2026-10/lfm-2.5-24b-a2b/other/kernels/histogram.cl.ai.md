### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:24:50Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte frequency histogram over an input byte stream using two-pass OpenCL kernels.

#### Purpose
- Generates byte frequency histogram for image or byte stream data.

#### Type
- OpenCL C 1.2 kernel source; two __kernel functions; uses __global and __local memory.

#### Input
- histogram_partial: input (const uchar *input), length (const uint length), partial (__global uint *partial), local_bins (__local uint *local_bins).
- histogram_reduce: partial (__global uint *partial), group_count (const uint group_count), histogram (__global uint *histogram).

#### Output
- histogram_partial writes partial histogram to global array per workgroup.
- histogram_reduce writes final 256-bin histogram to histogram array.

#### Core logic
- Pass 1: Reset local_bins for BINS bins, atomically increment counters per byte, write partial to global.
- Pass 2: Sum partial values across group_count workgroups for each of the BINS bins.

#### Public API
- histogram_partial(input, length, partial, local_bins) -> void: Computes per-workgroup byte histogram.
- histogram_reduce(partial, group_count, histogram) -> void: Aggregates partial histograms across groups.

#### Dependencies
- OpenCL C 1.2; atomic_inc, barrier, get_local_id, get_global_id, get_group_id, get_local_size, get_global_size.

#### Exceptions / Errors
- No explicit error handling; relies on correct OpenCL environment and atomic operations.
- Potential undefined behavior if group_count is zero or buffers are uninitialized.
