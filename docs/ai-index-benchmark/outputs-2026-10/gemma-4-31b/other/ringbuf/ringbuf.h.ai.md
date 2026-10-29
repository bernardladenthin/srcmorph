### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T21:37:26Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a single-producer single-consumer (SPSC) fixed-capacity byte ring buffer for asynchronous data transfer.

#### Purpose
* Implements a circular byte buffer to decouple data production and consumption.
* Ensures thread-safe access between one producer and one consumer using volatile head/tail indices.

#### Type
Header file (C).

#### Input
* `ringbuf_t` instance pointer.
* `capacity`: Size of the buffer (must be a power of two).
* `src`: Source byte array for writing.
* `dst`: Destination byte array for reading.
* `len`: Number of bytes to transfer.

#### Output
* `ringbuf_status_t`: Initialization status (OK, EINVAL, or ENOMEM).
* `size_t`: Number of bytes actually read or written.
* `size_t`: Current number of used or free bytes in the buffer.

#### Core logic
* Manages a byte array where `head` is the next write index (producer-owned) and `tail` is the next read index (consumer-owned).
* Enforces a power-of-two capacity constraint to facilitate efficient index wrapping.
* Reserves one slot as unused to differentiate between an empty and a full buffer state.
* Provides mechanisms for dynamic memory allocation and cleanup of the internal `data` buffer.

#### Public API
* `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initializes buffer and allocates memory.
* `ringbuf_free(ringbuf_t *rb) -> void`: Deallocates buffer memory.
* `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Copies bytes into the buffer.
* `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Copies bytes out of the buffer.
* `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns count of stored bytes.
* `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns count of available bytes.

#### Dependencies
* stddef.h
* stdint.h

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if capacity is zero or not a power of two.
* `RINGBUF_ENOMEM`: Returned if memory allocation for the buffer fails.
