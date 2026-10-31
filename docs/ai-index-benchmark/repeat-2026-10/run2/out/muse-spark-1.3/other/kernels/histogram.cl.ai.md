### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:37Z
- G: 
- A: 0.0.0
- X: file
---
> Counts occurrences of each byte value (0-255) in image/byte stream.

#### Purpose
- Two-pass 256-bin byte histogram for OpenCL C 1.2 devices.
- Splits counting into per-work-group partials then global reduction.

#### Type
- OpenCL kernel file, OpenCL C 1.2, defines BINS 256 macro.

#### Input
- histogram_partial: `input` uchar buffer, `length` uint byte count, `partial` uint buffer, `local_bins` local uint[256].
- histogram_reduce: `partial` uint buffer [group_count * BINS], `group_count` uint, `histogram` uint buffer[256].
- Work-item/group IDs, sizes via `get_local_id`, `get_global_id`, `get_group_id`, `get_local_size`, `get_global_size`.

#### Output
- histogram_partial: writes `partial[group_id * BINS + bin]` per work-group.
- histogram_reduce: writes final `histogram[bin]` summed counts.

#### Core logic
- histogram_partial zeroes `local_bins[0..255]` strided by `lid`.
- `barrier(CLK_LOCAL_MEM_FENCE)` synchronizes local initialization.
- Grid-stride loop `i = gid; i < length; i += gsize` with `atomic_inc(&local_bins[input[i]])`.
- Second barrier then coalesced copy `local_bins` to `partial` slice.
- histogram_reduce maps one work-item per `bin`, early return if `bin >= BINS`.
- Accumulates `sum += partial[g * BINS + bin]` over `group_count` groups.

#### Public API
- histogram_partial(input, length, partial, local_bins) -> void — counts per-group partial histogram.
- histogram_reduce(partial, group_count, histogram) -> void — sums partials into final histogram.
