### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:46:45Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers byte streams in a fixed-capacity circular queue, preserving FIFO order across wraparound.

#### Purpose
- Implements ring-buffer initialization, cleanup, byte writes, byte reads, and occupancy queries.
- Uses one slot as a sentinel, so usable capacity is `capacity - 1`.

#### Type
- C implementation file for the ring-buffer API declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: ring-buffer pointer and requested capacity.
- `ringbuf_write`: ring buffer, source byte array, and requested length.
- `ringbuf_read`: ring buffer, destination byte array, and requested length.
- Occupancy queries: ring-buffer pointer.

#### Output
- Initialization returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- Read and write return the number of bytes transferred, limited by available data or space.
- `ringbuf_free` releases the buffer and resets its fields.
- Read and write update the ring buffer’s `head` or `tail` indices.

#### Core logic
- `is_power_of_two` accepts only nonzero capacities with a single set bit; initialization rejects other capacities.
- `ringbuf_init` allocates `capacity` bytes and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts occupancy and the sentinel slot from capacity.
- `ringbuf_write` copies up to the available space in at most two chunks: from `head` to the array end, then from index zero; advances `head` with a capacity mask.
- `ringbuf_read` copies up to the available data in at most two chunks: from `tail` to the array end, then from index zero; advances `tail` with a capacity mask.
- `ringbuf_free` frees the allocation and clears the data pointer, capacity, and indices.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered byte count.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable byte count.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — consume bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- No constants or configuration variables are defined in this file.
- Referenced status values: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` (definitions are not present here).

#### Dependencies
- `"ringbuf.h"` — ring-buffer types, declarations, and status values.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t` — byte and size types.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
