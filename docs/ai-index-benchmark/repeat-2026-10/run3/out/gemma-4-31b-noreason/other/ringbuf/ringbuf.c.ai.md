### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:30:29Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a circular buffer (ring buffer) for byte-stream data storage and retrieval.

#### Purpose
* Provides a fixed-capacity FIFO queue for `uint8_t` data.
* Ensures efficient wrapping using power-of-two capacity masking.

#### Type
Implementation file (.c), C language.

#### Input
* `capacity`: Requested size in bytes (must be power of two).
* `src`: Source buffer for writing.
* `dst`: Destination buffer for reading.
* `len`: Number of bytes to read or write.

#### Output
* `ringbuf_status_t`: Initialization success or error code.
* `size_t`: Number of bytes actually read or written.
* Modified `ringbuf_t` state (head/tail indices and allocated data).

#### Core logic
* **Capacity Constraint**: Validates that `capacity` is a power of two via `is_power_of_two` to allow bitwise masking (`& (rb->capacity - 1)`) instead of modulo operators.
* **Space Calculation**: 
    * `ringbuf_used`: Calculates distance between `head` and `tail`.
    * `ringbuf_free_space`: Calculates available slots, reserving one byte to distinguish between full and empty states.
* **Wrapped Access**: Both `ringbuf_write` and `ringbuf_read` handle wrap-around by splitting the operation into two `memcpy` calls: one from the current index to the end of the buffer, and a second from the start of the buffer.
* **Index Management**: Updates `head` (on write) and `tail` (on read) using bitwise AND with `capacity - 1`.

#### Public API
* `ringbuf_init(ringbuf_t *, size_t) -> ringbuf_status_t`: Allocates memory and initializes buffer.
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
