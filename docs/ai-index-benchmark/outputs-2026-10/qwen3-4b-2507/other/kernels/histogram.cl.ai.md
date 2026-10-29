### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:14:40Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of image or byte data using two OpenCL passes

#### Purpose
- Computes a frequency histogram of byte values (0–255) in input data  
- Uses two-pass parallel processing to efficiently count pixel intensities in GPU kernels

#### Type
Kernel (OpenCL C) – two functions: `histogram_partial` and `histogram_reduce`

#### Input
- `input`: global read-only array of `uchar` (byte values)  
- `length`: total number of bytes to process  
- `partial`: global output buffer for partial histogram per work-group  
- `group_count`: number of work-groups used in the first pass  
- `histogram`: global output buffer for final histogram (size BINS)

#### Output
- `partial`: per-group partial histogram (intermediate)  
- `histogram`: final 256-bin histogram (one value per byte value 0–255)

#### Core logic
- Pass 1: Each work-group computes local bin counts using local memory, then writes to shared partial histogram  
- Uses local memory to reduce memory traffic and improve performance  
- All work-items in a group synchronize via barrier before reading/writing  
- Pass 2: Each work-item computes one bin by summing contributions from all work-groups  
- Final histogram is stored in global memory with one entry per byte value

#### Public API
- `histogram_partial(input, length, partial, local_bins)` → void – computes partial histogram per work-group  
- `histogram_reduce(partial, group_count, histogram)` → void – reduces partial histograms into final result

#### Dependencies
- OpenCL C 1.2  
- `CL_KERNEL` execution context  
- `CL_LOCAL_MEM_FENCE`, `CLK_LOCAL_MEM_FENCE`  
- `atomic_inc` for thread-safe increment operations

#### Exceptions / Errors
- No explicit error handling or exception throwing  
- Out-of-bounds access in `histogram_reduce` is guarded by `if (bin >= BINS)`  
- Input length must be valid; undefined behavior if `length` is negative or zero
