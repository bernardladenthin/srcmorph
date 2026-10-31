### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:12:49Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers bytes in a fixed-capacity circular queue, accepting and returning only the space or data currently available.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and byte transfer for a ring buffer.
- Uses a power-of-two capacity and reserves one slot to distinguish full from empty.

#### Type
- C implementation file for the `ringbuf` API declared in `ringbuf.h`.

#### Input
- `ringbuf_init(rb, capacity)`: destination `ringbuf_t` and requested capacity.
- `ringbuf_write(rb, src, len)`: source byte buffer and requested byte count.
- `ringbuf_read(rb, dst, len)`: destination byte buffer and requested byte count.
- Query and cleanup functions consume an initialized `ringbuf_t`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t` status.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred.
- Updates `rb->data`, `rb->capacity`, `rb->head`, and `rb->tail`; allocates and frees buffer storage.

#### Core logic
- `is_power_of_two` accepts nonzero capacities with exactly one bit set; initialization returns `RINGBUF_EINVAL` otherwise.
- `ringbuf_init` allocates `capacity` bytes; on allocation failure returns `RINGBUF_ENOMEM`; on success sets capacity and both indices to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts occupancy and the reserved slot from capacity.
- `ringbuf_write` transfers the minimum of requested length and free space, copying in up to two chunks across the array boundary, then advances `head` with a capacity mask.
- `ringbuf_read` transfers the minimum of requested length and used bytes, copying in up to two chunks across the array boundary, then advances `tail` with a capacity mask.
- `ringbuf_free` releases storage and resets the pointer, capacity, and indices to zero.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- Capacity constraint: nonzero power of two; no named configuration variables are defined.
- Status values referenced: `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` (definitions are not present in this file).

#### Dependencies
- `ringbuf.h`: `ringbuf_t`, `ringbuf_status_t`, status values, and API declarations.
- Standard headers: `<stdlib.h>` (`malloc`, `free`), `<string.h>` (`memcpy`).
- Uses `size_t`, `uint8_t`.

#### Exceptions / Errors
- Invalid capacity returns `RINGBUF_EINVAL`.
- Allocation failure returns `RINGBUF_ENOMEM`.
- Read and write transfer only available data or space; they do not signal short transfers as errors.
