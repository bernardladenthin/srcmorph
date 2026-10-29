### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:19:25Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, thread-safe (single-producer, single-consumer) byte ring buffer for data streaming.

#### Purpose
* Implements a circular buffer for byte-stream communication between two independent threads/processes.
* Manages memory for a fixed-size buffer where capacity must be a power of two.

#### Type
C header file defining a `ringbuf_t` struct and associated functions.

#### Input
* `ringbuf_t *rb`: Pointer to the buffer instance.
* `const uint8_t *src`: Source data for writing.
* `uint8_t *dst`: Destination buffer for reading.
* `size_t capacity`: Requested buffer size (must be power of two).
* `size_t len`: Number of bytes to read or write.

#### Output
* `ringbuf_status_t`: Status codes for initialization (OK, EINVAL, ENOMEM).
* `size_t`: Number of bytes successfully read or written.
* `size_t`: Current count of used or free bytes.

#### Core logic
* Uses `volatile` head and tail indices to ensure visibility between producer and consumer.
* Enforces a power-of-two capacity constraint (one slot remains unused to distinguish full/empty states).
* `ringbuf_write`: Advances `head` and copies data from source to `data` array.
* `ringbuf_read`: Advances `tail` and copies data from `data` array to destination.
* `ringbuf_used`: Calculates current occupancy based on head and tail positions.
* `ringbuf_free_space`: Calculates remaining capacity.

#### Public API
* `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes buffer and allocates memory.
* `ringbuf_free(ringbuf_t *rb)`: Deallocates buffer memory.
* `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes bytes to buffer.
* `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads bytes from buffer.
* `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns bytes currently in buffer.
* `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns available bytes for writing.

#### Dependencies
* `stddef.h`
* `stdint.h`
