### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:22:11Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Counts the frequency of every byte value in an image or byte stream using a two-pass 256-bin histogram.

#### Purpose
- Computes byte-value frequencies, producing one count for each value from 0 to 255.
- Uses per-work-group partial histograms followed by a reduction pass.

#### Type
- OpenCL C 1.2 kernel source.

#### Input
- `histogram_partial`: `input` byte buffer, `length`, `partial` output buffer, and caller-provided `local_bins` memory.
- `histogram_reduce`: `partial` buffer and `group_count`.

#### Output
- `partial`: one 256-counter histogram per work-group.
- `histogram`: final 256-counter histogram.

#### Core logic
- `histogram_partial` initializes local bins cooperatively, then synchronizes the work-group.
- Each work-item processes input indices starting at its global ID, stepping by the global size; `atomic_inc` counts each byte in local memory.
- After a second local-memory barrier, work-items write local bin counts to `partial[group_id * BINS + bin]`.
- `histogram_reduce` assigns one work-item per bin, sums that bin across all work-groups, and writes the result; IDs beyond `BINS` return immediately.

#### Public API
- `histogram_partial(__global const uchar *input, const uint length, __global uint *partial, __local uint *local_bins) -> void` — builds per-group histograms.
- `histogram_reduce(__global const uint *partial, const uint group_count, __global uint *histogram) -> void` — combines partial histograms.

#### Constants and configuration
- `BINS = 256`.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL flag: `CLK_LOCAL_MEM_FENCE`.
- OpenCL types and address spaces: `uchar`, `uint`, `__global`, `__local`, `__kernel`.

#### Exceptions / Errors
- No explicit error handling.
