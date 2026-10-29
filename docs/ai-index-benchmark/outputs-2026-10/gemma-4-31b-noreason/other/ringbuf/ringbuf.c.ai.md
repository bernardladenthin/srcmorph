### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T09:54:13Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a circular buffer (ring buffer) for byte-stream data storage and retrieval.

#### Purpose
* Provide a fixed-capacity FIFO queue for `uint8_t` data.
* Ensure efficient wrapping using power-of-two capacity masking.

#### Type
Implementation file (C).

#### Input
* `capacity`: Desired size of the buffer (must be power of two).
* `src`: Source byte array for writing.
* `dst`: Destination byte array for reading.
* `len`: Requested number of bytes to read or write.

#### Output
* `ringbuf_status_t`: Initialization success or error code.
* `size_t`: Actual number of bytes read or written.
* Side effects: Allocation/deallocation of `rb->data` heap memory.

#### Core logic
* **Capacity Constraint**: Validates that `capacity` is a power of two via `is_power_of_two` to allow bitwise masking (`& (rb->capacity - 1)`) instead of modulo operations.
* **Write Operation**: Calculates available space via `ringbuf_free_space`, performs up to two `memcpy` calls to handle wrap-around at the buffer boundary, and updates the `head` index.
* **Read Operation**: Calculates available data via `ringbuf_used`, performs up to two `memcpy` calls to handle wrap-around, and updates the `tail` index.
* **Space Management**: Maintains a "one slot empty" rule (capacity - 1) to distinguish between full and empty states.

#### Public API
* `ringbuf_init(ringbuf_t *, size_t) -> ringbuf_status_t`: Allocates and initializes the buffer.
* `ringbuf_free(ringbuf_t *) -> void`: Deallocates buffer memory.
* `ringbuf_used(const ringbuf_t *) -> size_t`: Returns number of bytes stored.
* `ringbuf_free_space(const ringbuf_t *) -> size_t`: Returns available write capacity.
* `ringbuf_write(ringbuf_t *, const uint8_t *, size_t) -> size_t`: Writes bytes to the buffer.
* `ringbuf_read(ringbuf_t *, uint8_t *, size_t) -> size_t`: Reads bytes from the buffer.

#### Dependencies
* `ringbuf.h`
* `stdlib.h` (malloc, free)
* `string.h` (memcpy)

#### Exceptions / Errors
* `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
* `RINGBUF_ENOMEM`: Returned if `malloc` fails to allocate `rb->data`.
