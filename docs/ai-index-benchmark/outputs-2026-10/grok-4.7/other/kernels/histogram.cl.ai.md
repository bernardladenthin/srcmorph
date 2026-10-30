### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:44:41Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense, structured markdown summary of the OpenCL histogram source.> Builds a 256-bin byte histogram of an image or byte stream in two OpenCL passes.

#### Purpose
- Count occurrences of each byte value (0–255) in an input buffer via a work-group partial histogram and a bin-wise reduction.
- Two-pass OpenCL C 1.2 design: local atomic counts, then sum partial histograms into a final 256-bin result.

#### Type
- OpenCL C kernel source (implementation); two `__kernel` functions, no host API.
- Constant `BINS` is 256.

#### Input
- `histogram_partial`: `__global const uchar *input` byte stream; `uint length`; `__global uint *partial` destination for per-group histograms; `__local uint *local_bins` work-group scratch of `BINS` counters.
- `histogram_reduce`: `__global const uint *partial` (layout `group_id * BINS + bin`); `uint group_count`.
- Work-item IDs from `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`.

#### Output
- `histogram_partial` writes one partial histogram of 256 `uint` counters at `partial[get_group_id(0) * BINS + bin]`.
- `histogram_reduce` writes the final 256-bin histogram into `__global uint *histogram`.
- No return values; side effects are global buffer stores. Local bins are zeroed then updated with `atomic_inc`.

#### Core logic
- Pass 1 (`histogram_partial`): each local work-item zeros a strided slice of `local_bins`, then `barrier(CLK_LOCAL_MEM_FENCE)`.
- Grid-stride loop over `input[0 .. length)`: `atomic_inc(&local_bins[input[i]])` so each byte increments its bin in local memory.
- Second local barrier, then the group writes `local_bins` into its slice of `partial`.
- Pass 2 (`histogram_reduce`): one work-item per bin; bins `>= BINS` return immediately.
- For each group `g` in `group_count`, add `partial[g * BINS + bin]` and store the sum in `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — work-group partial byte counts.
- `histogram_reduce(partial, group_count, histogram) -> void` — sum partials into 256 bins.

#### Dependencies
- OpenCL C 1.2 built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`.
- Types: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Exceptions / Errors
- No explicit error returns. `histogram_reduce` no-ops when `bin >= BINS`.
- Correctness depends on `local_bins` covering 256 bins, `partial` sized for all groups, and matching `group_count`.
