### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:37:27Z
- G: 
- A: 0.0.0
- X: file
---
> Stores and retrieves byte streams in a bounded circular buffer, preserving FIFO order across wraparound.

#### Purpose
- Allocates and manages a byte-oriented ring buffer.
- Limits reads and writes to available data and free space.

#### Type
- C implementation file; implements the API declared by `ringbuf.h`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, requested `size_t capacity`.
- `ringbuf_write`: buffer, source byte array `src`, requested `len`.
- `ringbuf_read`: buffer, destination byte array `dst`, requested `len`.
- Other operations receive a `ringbuf_t *` or `const ringbuf_t *`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates `capacity` bytes, and initializes `head` and `tail` to zero.
- Tracks used bytes as `(head - tail) & (capacity - 1)`; reserves one slot, so maximum free space is `capacity - 1`.
- `ringbuf_write` copies the minimum of requested length and free space, using up to two `memcpy` calls to handle wraparound, then advances `head` with a capacity mask.
- `ringbuf_read` copies the minimum of requested length and used bytes, using up to two `memcpy` calls to handle wraparound, then advances `tail` with a capacity mask.
- `ringbuf_free` frees the data allocation and resets data, capacity, head, and tail.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer storage.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — retrieve available bytes.
- Internal helpers: `is_power_of_two(size_t n)`, `min_size(size_t a, size_t b)`.

#### Constants and configuration
- No named constants, macros, or configuration variables defined in this file.
- `ringbuf_init` requires `capacity` to be a nonzero power of two.

#### Dependencies
- `"ringbuf.h"`: ring buffer types, status codes, and declarations.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails; otherwise returns `RINGBUF_OK`.
