### histogram.cl
- H: 1.0
- C: C466EA12
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T22:44:08Z
- G: 
- A: 0.0.0
- X: file
---
> Computes a 256-bin byte histogram of an arbitrary input buffer using a two-pass OpenCL kernel (partial per-group counts, then cross-group reduction).

#### Purpose
- Two-pass GPU histogram of a byte stream (image or any `uchar` buffer) into a 256-bin `uint` histogram.
- Designed for large inputs where a single-pass atomic approach on global memory would be too slow.

#### Type
- OpenCL C 1.2 kernel file (`.cl`); two `__kernel` functions, no host code.

#### Input
- `input` (`__global const uchar *`) – source byte stream.
- `length` (`uint`) – number of bytes to process.
- `partial` (`__global uint *`) – scratch buffer sized `group_count * 256` (written by pass 1, read by pass 2).
- `local_bins` (`__local uint *`) – per-work-group scratch, size 256.
- `group_count` (`uint`) – number of work-groups launched in pass 1.

#### Output
- `histogram` (`__global uint *`) – final 256-bin histogram, one `uint` per byte value.
- Side effect: `partial` buffer is written during pass 1.

#### Core logic
- **Pass 1 (`histogram_partial`):**
  - Each work-item in a group cooperatively zeroes `local_bins[0..255]` via strided loop.
  - `barrier(CLK_LOCAL_MEM_FENCE)` synchronizes.
  - Each global work-item strides over `input[0..length-1]` with `atomic_inc(&local_bins[input[i]])` – counts byte values into local memory (fast, no global contention).
  - Second barrier; group cooperatively writes its 256 counters to `partial[group_id * 256 + bin]`.
- **Pass 2 (`histogram_reduce`):**
  - One work-item per bin (global id = bin index, clamped to 256).
  - Serially sums `partial[g * 256 + bin]` over all `group_count` groups.
  - Writes final sum to `histogram[bin]`.

#### Public API
- `histogram_partial(input, length, partial, local_bins) -> void` – per-group local histogram accumulation.
- `histogram_reduce(partial, group_count, histogram) -> void` – cross-group reduction to final histogram.

#### Dependencies
- OpenCL C 1.2 runtime (no external includes).
- Host must allocate `partial` buffer of size `group_count * 256 * sizeof(uint)`.
- Host must launch pass 1 with a 1D NDRange where `local_size` divides 256 (or at least ≤ 256) and `global_size` is a multiple of `local_size`.
- Host must launch pass 2 with `global_size = 256`.

#### Exceptions / Errors
- No explicit error handling in kernel; out-of-bounds `input[i]` would be undefined behavior (host must ensure `length` matches buffer size).
- If `bin >= 256` in pass 2, work-item returns early (guard against over-sized grid).
