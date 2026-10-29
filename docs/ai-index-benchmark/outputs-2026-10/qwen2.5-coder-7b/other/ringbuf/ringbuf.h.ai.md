### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:18:03Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a fixed-capacity byte ring buffer for use by a single producer and a single consumer.

#### Type
Header file in C. Exports C functions.

#### Input
- `capacity`: size_t, the buffer's capacity (must be a power of two).
- `src`: const uint8_t*, data to write.
- `dst`: uint8_t*, buffer to read data into.
- `len`: size_t, number of bytes to copy.

#### Output
- `ringbuf_t`: struct, the ring buffer.
- `ringbuf_status_t`: enum, status of operations.
- `size_t`: number of bytes actually copied or used/free space.

#### Core logic
- Initializes the ring buffer with a given capacity.
- Frees the ring buffer.
- Writes up to `len` bytes from `src` to the buffer.
- Reads up to `len` bytes from the buffer into `dst`.
- Returns the number of bytes currently used in the buffer.
- Returns the amount of free space in the buffer.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes the ring buffer.
- `ringbuf_free(ringbuf_t *rb) -> void`: Frees the ring buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes data to the buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads data from the buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns the number of bytes used.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns the free space in the buffer.

#### Dependencies
- `stddef.h`
- `stdint.h`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: Allocation failed.
