### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:17:02Z
- G: 
- A: 0.0.0
- X: file
---
> Counts 256-bin byte-value frequencies for images or byte streams.
#### Purpose
- Two-pass parallel byte histogram for image / byte-stream frequency analysis.
- Splits counting into per-work-group partials then global reduction.
#### Type
- OpenCL C 1.2 kernel file with 2 kernels, macro `BINS` 256.
#### Input
- `input` global `uchar` buffer, byte stream / image pixels.
- `length` uint, input element count.
- `partial` global `uint` buffer, `group_count * BINS` counters.
- `local_bins` local `uint[256]`, per-work-group scratch.
- `group_count` uint, number of partial histograms.
- `histogram` global `uint` buffer, final 256 bins.
- OpenCL IDs/sizes: `get_global_id`, `get_group_id`, `get_local_id`, `get_local_size`, `get_global_size`.
#### Output
- `partial[group_id * BINS + bin]` per-group partial histograms from `histogram_partial`.
- `histogram[bin]` final summed 256-bin counts from `histogram_reduce`.
#### Core logic
- `histogram_partial` zeroes `local_bins` strided by `lid`.
- `barrier(CLK_LOCAL_MEM_FENCE)` synchronizes local initialization.
- Grid-stride loop `i = gid; i < length; i += gsize` with `atomic_inc(&local_bins[input[i]])`.
- Second barrier then strided copy `local_bins` to `partial` group slice.
- `histogram_reduce` assigns one work-item per `bin`.
- Loops `g < group_count` accumulating `partial[g * BINS + bin]` into `sum`.
- Writes `sum` to `histogram[bin]`.
#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` builds per-group partial histograms.
- `histogram_reduce(partial, group_count, histogram) -> void` sums partials into final histogram.
#### Dependencies
- No includes/imports, self-contained OpenCL builtins only.
- Builtins: `barrier`, `atomic_inc`, `CLK_LOCAL_MEM_FENCE`, ID/size queries.
