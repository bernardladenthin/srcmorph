### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:44:44Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin frequency histogram of byte values from an image or byte stream on the GPU.

#### Purpose
- Builds a 256-bin histogram of uchar input data (e.g., image pixels) across two kernel passes
- Pass 1 accumulates per-work-group partial counts in local memory; Pass 2 reduces them into one global histogram

#### Type
- OpenCL C 1.2 kernel source file (.cl)
- Two `__kernel` functions: `histogram_partial`, `histogram_reduce`
- Uses `__global` buffers and `__local` work-group memory (256 `uint` counters)

#### Input
- `histogram_partial`: `__global const uchar *input` (byte stream), `const uint length`, `__global uint *partial` (output for group partials), `__local uint *local_bins` (256-counter local array)
- `histogram_reduce`: `__global const uint *partial` (pass-1 partials), `const uint group_count` (number of work-groups), `__global uint *histogram` (final 256-bin output)

#### Output
- `histogram_partial`: writes 256 `uint` counters per work-group to `partial[get_group_id(0) * BINS + bin]`
- `histogram_reduce`: writes summed counts to `histogram[bin]` for `bin` in `[0, BINS)`

#### Core logic
- `histogram_partial`:
  - Zero `local_bins[0..BINS-1]` via strided loop over `get_local_id(0)`
  - `barrier(CLK_LOCAL_MEM_FENCE)`
  - Grid-stride loop over input: `atomic_inc(&local_bins[input[i]])` for `i = gid; i < length; i += gsize`
  - `barrier(CLK_LOCAL_MEM_FENCE)`
  - Copy `local_bins` to global `partial` at offset `get_group_id(0) * BINS`
- `histogram_reduce`:
  - Return early if `bin >= BINS`
  - Sum `partial[g * BINS + bin]` for `g` in `[0, group_count)`
  - Store result in `histogram[bin]`

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` — builds per-group partial histograms
- `histogram_reduce(partial, group_count, histogram) -> void` — sums partials into final histogram

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `CLK_LOCAL_MEM_FENCE`, `atomic_inc`
- `BINS` macro (256)

#### Exceptions / Errors
- `histogram_reduce` guards out-of-range bins with early return when `bin >= BINS`
- No explicit error handling; assumes input `length` and buffer sizes match the launch configuration
