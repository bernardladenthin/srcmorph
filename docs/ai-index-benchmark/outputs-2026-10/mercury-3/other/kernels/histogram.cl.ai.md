### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:01Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an image or arbitrary byte stream using a two-pass OpenCL kernel (partial per-group counts, then a global reduction).

#### Purpose  
- Produces a 256-bin histogram (`BINS = 256`) of raw byte values from any input buffer.  
- Uses a two-pass GPU-friendly strategy: per-group partial histograms, then a final reduce pass.

#### Type  
- OpenCL C 1.2 kernels (`.cl` file).  
- Contains two `__kernel` functions: `histogram_partial` and `histogram_reduce`.

#### Input  
- `histogram_partial`: `__global const uchar *input` (byte stream), `uint length`, `__global uint *partial` (output buffer), `__local uint *local_bins` (group-local scratch).  
- `histogram_reduce`: `__global const uint *partial` (partial histograms), `uint group_count`, `__global uint *histogram` (final output).

#### Output  
- `histogram_partial` writes `BINS` counters per work-group to `partial[group_id * BINS + bin]`.  
- `histogram_reduce` writes the final 256-bin counts to `histogram[bin]`.

#### Core logic  
- `histogram_partial`:
  - Each work-group zeroes its `local_bins` (coalesced loop over `BINS`).  
  - Barriers with `CLK_LOCAL_MEM_FENCE` ensure initialization completes before counting.  
  - Each global work-item strides over the input with `atomic_inc(&local_bins[input[i]])`.  
  - After a barrier, the group copies `local_bins` to its slice of `partial`.  
- `histogram_reduce`:
  - One work-item per bin (`get_global_id(0)`) sums `partial[g * BINS + bin]` across all `group_count` groups.  
  - Writes the sum to `histogram[bin]`; work-items with `bin >= BINS` return early.

#### Public API  
- `histogram_partial(input, length, partial, local_bins) -> void` — counts bytes into per-group partial histograms.  
- `histogram_reduce(partial, group_count, histogram) -> void` — reduces partial histograms into the final histogram.

#### Dependencies  
- OpenCL C standard (built-in types and functions: `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`).  
- Macro `BINS` (256).

#### Exceptions / Errors  
- No explicit error handling or thrown exceptions; relies on standard OpenCL kernel invocation and memory fencing.
