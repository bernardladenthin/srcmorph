### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T05:12:31Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity single-producer/single-consumer byte ring buffer.

#### Purpose
- Provides a thread-safe ring buffer for efficient data exchange between a single producer and consumer.
- Guarantees no memory allocations after initialization.

#### Type
- Header file (ringbuf.h) defining a C API.
- No class or struct definitions; uses `ringbuf_t` struct.

#### Input
- `ringbuf_init`: `rb` pointer, `capacity` (power of two).
- `ringbuf_write`: `rb` pointer, `src` buffer, `len` bytes.
- `ringbuf_read`: `rb` pointer, `dst` buffer, `len` bytes.

#### Output
- `ringbuf_init`: status code (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_write`: bytes written.
- `ringbuf_read`: bytes read.
- `ringbuf_used`: bytes currently stored.
- `ringbuf_free_space`: available capacity.

#### Core logic
- Uses circular indexing with `head` and `tail` pointers.
- `head` owned by producer, `tail` by consumer.
- Capacity must be power of two; one slot unused.
- `ringbuf_write` and `ringbuf_read` copy data and update pointers atomically.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: initializes buffer.
- `ringbuf_free(ringbuf_t *rb) -> void`: frees buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: writes data.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: reads data.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: returns used bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: returns free space.

#### Dependencies
- `<stddef.h>`: `size_t`.
- `<stdint.h>`: `uint8_t`, `uint32_t`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` if capacity invalid.
- `ringbuf_init` returns `RINGBUF_ENOMEM` if allocation fails (not applicable here).
