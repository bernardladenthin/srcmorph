### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:55:18Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Computes a 256-bin byte histogram from a byte stream using two OpenCL passes: per-group counting and global reduction.

#### Purpose
- Counts byte frequencies in parallel work groups.
- Reduces partial histograms to a single 256-bin histogram.

#### Type
Kernel / OpenCL C 1.2 header; two `__kernel` functions.

#### Input
- `input`: global `uchar` array (byte stream).
- `length`: total number of bytes in `input`.
- `partial`: global array sized `group_count * BINS` (output of pass 1).
- `group_count`: number of work groups (passed to pass 2).
- `histogram`: global `uint` array sized `BINS` (output of pass 2).

#### Output
- `partial`: each work group writes its `BINS` counters at offset `group_id * BINS`.
- `histogram`: final 256-bin histogram stored at `histogram[bin]`.

#### Core logic
- Pass 1 (`histogram_partial`):
  - Initializes local `local_bins` to zero for bins covered by the work group.
  - Iterates over input slice (`gid` to `length` with stride `gsize`), increments `local_bins[input[i]]` atomically.
  - Writes local counts to `partial[group_id * BINS + bin]`.
  - Uses `barrier(CLK_LOCAL_MEM_FENCE)` after init and after counting.
- Pass 2 (`histogram_reduce`):
  - Each work item handles exactly one bin (`bin = global_id`).
  - Sums `partial[g * BINS + bin]` over all groups (`g = 0..group_count-1`).
  - Stores result to `histogram[bin]`.
  - Returns early if `bin >= BINS`.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void`  
  Counts bytes in input slice and writes partial histogram.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void`  
  Reduces all partial histograms to final histogram.

#### Dependencies
- `uchar`, `uint`, `uint length`, `uint group_count` (built-in types).
- OpenCL C kernel primitives: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`, `CLK_LOCAL_MEM_FENCE`.
- Macro `BINS` (defined as 256).

#### Exceptions / Errors
- No explicit exceptions; relies on OpenCL runtime behavior.
- Early return in `histogram_reduce` for bins beyond `BINS-1`.
- Assumes `group_count` matches number of work groups that wrote to `partial`.
- Atomic increment safe for `uint` in OpenCL 1.2.
