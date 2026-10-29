### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:54:27Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, single-producer single-consumer (SPSC) byte ring buffer.

#### Purpose
* Provides a lock-free circular buffer for byte-stream data transfer between two threads/contexts.
* Manages memory and indexing for a fixed-size FIFO queue.

#### Type
Header file; C.

#### Input
* `capacity`: Size of the buffer (must be a power of two).
* `src`: Byte array for writing to the buffer.
* `dst`: Byte array for reading from the buffer.
* `len`: Number of bytes to attempt to read or write.

#### Output
* `ringbuf_status_t`: Initialization status (OK, EINVAL, or ENOMEM).
* `size_t`: Number of bytes actually copied or current space metrics.
* `ringbuf_t`: Initialized state containing data pointer, capacity, head, and tail indices.

#### Core logic
* Uses a power-of-two capacity to allow efficient wrapping of indices.
* Maintains a `head` index (producer-owned) for writes and a `tail` index (consumer-owned) for reads.
* Reserves one slot as unused to distinguish between full and empty states.
* Implements volatile indexing to ensure visibility across producer and consumer contexts.

#### Public API
* `ringbuf_init(ringbuf_t*, size_t) -> ringbuf_status_t`: Allocates and initializes the ring buffer.
* `ringbuf_free(ringbuf_t*) -> void`: Deallocates buffer memory.
* `ringbuf_write(ringbuf_t*, const uint8_t*, size_t) -> size_t`: Copies bytes into the buffer.
* `ringbuf_read(ringbuf_t*, uint8_t*, size_t) -> size_t`: Copies bytes out of the buffer.
* `ringbuf_used(const ringbuf_t*) -> size_t`: Returns number of bytes currently stored.
* `ringbuf_free_space(const ringbuf_t*) -> size_t`: Returns available write capacity.

#### Dependencies
* `stddef.h`
* `stdint.h`

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if capacity is zero or not a power of two.
* `RINGBUF_ENOMEM`: Returned if memory allocation for the data buffer fails.
