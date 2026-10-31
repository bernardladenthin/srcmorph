### other/kernels
- H: 1.0
- C: 9B435E58
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:01Z
- G: 
- A: 0.0.0
- X: package
- F: [histogram.cl](histogram.cl.ai.md)
---
> Byte-value frequency counting for image/byte streams via 256-bin histogram.
#### Purpose
- Two-pass 256-bin byte histogram for OpenCL C 1.2 devices.
#### Responsibilities
- Per-work-group partial counting from `input` uchar buffer into `partial` slices.
- Global reduction of per-group partials into final 256-entry `histogram`.
#### Key units
- `histogram_partial(input, length, partial, local_bins)` — grid-stride counting with `atomic_inc` into `local_bins[256]`, writes `partial[group_id * BINS + bin]`.
- `histogram_reduce(partial, group_count, histogram)` — one work-item per `bin`, sums `partial[g * BINS + bin]` into `histogram[bin]`.
- `BINS 256` macro — fixed bin count for byte values 0-255.
#### Data flow
- `input` + `length` -> strided loop `i = gid; i < length; i += gsize` -> `local_bins[input[i]]` -> `partial[group_count * BINS]` -> `histogram[256]`.
#### Dependencies
- OpenCL C 1.2 builtins: `get_local_id`, `get_global_id`, `get_group_id`, `get_local_size`, `get_global_size`, `barrier(CLK_LOCAL_MEM_FENCE)`, `atomic_inc`.
#### Cross-cutting
- Two-kernel split: partials then reduction to avoid global atomics.
- `barrier(CLK_LOCAL_MEM_FENCE)` after zeroing `local_bins` and after counting, before coalesced copy to `partial`.
- Grid-stride access and strided zero/copy by `lid`; early return if `bin >= BINS` in reduce.
