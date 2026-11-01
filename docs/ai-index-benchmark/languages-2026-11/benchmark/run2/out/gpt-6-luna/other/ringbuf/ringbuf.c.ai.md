### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:38:29Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers byte streams in a fixed-capacity circular buffer, preserving FIFO order across wraparound.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and partial reads and writes for a ring buffer.
- Uses one reserved slot to distinguish full from empty.

#### Type
- C implementation file; defines static helpers and externally visible `ringbuf_*` functions.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: buffer pointer `rb` and requested `capacity`.
- `ringbuf_write`: buffer `rb`, source bytes `src`, and requested length `len`.
- `ringbuf_read`: buffer `rb`, destination bytes `dst`, and requested length `len`.
- Occupancy functions consume `rb`; allocation uses the requested capacity.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`.
- `ringbuf_used`, `ringbuf_free_space`, `ringbuf_write`, and `ringbuf_read` return `size_t`.
- `ringbuf_write` and `ringbuf_read` update buffer indices and copy up to the available space or data.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `is_power_of_two` accepts nonzero capacities with exactly one bit set; initialization rejects other capacities.
- `ringbuf_init` allocates `capacity` bytes and initializes `head` and `tail` to zero.
- `ringbuf_used` computes occupancy using `(head - tail) & (capacity - 1)`; `ringbuf_free_space` leaves one slot unused.
- Reads and writes limit transfer length to available data or space, then copy in up to two chunks to handle wraparound.
- Index updates use a capacity mask, relying on power-of-two capacity.
- `ringbuf_free` frees storage and clears the pointer, capacity, and indices.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report buffered bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — write available bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — read available bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` — status codes returned by `ringbuf_init`; values are not defined here.
- Capacity must be a nonzero power of two; one slot is reserved.

#### Dependencies
- `"ringbuf.h"` — buffer and status declarations.
- `<stdlib.h>` — `malloc`, `free`.
- `<string.h>` — `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for a capacity that is not a nonzero power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
