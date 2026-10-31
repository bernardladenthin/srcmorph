### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:59:52Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers byte streams in a fixed-capacity circular queue, allowing partial reads and writes across wraparound.

#### Purpose
- Provides initialization, cleanup, occupancy queries, and byte transfer operations for a ring buffer.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file for the `ringbuf_t` API declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte array, and requested length.
- `ringbuf_read`: buffer, destination byte array, and requested length.
- Occupancy queries and `ringbuf_free`: buffer pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; other transfer and occupancy functions return `size_t`.
- Stores bytes in `rb->data` and updates `rb->head` on writes and `rb->tail` on reads.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates that many bytes, and initializes head and tail to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot and returns `capacity - 1 - used`.
- `ringbuf_write` copies at most the available free space in up to two chunks, wrapping at the array boundary; advances head with a capacity mask.
- `ringbuf_read` copies at most the used bytes in up to two chunks, wrapping at the array boundary; advances tail with a capacity mask.
- `ringbuf_free` releases the data allocation and zeros data, capacity, head, and tail.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- No constants, configuration variables, or environment variables defined.

#### Dependencies
- `ringbuf.h`: `ringbuf_t`, `ringbuf_status_t`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK`.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `size_t`, `uint8_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
