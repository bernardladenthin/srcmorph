### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:07:17Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a power‑of‑two sized circular buffer with read/write and memory management.

#### Purpose
- Provides a fixed‑size ring buffer for byte streams.
- Supports initialization, deallocation, reading, writing, and space queries.

#### Type
- C source file implementing `ringbuf_t` operations defined in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (OK, EINVAL, ENOMEM).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_free` releases allocated memory.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates memory, sets head/tail to 0.
- `ringbuf_free` frees memory and resets fields.
- `ringbuf_used` computes used bytes via modulo arithmetic.
- `ringbuf_free_space` computes available space.
- `ringbuf_write` copies up to two contiguous chunks to wrap around.
- `ringbuf_read` copies up to two contiguous chunks from buffer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – free capacity.
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
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
