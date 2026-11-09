### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T12:10:59Z
- G: 
- A: 0.0.0
- X: file
- P: C7A82429
---
> Implements a fixed‑size circular buffer with power‑of‑two capacity.

#### Purpose
- Provides a FIFO buffer for byte streams.
- Supports non‑blocking read/write with wrap‑around.

#### Type
- C source file (`ringbuf.c`) implementing functions for `ringbuf_t` defined in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – data to enqueue.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – buffer to dequeue into.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` returns number of bytes actually written.
- `ringbuf_read` returns number of bytes actually read.
- `ringbuf_free` releases allocated memory.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates memory, initializes indices.
- `ringbuf_free` frees memory and resets fields.
- `ringbuf_used` computes bytes in buffer via modulo arithmetic.
- `ringbuf_free_space` computes available space.
- `ringbuf_write` copies up to two contiguous chunks to handle wrap‑around.
- `ringbuf_read` copies up to two contiguous chunks from buffer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – space left.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue data.

#### Constants and configuration
- None defined in this file.

#### Dependencies
- `ringbuf.h`
- Standard library: `stdlib.h`, `string.h`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if memory allocation fails.
