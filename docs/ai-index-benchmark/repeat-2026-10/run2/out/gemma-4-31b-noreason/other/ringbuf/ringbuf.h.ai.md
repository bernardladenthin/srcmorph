### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:26Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity byte ring buffer for single-producer single-consumer (SPSC) data exchange.

#### Purpose
* Provide a lock-free circular buffer for byte streams.
* Manage memory and indices for asynchronous read/write operations.

#### Type
C Header file; defines `ringbuf_t` (struct) and `ringbuf_status_t` (enum).

#### Input
* `capacity`: Must be a power of two.
* `src`: Byte array for writing.
* `dst`: Byte array for reading.
* `len`: Requested number of bytes to transfer.

#### Output
* `ringbuf_status_t`: Initialization success or error code.
* `size_t`: Actual number of bytes read or written.
* `size_t`: Current used or free space in bytes.

#### Core logic
* Uses a `head` index for the producer (write) and a `tail` index for the consumer (read).
* Maintains a capacity constraint where one slot remains unused to distinguish between full and empty states.
* Requires capacity to be a power of two to facilitate efficient wrapping (implied by comments).
* Employs `volatile` qualifiers on indices to prevent compiler optimization in SPSC concurrency.

#### Public API
* `ringbuf_init(ringbuf_t*, size_t) -> ringbuf_status_t`: Allocates and initializes the buffer.
* `ringbuf_free(ringbuf_t*) -> void`: Deallocates buffer memory.
* `ringbuf_write(ringbuf_t*, const uint8_t*, size_t) -> size_t`: Copies bytes into the buffer.
* `ringbuf_read(ringbuf_t*, uint8_t*, size_t) -> size_t`: Copies bytes out of the buffer.
* `ringbuf_used(const ringbuf_t*) -> size_t`: Returns bytes currently stored.
* `ringbuf_free_space(const ringbuf_t*) -> size_t`: Returns available write capacity.

#### Dependencies
* `stddef.h`
* `stdint.h`

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Triggered if capacity is zero or not a power of two.
* `RINGBUF_ENOMEM`: Triggered if memory allocation for `data` fails.
