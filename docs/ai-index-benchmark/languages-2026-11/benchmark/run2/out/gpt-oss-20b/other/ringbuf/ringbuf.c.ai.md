### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:42:29Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed‑size circular buffer with power‑of‑two capacity.

#### Purpose
- Provides FIFO storage for byte streams.
- Supports non‑blocking read/write with wrap‑around.

#### Type
- C source file (`ringbuf.c`).
- Implements functions declared in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – source buffer and length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – destination buffer and length.

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
- `ringbuf_used` computes bytes currently stored via modulo arithmetic.
- `ringbuf_free_space` calculates available space.
- `ringbuf_write` writes up to two contiguous chunks: up to end of array, then from start; updates head.
- `ringbuf_read` reads up to two contiguous chunks similarly; updates tail.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – space left.
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
