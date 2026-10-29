### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:15:07Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed‑size circular buffer with power‑of‑two capacity.

#### Purpose
- Provides FIFO storage for byte streams.
- Supports efficient wrap‑around reads and writes.

#### Type
- C source file implementing `ringbuf_t` operations defined in `ringbuf.h`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – capacity in bytes.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – data to enqueue.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – buffer to dequeue into.

#### Output
- `ringbuf_init` returns status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write` returns number of bytes written.
- `ringbuf_read` returns number of bytes read.
- `ringbuf_free` releases allocated memory.

#### Core logic
- `is_power_of_two` checks capacity validity.
- `min_size` selects smaller of two sizes.
- `ringbuf_init` allocates memory, initializes indices.
- `ringbuf_free` frees memory and resets fields.
- `ringbuf_used` computes occupied bytes via modulo arithmetic.
- `ringbuf_free_space` calculates remaining capacity.
- `ringbuf_write` copies data in up to two contiguous chunks to handle wrap‑around.
- `ringbuf_read` copies data similarly, updating tail pointer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` – initialize buffer.
- `void ringbuf_free(ringbuf_t *rb)` – free resources.
- `size_t ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `size_t ringbuf_free_space(const ringbuf_t *rb)` – available space.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue data.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue data.

#### Dependencies
- `ringbuf.h` (declaration of `ringbuf_t`, `ringbuf_status_t`, status codes).
- Standard library headers: `<stdlib.h>`, `<string.h>`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity not power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` on memory allocation failure.
