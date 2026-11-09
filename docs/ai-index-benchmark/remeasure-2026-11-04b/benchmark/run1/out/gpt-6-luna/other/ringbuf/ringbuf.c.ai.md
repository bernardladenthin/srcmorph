### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:49:32Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Stores and retrieves byte streams in a bounded circular buffer while preserving FIFO order.

#### Purpose
- Implements byte-oriented ring-buffer initialization, cleanup, occupancy queries, writes, and reads.
- Uses one unused slot to distinguish full from empty.

#### Type
- C implementation file; exports `ringbuf_*` functions declared in `ringbuf.h`.

#### Input
- `ringbuf_init(rb, capacity)`: buffer state pointer and requested capacity.
- `ringbuf_write(rb, src, len)`: source bytes and requested write length.
- `ringbuf_read(rb, dst, len)`: destination buffer and requested read length.
- Query and cleanup functions take `rb`.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; other operations return byte counts or `void`.
- Mutates `rb->data`, `capacity`, `head`, and `tail`; allocates and frees the backing buffer.

#### Core logic
- `ringbuf_init` accepts only nonzero power-of-two capacities, allocates storage, and initializes indices to zero.
- `ringbuf_used` computes occupancy as `(head - tail) & (capacity - 1)`; `ringbuf_free_space` subtracts occupancy and the reserved slot from capacity.
- `ringbuf_write` writes up to available space, copying in up to two chunks across the array boundary, then advances `head` with a capacity mask.
- `ringbuf_read` reads up to available data using the same two-chunk wraparound strategy, then advances `tail` with a capacity mask.
- `ringbuf_free` releases storage and resets all buffer fields.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report occupied bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — append bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — remove bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — status codes returned by `ringbuf_init`; values are not defined in this file.
- Capacity must be a nonzero power of two.

#### Dependencies
- `"ringbuf.h"` — ring-buffer types and status definitions.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
