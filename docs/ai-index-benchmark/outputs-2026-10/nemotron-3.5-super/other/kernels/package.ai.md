### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:54:39Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Computes a 256-bin byte frequency histogram of an image or byte stream using a two-pass OpenCL reduction.

#### Purpose
- Counts byte value frequencies across an input buffer into a 256-bin histogram.
- Splits work into per-work-group partial histograms then reduces them into a single global histogram.

#### Responsibilities
- histogram_partial: builds per-work-group partial byte histograms from an input byte stream using local memory scratch and atomic increments.
- histogram_reduce: reduces per-work-group partial histograms into a final 256-bin global histogram.

#### Key units
- histogram.cl: OpenCL C 1.2 kernel source containing histogram_partial and histogram_reduce kernels with BINS set to 256.
- histogram_partial: __kernel that reads __global const uchar *input and writes per-group partial counts to __global uint *partial using __local uint *local_bins.
- histogram_reduce: __kernel that reads __global const uint *partial and writes final BINS uint counts to __global uint *histogram.

#### Data flow
- Host enqueues histogram_partial with global and local work sizes over an input byte stream.
- histogram_partial zeroes local_bins, strides over input, atomically increments local_bins[input[i]], then copies assigned bins to partial[group_id * BINS + bin].
- Host enqueues histogram_reduce with one work-item per bin over group_count partial slices.
- histogram_reduce sums partial[g * BINS + bin] across all groups and stores the result in histogram[bin].

#### Dependencies
- OpenCL C 1.2 builtins: get_local_id, get_local_size, get_global_id, get_global_size, get_group_id, barrier, atomic_inc, CLK_LOCAL_MEM_FENCE.
- No user-defined imports or external modules.
- Requires partial buffer capacity of at least group_count * BINS uints.
- Requires histogram buffer capacity of at least BINS uints.
- Requires local_bins capacity of at least BINS uints per work-group.

#### Cross-cutting
- Two-pass reduction pattern: partial per-work-group histograms followed by a single global reduction.
- BINS preprocessor constant fixed at 256 across both kernels.
- Kernels return void with no explicit error codes; histogram_reduce exits early when global_id >= BINS.
- Local memory scratch (local_bins) zeroed and synchronized with barrier before and after atomic increments.
