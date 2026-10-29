### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:47:15Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates a 256-bin histogram for byte data in two OpenCL passes.

#### Purpose
- Counts byte values into 256 bins for a byte stream or image region.
- Produces per-group partial totals and a final aggregated histogram.

#### Type
- Kernel file; OpenCL C 1.2.
- Exported kernels: `histogram_partial`, `histogram_reduce`.
- Uses `__kernel`, `__global`, `__local`, `uchar`, `uint`.

#### Input
- `input`: `__global const uchar *` byte data source.
- `length`: `const uint` number of input elements processed.
- `partial`: `__global uint *` buffer for per-group partial histograms.
- `local_bins`: `__local uint *` scratch area for local bin counts.
- `group_count`: `const uint` number of groups contributing to the reduction.
- `histogram`: `__global uint *` output histogram buffer.

#### Output
- Writes partial counts to `partial[group_id * BINS + bin]` in `histogram_partial`.
- Writes final per-bin totals to `histogram[bin]` in `histogram_reduce`.
- No return values; side effect is buffer population.

#### Core logic
- `histogram_partial`:
  - Computes `lid`, `lsize`, `gid`, `gsize`.
  - Clears `local_bins[0..255]` with a loop over `b = lid; b < BINS; b += lsize`.
  - Synchronizes with `barrier(CLK_LOCAL_MEM_FENCE)`.
  - Iterates `for (uint i = gid; i < length; i += gsize)` and increments `local_bins[input[i]]` with `atomic_inc`.
  - Synchronizes again.
  - Copies each `local_bins[b]` to `partial + get_group_id(0) * BINS`.
- `histogram_reduce`:
  - Uses one work-item per bin via `bin = get_global_id(0)`.
  - Exits early if `bin >= BINS`.
  - Initializes `sum = 0`.
  - Sums `partial[g * BINS + bin]` for `g = 0 .. group_count - 1`.
  - Writes final total to `histogram[bin]`.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — counts group slice
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — sums group totals

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `atomic_inc`, `barrier`.
- OpenCL constants: `CLK_LOCAL_MEM_FENCE`.
- Macro: `BINS` defined as `256`.

#### Exceptions / Errors
- No explicit exception handling.
- `histogram_reduce` handles `bin >= BINS` by returning early.
