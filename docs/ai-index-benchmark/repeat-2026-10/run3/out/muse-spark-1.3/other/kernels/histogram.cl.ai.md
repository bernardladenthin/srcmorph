### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:24:01Z
- G: 
- A: 0.0.0
- X: file
---
> Counts byte-value frequencies into 256-bin histogram for images or byte streams.

#### Purpose
- Two-pass parallel byte histogram with per-work-group partials plus final reduction
- Supports arbitrary byte stream via strided counting and bin-wise summation

#### Type
- OpenCL C 1.2 kernel file with two `__kernel` entry points, `BINS` 256 macro

#### Input
- `input` global `uchar` buffer, byte stream / image pixels
- `length` uint byte count, loop bound for strided access
- `partial` global `uint` buffer, `group_count * BINS` intermediate counters
- `local_bins` local `uint[256]` per-work-group scratch histogram
- `group_count` uint number of Pass 1 work-groups
- `histogram` global `uint[256]` final output buffer

#### Output
- `partial[group_id * BINS + bin]` per-group BINS counters from `histogram_partial`
- `histogram[bin]` summed 256-bin frequencies from `histogram_reduce`

#### Core logic
- `histogram_partial` zeroes `local_bins` cooperatively by `lid` stride over `BINS`
- `histogram_partial` strided scan `i = gid; i < length; i += gsize` with `atomic_inc` on `local_bins[input[i]]`
- `histogram_partial` coalesced write of `local_bins` to `partial` slice after `CLK_LOCAL_MEM_FENCE` barriers
- `histogram_reduce` maps one work-item per `bin = get_global_id(0)` with `bin >= BINS` early exit
- `histogram_reduce` accumulates `sum += partial[g * BINS + bin]` over all groups into `histogram[bin]`

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` - builds per-group partial histograms
- `histogram_reduce(partial, group_count, histogram) -> void` - sums partials into final histogram
