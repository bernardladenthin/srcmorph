### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T13:58:18Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a circular byte buffer (ring buffer) with power-of-two capacity for efficient indexing.

#### Purpose
* Provide a fixed-capacity FIFO queue for byte streams.
* Manage memory allocation and wrap-around indexing for read/write operations.

#### Type
Implementation file (C).

#### Input
* `capacity`: Size of the buffer (must be power of two).
* `src`: Pointer to source data for writing.
* `dst`: Pointer to destination buffer for reading.
* `len`: Requested number of bytes to transfer.

#### Output
* `ringbuf_status_t`: Initialization success or error code.
* `size_t`: Number of bytes actually read or written.
* Side effects: Modifies `ringbuf_t` head/tail indices and internal `data` heap memory.

#### Core logic
* **Capacity Constraint**: Enforces power-of-two size to allow bitwise masking `(index & (capacity - 1))` instead of modulo operators for wrap-around.
* **Space Calculation**: 
    * Used space: `(head - tail) & (capacity - 1)`.
    * Free space: `capacity - 1 - used`.
* **Two-Stage Copy**: Both `ringbuf_write` and `ringbuf_read` handle wrap-around by splitting the transfer into two `memcpy` calls: one from the current index to the end of the array, and a second from the start of the array.
* **Index Management**: Updates `head` on write and `tail` on read using bitwise masking.

#### Public API
* `ringbuf_init(ringbuf_t *, size_t) -> ringbuf_status_t`: Allocates and initializes buffer.
* `ringbuf_free(ringbuf_t *) -> void`: Deallocates buffer memory.
* `ringbuf_used(const ringbuf_t *) -> size_t`: Returns bytes currently stored.
* `ringbuf_free_space(const ringbuf_t *) -> size_t`: Returns available write capacity.
* `ringbuf_write(ringbuf_t *, const uint8_t *, size_t) -> size_t`: Writes bytes to buffer.
* `ringbuf_read(ringbuf_t *, uint8_t *, size_t) -> size_t`: Reads bytes from buffer.

#### Dependencies
* `ringbuf.h`
* `stdlib.h` (malloc, free)
* `string.h` (memcpy)

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
* `RINGBUF_ENOMEM`: Returned if `malloc` fails to allocate `rb->data`.
