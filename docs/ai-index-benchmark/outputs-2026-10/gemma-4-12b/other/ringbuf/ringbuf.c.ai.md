### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T04:17:18Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a thread-unsafe circular buffer (ring buffer) for byte stream manipulation with power-of-two capacity constraints.

#### Purpose
* Implements a fixed-capacity ring buffer for efficient FIFO byte storage.
* Manages memory allocation and pointer arithmetic for wrap-around data access.

#### Type
C implementation file (source) for `ringbuf_t` structure.

#### Input
* `capacity`: Size of the buffer (must be a power of two).
* `src`: Pointer to source byte array for writing.
* `dst`: Pointer to destination byte array for reading.
* `len`: Number of bytes to read or write.

#### Output
* `ringbuf_status_t`: Status codes (OK, EINVAL, ENOMEM).
* `size_t`: Number of bytes successfully processed or current buffer occupancy.
* Side effects: Modifies `ringbuf_t` internal `head` and `tail` pointers and `data` buffer.

#### Core logic
* **Power of Two Constraint**: Validates that capacity is a power of two to allow bitwise masking for wrap-around logic.
* **Memory Management**: Dynamically allocates `rb->data` during initialization and frees it on destruction.
* **Occupancy Calculation**: Uses bitwise AND `(head - tail) & (capacity - 1)` to calculate used space.
* **Two-Chunk Copy**: Handles wrap-around by splitting a single logical copy into two `memcpy` operations if the data crosses the buffer boundary.
* **Pointer Arithmetic**: Updates `head` and `tail` using bitwise masking to ensure they stay within bounds.

#### Public API
* `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: Allocates memory and initializes buffer state.
* `ringbuf_free(ringbuf_t *rb) -> void`: Releases allocated memory and resets structure.
* `ringbuf_used(const ringbuf_t *rb) -> size_t`: Returns count of bytes currently in buffer.
* `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: Returns available bytes for writing.
* `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: Writes bytes to buffer, handles wrap-around.
* `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: Reads bytes from buffer, handles wrap-around.

#### Dependencies
* `ringbuf.h`
* `stdlib.h`
* `string.h`
