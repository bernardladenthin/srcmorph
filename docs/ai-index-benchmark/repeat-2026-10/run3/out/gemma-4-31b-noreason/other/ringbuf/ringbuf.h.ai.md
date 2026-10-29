### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:30:44Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, single-producer single-consumer (SPSC) byte ring buffer.

#### Purpose
* Provide a lock-free circular buffer for byte-stream data exchange.
* Manage memory and indices for producer/consumer synchronization.

#### Type
C Header file; defines `ringbuf_t` (struct) and `ringbuf_status_t` (enum).

#### Input
* `capacity`: Must be a power of two.
* `src`: Source byte array for writes.
* `dst`: Destination byte array for reads.
* `len`: Requested number of bytes to transfer.

#### Output
* `ringbuf_status_t`: Initialization success or error codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
* `size_t`: Actual number of bytes read or written.
* `size_t`: Current used or free space.

#### Core logic
* Uses a power-of-two capacity to facilitate wrap-around indexing.
* Maintains a `head` index (producer-owned) and a `tail` index (consumer-owned).
* Reserves one slot as unused to distinguish between full and empty states.
* Supports partial reads/writes if the requested `len` exceeds available space.

#### Public API
* `ringbuf_init(ringbuf_t*, size_t) -> ringbuf_status_t`: Allocates and initializes buffer.
* `ringbuf_free(ringbuf_t*) -> void`: Deallocates buffer memory.
* `ringbuf_write(ringbuf_t*, const uint8_t*, size_t) -> size_t`: Copies bytes into buffer.
* `ringbuf_read(ringbuf_t*, uint8_t*, size_t) -> size_t`: Copies bytes out of buffer.
* `ringbuf_used(const ringbuf_t*) -> size_t`: Returns bytes currently stored.
* `ringbuf_free_space(const ringbuf_t*) -> size_t`: Returns available write capacity.

#### Dependencies
* `stddef.h`
* `stdint.h`
