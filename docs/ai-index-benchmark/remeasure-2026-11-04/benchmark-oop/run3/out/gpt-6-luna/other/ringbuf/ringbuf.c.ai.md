### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:52:43Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Buffers byte streams in a fixed-capacity circular queue, accepting and returning as many bytes as available space or data permits.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and partial byte reads and writes for `ringbuf_t`.
- Uses power-of-two capacity for masked index wraparound and reserves one slot to distinguish full from empty.

#### Type
- C implementation file; exports the `ringbuf_*` functions declared by `ringbuf.h`.

#### Input
- `ringbuf_init`: ring-buffer pointer `rb`, requested byte `capacity`.
- `ringbuf_write`: `rb`, source buffer `src`, requested length `len`.
- `ringbuf_read`: `rb`, destination buffer `dst`, requested length `len`.
- Occupancy queries and `ringbuf_free`: `rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; on success allocates storage and initializes `capacity`, `head`, and `tail`.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred; each may transfer fewer than `len`.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets the buffer fields.

#### Core logic
- `ringbuf_init` rejects capacities that are zero or not powers of two; allocates `capacity` bytes and initializes indices to zero.
- `ringbuf_used` computes wrapped occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts that occupancy and the reserved slot from capacity.
- `ringbuf_write` limits transfer to free space, copies across the array end in at most two chunks, then advances `head` with a capacity mask.
- `ringbuf_read` limits transfer to available bytes, copies across the array end in at most two chunks, then advances `tail` with a capacity mask.
- `ringbuf_free` frees the allocated data and resets pointers, capacity, and indices.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(rb) -> void` — release and reset buffer
- `ringbuf_used(rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(rb) -> size_t` — report writable bytes
- `ringbuf_write(rb, src, len) -> size_t` — append available bytes
- `ringbuf_read(rb, dst, len) -> size_t` — retrieve available bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — initialization status codes; values are not shown.
- No configuration or environment variables.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status codes.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t` — byte-buffer element type.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
- No checks are shown for null pointers or invalid buffer state.
