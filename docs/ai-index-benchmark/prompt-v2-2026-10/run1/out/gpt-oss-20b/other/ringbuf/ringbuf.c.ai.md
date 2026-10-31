### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:40:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a power‑of‑two sized circular buffer with read/write and size queries.

#### Purpose
- Implements a ring buffer for byte streams.
- Supports initialization, deallocation, reading, writing, and space usage.

#### Type
- C source file implementing `ringbuf_t` operations (header `ringbuf.h`).

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – data to write.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – buffer to read into.
- `ringbuf_used(const ringbuf_t *rb)` – query used bytes.
- `ringbuf_free_space(const ringbuf_t *rb)` – query free bytes.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_free` deallocates internal buffer.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates buffer, sets head/tail to 0.
- `ringbuf_free` frees buffer and resets fields.
- `ringbuf_used` computes used bytes via modulo arithmetic.
- `ringbuf_free_space` calculates remaining capacity.
- `ringbuf_write` writes up to two contiguous chunks (wrap‑around).
- `ringbuf_read` reads up to two contiguous chunks (wrap‑around).

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)` – used bytes.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – free bytes.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read data.

#### Constants and configuration
- None defined in this file.

#### Dependencies
- `<stdlib.h>` – `malloc`, `free`.
- `<string.h>` – `memcpy`.
- `ringbuf.h` – type and status definitions.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` on allocation failure.
