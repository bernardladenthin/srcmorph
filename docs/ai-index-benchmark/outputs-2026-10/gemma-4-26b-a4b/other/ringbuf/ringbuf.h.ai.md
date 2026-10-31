### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T13:58:41Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity byte ring buffer for single-producer single-consumer communication.

#### Purpose
* Implements a lock-free circular buffer for asynchronous data transfer between one producer and one consumer.
* Manages byte-level data storage using power-of-two capacity constraints.

#### Type
Header file (C/C++)

#### Input
* `ringbuf_t` structure containing data pointer, capacity, head index, and tail index.
* `capacity` parameter (must be a power of two).
* `src` byte arrays for writing.
* `dst` byte arrays for reading.
* `len` size parameters for read/write operations.

#### Output
* `ringbuf_status_t` status codes for initialization.
* `size_t` counts of bytes actually written or read.
* `size_t` counts of currently used or free bytes.

#### Core logic
* Uses `head` (producer-owned) and `tail` (consumer-owned) indices to track buffer state.
* Enforces power-of-two capacity, resulting in an effective storage of `capacity - 1` bytes.
* Utilizes `volatile` qualifiers on indices to support single-producer single-consumer concurrency.

#### Public API
* `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` Initializes the ring buffer.
* `ringbuf_free(ringbuf_t *rb) -> void` Releases allocated buffer memory.
* `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` Writes bytes into the buffer.
* `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` Reads bytes from the buffer.
* `ringbuf_used(const ringbuf_t *rb) -> size_t` Returns number of bytes currently stored.
* `ringbuf_free_space(const ringbuf_t *rb) -> size_t` Returns available bytes for writing.

#### Dependencies
* `stddef.h`
* `stdint.h`

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if capacity is zero or not a power of two.
* `RINGBUF_ENOMEM`: Returned if memory allocation fails during initialization.
