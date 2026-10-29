### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:21:50Z
- G: 
- A: 0.0.0
- X: file
---
> Counts the frequency of each byte value in an image or byte stream using a two-pass OpenCL histogram.

#### Purpose
- Computes a 256-bin histogram of input bytes.
- Uses per-work-group partial histograms followed by a reduction pass.

#### Type
- OpenCL C 1.2 kernel source; defines `BINS` as 256.

#### Input
- `histogram_partial`: byte buffer `input`, byte `length`, output buffer `partial`, dynamically allocated local-memory `local_bins`.
- `histogram_reduce`: partial histogram buffer `partial`, number of work-groups `group_count`.

#### Output
- `histogram_partial` writes 256 counters per work-group to `partial`.
- `histogram_reduce` writes the final 256-bin result to `histogram`.

#### Core logic
- `histogram_partial` initializes local counters cooperatively, then uses a local-memory atomic increment for each assigned input byte.
- Work-items stride through input by global size; each work-group aggregates the work-items’ counts and writes its partial histogram at `group_id * BINS`.
- `histogram_reduce` assigns one work-item per bin and sums that bin across all work-groups.
- Reduction work-items whose bin index is at least 256 return without writing.

#### Public API
- `histogram_partial(__global const uchar *input, uint length, __global uint *partial, __local uint *local_bins) -> void` — generate per-group byte counts.
- `histogram_reduce(__global const uint *partial, uint group_count, __global uint *histogram) -> void` — combine group counts by bin.

#### Dependencies
- OpenCL built-ins: `get_local_id`, `get_local_size`, `get_global_id`, `get_global_size`, `get_group_id`, `barrier`, `atomic_inc`.
- OpenCL memory spaces and flags: `__global`, `__local`, `CLK_LOCAL_MEM_FENCE`.
