### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:51:47Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte frequency histogram of an image or byte stream using a two-pass OpenCL reduction.

#### Purpose
- Counts byte value frequencies across an input buffer into a 256-bin histogram.
- Splits work into per-work-group partial histograms then reduces them into a single global histogram.

#### Type
- OpenCL C kernel source (OpenCL C 1.2).
- Two __kernel functions: histogram_partial and histogram_reduce.
- Preprocessor constant BINS set to 256.

#### Input
- histogram_partial reads __global const uchar *input byte stream, const uint length, writes to __global uint *partial, uses __local uint *local_bins scratch.
- histogram_reduce reads __global const uint *partial and const uint group_count, writes to __global uint *histogram.
- Host supplies global and local work sizes at enqueue time.

#### Output
- histogram_partial writes BINS uint counters per work-group to partial[group_id * BINS + bin].
- histogram_reduce writes final BINS uint counts to __global uint *histogram.
- Side effects: zeroes local_bins, performs atomic increments on local memory, then copies local bins to global partial buffer.

#### Core logic
- histogram_partial initializes local_bins bins in strided fashion by local id, then barrier.
- Each work-item strides over input by global size and atomically increments local_bins[input[i]], then barrier.
- Each work-item copies its assigned bins from local_bins to global partial output slice.
- histogram_reduce assigns one global work-item per bin, returns if bin >= BINS.
- Each bin work-item sums partial[g * BINS + bin] over all groups and stores result in histogram[bin].

#### Public API
- histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void : builds per-work-group partial byte histogram.
- histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void : reduces partial histograms into final 256-bin histogram.

#### Dependencies
- OpenCL C 1.2 builtins: get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier, atomic_inc, CLK_LOCAL_MEM_FENCE.
- No user-defined imports or external modules.

#### Exceptions / Errors
- Kernels return void; no explicit error codes.
- histogram_reduce exits early for global_id >= BINS.
- Requires partial buffer capacity of at least group_count * BINS uints.
- Requires histogram buffer capacity of at least BINS uints.
- Requires local_bins capacity of at least BINS uints per work-group.
