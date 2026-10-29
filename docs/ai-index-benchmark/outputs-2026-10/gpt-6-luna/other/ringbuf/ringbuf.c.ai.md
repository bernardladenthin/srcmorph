### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:21:54Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers and retrieves byte streams in FIFO order using a fixed-capacity circular buffer.

#### Purpose
- Implements ring-buffer initialization, cleanup, occupancy queries, and byte reads/writes.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file; exports `ringbuf_*` functions and defines static helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer and power-of-two capacity.
- `ringbuf_write`: buffer, source byte array, requested length.
- `ringbuf_read`: buffer, destination byte array, requested length.
- Occupancy queries and cleanup: buffer pointer.

#### Output
- `ringbuf_init` returns a status; other operations return byte counts or update buffer state.
- `ringbuf_write` copies into `rb->data`; `ringbuf_read` copies out.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `is_power_of_two` validates nonzero capacities; this enables wraparound with a bit mask.
- `ringbuf_init` allocates `capacity` bytes and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` reserves one slot.
- `ringbuf_write` and `ringbuf_read` limit transfers to available space or used bytes, then copy in up to two chunks across the array boundary.
- Read and write positions advance modulo capacity using `(capacity - 1)` as a mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes.

#### Dependencies
- `ringbuf.h`: `ringbuf_t`, `ringbuf_status_t`, status constants.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails; otherwise returns `RINGBUF_OK`.
- Reads and writes transfer fewer than requested bytes when data or free space is insufficient.
