### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:45:44Z
- G: 
- A: 0.0.0
- X: file
- P: 0732CA46
---
> Stores and retrieves byte streams through a bounded circular buffer while preserving FIFO order.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and byte reads/writes for `ringbuf_t`.
- Uses a power-of-two buffer with one slot reserved to distinguish full from empty.

#### Type
- C implementation file; exports the `ringbuf_*` functions declared in `ringbuf.h`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, requested `size_t capacity`.
- `ringbuf_write`: buffer, source byte pointer `src`, requested length `len`.
- `ringbuf_read`: buffer, destination byte pointer `dst`, requested length `len`.
- Occupancy queries and cleanup take a `ringbuf_t` pointer.

#### Output
- `ringbuf_init` returns a `ringbuf_status_t`; allocates storage and initializes indices on success.
- `ringbuf_write` and `ringbuf_read` return the number of bytes transferred, which may be less than requested.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- `ringbuf_init` rejects zero or non-power-of-two capacities; allocates `capacity` bytes and sets `head` and `tail` to zero.
- Computes used bytes as `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- `ringbuf_write` limits transfer to free space; copies across the array boundary in at most two chunks, then advances `head` with a capacity mask.
- `ringbuf_read` limits transfer to used bytes; copies across the boundary in at most two chunks, then advances `tail` with a capacity mask.
- `ringbuf_free` frees the data allocation and resets `data`, `capacity`, `head`, and `tail`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report stored bytes
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes
- Internal helpers: `is_power_of_two`, `min_size`

#### Constants and configuration
- `RINGBUF_EINVAL` — returned for invalid capacity.
- `RINGBUF_ENOMEM` — returned when allocation fails.
- `RINGBUF_OK` — returned on successful initialization.
- No configuration or environment variables are read.

#### Dependencies
- `"ringbuf.h"` — buffer types, status type, and status codes.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.
- `uint8_t`, `size_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if `malloc` fails.
