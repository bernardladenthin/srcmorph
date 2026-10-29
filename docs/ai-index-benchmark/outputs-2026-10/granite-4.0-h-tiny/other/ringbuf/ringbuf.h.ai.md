### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T00:09:47Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
- Provides a thread-safe, fixed-capacity byte ring buffer for single-producer, single-consumer scenarios.

#### Type
- C header file (`ringbuf.h`), static library implementation.

#### Input
- `ringbuf_t *rb`: pointer to the ring buffer structure.
- `size_t capacity`: fixed capacity of the ring buffer (must be a power of two).

#### Output
- Returns `ringbuf_status_t` indicating success (`RINGBUF_OK`), invalid capacity (`RINGBUF_EINVAL`), or memory allocation failure (`RINGBUF_ENOMEM`).

#### Core logic
- Initializes the ring buffer with a specified capacity.
- Frees resources associated with the ring buffer.
- Writes up to `len` bytes into the buffer, returning the actual number of bytes written.
- Reads up to `len` bytes from the buffer, returning the actual number of bytes read.
- Provides functions to query the current used space and free space in the buffer.

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)`: Initializes the ring buffer.
- `void ringbuf_free(ringbuf_t *rb)`: Frees resources used by the ring buffer.
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)`: Writes data into the ring buffer.
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)`: Reads data from the ring buffer.
- `size_t ringbuf_used(const ringbuf_t *rb)`: Returns the current used space in the buffer.
- `size_t ringbuf_free_space(const ringbuf_t *rb)`: Returns the current free space in the buffer.

#### Dependencies
- `stddef.h`: for `size_t`.
- `stdint.h`: for `uint8_t`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: thrown if the capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: thrown if memory allocation fails during initialization.
