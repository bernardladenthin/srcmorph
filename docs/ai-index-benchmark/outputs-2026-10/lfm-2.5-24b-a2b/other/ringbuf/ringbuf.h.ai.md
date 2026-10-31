### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T12:28:23Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity byte ring buffer for single-producer single-consumer data streaming.

#### Purpose
- Implements a circular byte buffer that supports sequential producer and consumer operations.
- Enables safe in-place data transfer without per-call dynamic allocation.

#### Type
- C struct ringbuf_t holds data pointer, capacity, volatile head (producer index), and volatile tail (consumer index).
- ringbuf_status_t enum defines OK, EINVAL, and ENOMEM status codes.

#### Input
- ringbuf_init: ringbuf_t pointer and capacity.
- ringbuf_write: ringbuf_t pointer, source buffer, length.
- ringbuf_read: ringbuf_t pointer, destination buffer, length.
- ringbuf_used: ringbuf_t pointer.
- ringbuf_free_space: ringbuf_t pointer.
- ringbuf_free: ringbuf_t pointer.

#### Output
- ringbuf_init returns ringbuf_status_t.
- ringbuf_write returns number of bytes copied.
- ringbuf_read returns number of bytes read.
- ringbuf_used returns bytes currently stored.
- ringbuf_free_space returns remaining free bytes.
- ringbuf_free has no return value.

#### Core logic
- ringbuf_init validates capacity (power of two, non-zero) and allocates data; sets head and tail to zero.
- ringbuf_write copies up to len bytes from src to the buffer at head, wraps modulo capacity, advances head, and returns actual count.
- ringbuf_read copies up to len bytes from buffer tail to dst, wraps, advances tail, and returns actual count.
- ringbuf_used computes available bytes as head minus tail with wrap handling.
- ringbuf_free_space returns capacity minus used.
- ringbuf_free releases the allocated buffer.

#### Public API
- ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t: initializes buffer, returns status.
- ringbuf_free(ringbuf_t *rb): frees allocated memory.
- ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t: writes bytes, returns count.
- ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t: reads bytes, returns count.
- ringbuf_used(const ringbuf_t *rb) -> size_t: returns bytes in buffer.
- ringbuf_free_space(const ringbuf_t *rb) -> size_t: returns free space.

#### Dependencies
- stddef.h, stdint.h for standard types.
- Implicit C library functions malloc and free for allocation.

#### Exceptions / Errors
- RINGBUF_EINVAL: capacity is zero or not a power of two.
- RINGBUF_ENOMEM: allocation failed during initialization.
