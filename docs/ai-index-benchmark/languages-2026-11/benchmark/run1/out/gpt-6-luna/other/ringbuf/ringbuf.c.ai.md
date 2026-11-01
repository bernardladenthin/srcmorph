### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:16:05Z
- G: 
- A: 0.0.0
- X: file
---
> Buffers byte streams in a fixed-capacity circular queue, accepting and returning as many bytes as available space or data permits.

#### Purpose
- Implements initialization, cleanup, occupancy queries, and partial reads/writes for a byte ring buffer.
- Requires a power-of-two capacity and reserves one slot to distinguish full from empty.

#### Type
- C implementation file; defines exported `ringbuf_*` functions and `static` helpers.
- Uses `ringbuf_t` and `ringbuf_status_t` from `ringbuf.h`.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity`.
- `ringbuf_free`, `ringbuf_used`, `ringbuf_free_space`: buffer pointer.
- `ringbuf_write`: buffer, source byte pointer, requested length.
- `ringbuf_read`: buffer, destination byte pointer, requested length.

#### Output
- Initialization allocates `capacity` bytes and resets `head` and `tail`.
- Read/write return the number of bytes transferred; transfer is limited by available data or space.
- `ringbuf_used` and `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases storage and resets buffer fields.

#### Core logic
- Validates capacity with `is_power_of_two`; invalid capacity returns `RINGBUF_EINVAL`.
- Allocates storage with `malloc`; allocation failure returns `RINGBUF_ENOMEM`, otherwise initialization returns `RINGBUF_OK`.
- Computes used bytes as `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - used`.
- Reads and writes wrap at the array boundary using up to two `memcpy` calls.
- Advances `head` or `tail` modulo capacity using the power-of-two mask.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release and reset buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — report queued bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — report writable bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue available bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue available bytes.
- Internal helpers: `is_power_of_two`, `min_size`.

#### Constants and configuration
- No constants or configuration variables are defined.
- Status codes referenced: `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`, `RINGBUF_OK` (values not shown).

#### Dependencies
- `"ringbuf.h"`: `ringbuf_t`, `ringbuf_status_t`, `uint8_t`, status codes.
- `<stdlib.h>`: `malloc`, `free`.
- `<string.h>`: `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` for zero or non-power-of-two capacity.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails.
