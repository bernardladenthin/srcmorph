### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T14:44:08Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers and retrieves byte streams in FIFO order using a fixed-capacity circular buffer.

#### Purpose
- Implements initialization, cleanup, usage queries, and byte reads/writes for a ring buffer.
- Reserves one slot to distinguish full from empty, so usable capacity is `capacity - 1`.

#### Type
- C implementation file; exports `ringbuf_*` functions declared in `ringbuf.h`.
- Operates on `ringbuf_t` and returns `ringbuf_status_t` for initialization.

#### Input
- `ringbuf_init`: buffer pointer and requested capacity.
- `ringbuf_write`: buffer, source byte pointer, requested length.
- `ringbuf_read`: buffer, destination byte pointer, requested length.
- Usage queries and `ringbuf_free`: buffer pointer.

#### Output
- `ringbuf_init` returns `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`; on success allocates storage and resets indices.
- `ringbuf_write` returns bytes accepted; `ringbuf_read` returns bytes retrieved.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- Requires initialization capacity to be nonzero and a power of two.
- Allocates `capacity` bytes; sets `head` and `tail` to zero.
- Computes used bytes as `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- Reads and writes no more than the available data or space, respectively.
- Copies each transfer in up to two `memcpy` chunks to handle wrapping at the array boundary.
- Advances `head` or `tail` modulo capacity using the power-of-two mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release storage and reset state.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report queued bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes.
- Internal: `is_power_of_two(size_t n)`, `min_size(size_t a, size_t b)`.

#### Constants and configuration
- No constants, macros, enum values, or configuration variables are defined or read in this file.
- Status codes used: `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK`.

#### Dependencies
- `"ringbuf.h"`: `ringbuf_t`, `ringbuf_status_t`, status codes, and `uint8_t`.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
