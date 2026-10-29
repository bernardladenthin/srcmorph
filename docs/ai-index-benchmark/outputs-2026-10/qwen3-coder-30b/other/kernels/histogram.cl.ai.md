### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:18:47Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin histogram of byte data in two passes using OpenCL local and global memory.

#### Purpose
- Calculates frequency distribution of byte values from input data
- Processes large byte streams efficiently using parallel OpenCL kernels

#### Type
OpenCL C kernel file, language: OpenCL C 1.2

#### Input
- `input`: global memory pointer to byte stream data
- `length`: total number of bytes to process
- `partial`: global memory for intermediate histogram results
- `group_count`: number of work groups used in first pass
- `local_bins`: local memory scratch space for each work-group

#### Output
- `histogram`: global memory output array with 256 bin counts
- `partial`: intermediate histogram data written per work-group

#### Core logic
- First pass: each work-group processes slice of input data, accumulates counts in local memory, writes partial histogram to global memory
- Second pass: single work-item per histogram bin sums all partial histograms from work-groups
- Uses atomic operations for thread-safe counting in local memory
- Employs barriers for synchronization between work-items in work-group

#### Public API
- `histogram_partial(input, length, partial, local_bins)`: computes partial histogram per work-group
- `histogram_reduce(partial, group_count, histogram)`: reduces partial histograms to final result

#### Dependencies
- OpenCL 1.2 built-in functions: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`
- Memory space qualifiers: `__global`, `__local`

#### Exceptions / Errors
- No explicit error handling
- Kernel execution assumes valid memory pointers and sufficient global/local memory allocation
- Input length and group count must be properly specified for correct operation
