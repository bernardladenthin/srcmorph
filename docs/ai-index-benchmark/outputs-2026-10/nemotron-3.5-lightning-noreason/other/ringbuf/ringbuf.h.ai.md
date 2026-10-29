### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:10Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity byte ring buffer for a single producer and a single consumer.

#### Purpose
A fixed-capacity, single-producer/single-consumer byte ring buffer implementation.

#### Type
C header file (`ringbuf.h`). Defines the `ringbuf_t` struct and functions for initialization, freeing, writing, reading, and querying space usage. Uses `volatile` for head/tail to support concurrent access.

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer instance.
- `size_t capacity`: The total capacity (must be a power of two; one slot is reserved).
- `const uint8_t *src`: Source data pointer for writing.
- `uint8_t *dst`: Destination data pointer for reading.
- `size_t len`: Number of bytes to transfer.

#### Output
- `ringbuf_status_t`: Return status (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`) from init/free.
- `size_t`: From `ringbuf_write` and `ringbuf_read`, the actual number of bytes copied. From `ringbuf_used` and `ringbuf_free_space`, the current number of used bytes or free space.

#### Core logic
- Buffer capacity is fixed at initialization; one slot is always reserved so the buffer never appears full when head catches tail.
- `ringbuf_write`: Calculates available space, copies data from `src` wrapping around the end of the underlying `data` array using modulo arithmetic (index & (capacity - 1)), updates the volatile `head` index, and returns the actual byte count.
- `ringbuf_read`: Calculates available data (difference between head and tail), copies data to `dst` wrapping around the buffer end, updates the volatile `tail` index, and returns the actual byte count.
- `ringbuf_used`: Returns `(head - tail) & (capacity - 1)` to determine current occupancy.
- `ringbuf_free_space`: Returns `capacity - 1 - ringbuf_used()`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Initialize the ring buffer with given capacity; returns error if capacity is zero or not a power of two.
- `ringbuf_free(ringbuf_t *rb) -> void`: Free associated resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Write bytes from src; returns actual bytes copied.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Read bytes into dst; returns actual bytes copied.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: Return number of bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Return number of bytes available for writing.

#### Dependencies
- `<stddef.h>` for `size_t`
- `<stdint.h>` for `uint8_t`, `size_t`
- C++ `extern "C"` linkage when compiled as C++.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if `capacity` is zero or not a power of two during `ringbuf_init`.
- `RINGBUF_ENOMEM`: Returned if allocation fails (though the provided source does not show explicit malloc/free within init, the status indicates its intent).
