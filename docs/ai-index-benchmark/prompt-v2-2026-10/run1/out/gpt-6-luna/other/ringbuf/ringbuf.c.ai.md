### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:33:18Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers and retrieves byte streams through a fixed-capacity ring buffer, preserving FIFO order across wraparound.

#### Purpose
- Implements ring-buffer initialization, cleanup, capacity accounting, byte writes, and byte reads.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file; defines exported `ringbuf_*` functions and static helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: ring buffer, source byte pointer, requested length.
- `ringbuf_read`: ring buffer, destination byte pointer, requested length.
- `ringbuf_used` and `ringbuf_free_space`: ring buffer state.

#### Output
- `ringbuf_init` returns a status and initializes allocated buffer state on success.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_free_space` and `ringbuf_used` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` rejects capacities that are not nonzero powers of two, allocates `capacity` bytes, then sets `head` and `tail` to zero.
- Computes used bytes as `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- `ringbuf_write` transfers up to available free space in at most two `memcpy` calls: from `head` to the array end, then from index zero; advances `head` with a capacity mask.
- `ringbuf_read` transfers up to the used byte count in at most two `memcpy` calls: from `tail` to the array end, then from index zero; advances `tail` with a capacity mask.
- `ringbuf_free` frees `data` and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize ring buffer.
- `ringbuf_free(rb) -> void` — release storage and reset state.
- `ringbuf_used(rb) -> size_t` — report buffered byte count.
- `ringbuf_free_space(rb) -> size_t` — report writable byte count.
- `ringbuf_write(rb, src, len) -> size_t` — write available source bytes.
- `ringbuf_read(rb, dst, len) -> size_t` — read available buffered bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- `RINGBUF_EINVAL` — returned when capacity is not a nonzero power of two.
- `RINGBUF_ENOMEM` — returned when allocation fails.
- `RINGBUF_OK` — returned after successful initialization.
- No configuration or environment variables are read.

#### Dependencies
- `ringbuf.h` — ring-buffer types, fields, and status definitions.
- Standard library headers: `stdlib.h`, `string.h`.
- Library functions: `malloc`, `free`, `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for invalid capacity and `RINGBUF_ENOMEM` if `malloc` fails.
- Other functions do not validate pointers or buffer initialization.
