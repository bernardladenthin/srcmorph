### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:05:10Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular buffer for efficient binary data streaming with power-of-two sizing.

#### Purpose
- Manages a ring buffer for enqueueing and dequeueing byte sequences with wrap-around logic.
- Provides O(1) memory allocation and data movement for streaming applications.

#### Type
C implementation file (`ringbuf.c`)
- Implements `ringbuf_t` structure and associated operations defined in `ringbuf.h`.
- Uses static helper functions for validation and size calculation.

#### Input
- `ringbuf_t *rb`: Pointer to the ring buffer instance.
- `size_t capacity`: Initial buffer size (must be power of two).
- `const uint8_t *src`: Source data buffer for write operations.
- `uint8_t *dst`: Destination buffer for read operations.
- `size_t len`: Requested number of bytes to transfer.

#### Output
- `ringbuf_status_t`: Status codes (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `size_t`: Number of bytes actually written or read.
- Internal state updates: `head`, `tail`, and `data` pointer modifications.

#### Core logic
- Validates `capacity` is a power of two using bitwise check `(n & (n - 1)) == 0`.
- Allocates memory for `data` buffer; returns error if allocation fails.
- Calculates used space via bitwise AND: `(head - tail) & (capacity - 1)`.
- Calculates free space as `capacity - 1 - used`.
- Writes data in at most two chunks: first to end of array, then wrapping to index 0.
- Reads data in at most two chunks: first from tail to end, then wrapping to index 0.
- Updates `head` or `tail` indices using bitwise AND for modulo arithmetic.

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: Initializes buffer with specified power-of-two capacity.
- `ringbuf_free(rb) -> void`: Releases allocated memory and resets state.
- `ringbuf_used(rb) -> size_t`: Returns current number of occupied bytes.
- `ringbuf_free_space(rb) -> size_t`: Returns available bytes for writing.
- `ringbuf_write(rb, src, len) -> size_t`: Copies up to `len` bytes into buffer.
- `ringbuf_read(rb, dst, len) -> size_t`: Copies up to `len` bytes out of buffer.

#### Dependencies
- `ringbuf.h`: Header defining `ringbuf_t`, `ringbuf_status_t`, and function prototypes.
- `stdlib.h`: `malloc`, `free`.
- `string.h`: `memcpy`.

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two.
- `RINGBUF_ENOMEM`: Returned if `malloc` fails during initialization.
- Silent truncation: `ringbuf_write` and `ringbuf_read` return actual bytes transferred if buffer is full or empty.
