### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:22:55Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers and retrieves byte streams in FIFO order using a fixed-size circular buffer.

#### Purpose
- Implements ring-buffer initialization, cleanup, occupancy queries, and byte reads/writes.
- Leaves one slot unused to distinguish full from empty.

#### Type
- C implementation file for the `ringbuf.h` ring-buffer API.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte array, and requested length.
- `ringbuf_read`: buffer, destination byte array, and requested length.
- Occupancy queries: buffer state (`capacity`, `head`, `tail`).

#### Output
- `ringbuf_init`: initializes allocated storage and indices; returns a status.
- `ringbuf_used` / `ringbuf_free_space`: report byte counts.
- `ringbuf_write` / `ringbuf_read`: return the number of bytes transferred.
- `ringbuf_free`: releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities; allocates `capacity` bytes and initializes `head` and `tail` to zero.
- Tracks occupancy as `(head - tail) & (capacity - 1)`; available space is `capacity - 1 - used`.
- `ringbuf_write` copies up to available space, splitting the copy at the array boundary when needed; advances `head` with a capacity mask.
- `ringbuf_read` copies up to the used byte count, splitting at the array boundary when needed; advances `tail` with a capacity mask.
- `ringbuf_free` frees the data allocation and resets the pointer, capacity, and indices.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report queued bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes

#### Dependencies
- `"ringbuf.h"` — buffer types, status codes, API declarations
- `<stdlib.h>` — `malloc`, `free`
- `<string.h>` — `memcpy`

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
