### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:08:58Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, power-of-two byte ring buffer with wrap-around read/write operations for circular data storage.

#### Purpose
- Provides a C implementation of a ring buffer (`ringbuf_t`) for storing and retrieving byte sequences in a circular fashion.
- Enforces power-of-two capacity to enable efficient index wrapping via bitwise AND.

#### Type
- C implementation file (`ringbuf.c`)
- Defines static helper functions and public API functions operating on `ringbuf_t` (defined in `ringbuf.h`)

#### Input
- `capacity`: size of the buffer, must be a power of two
- `src`: source byte array for writes
- `dst`: destination byte array for reads
- `len`: number of bytes to read or write

#### Output
- `ringbuf_status_t`: status code (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
- `size_t`: number of bytes actually read or written
- Modifies internal state: `rb->head`, `rb->tail`, `rb->data`, `rb->capacity`

#### Core logic
- **Initialization**: Validates capacity is a power of two; allocates `capacity` bytes; initializes `head` and `tail` to 0
- **Index wrapping**: Uses `(index) & (capacity - 1)` to wrap indices, leveraging power-of-two property
- **Write operation**: Copies data in up to two chunks (from `head` to end of array, then from index 0); advances `head` by written bytes
- **Read operation**: Copies data in up to two chunks (from `tail` to end of array, then from index 0); advances `tail` by read bytes
- **Space calculation**: `used = (head - tail) & (capacity - 1)`; `free_space = capacity - 1 - used` (one slot reserved to distinguish full from empty)

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — Initialize buffer with given capacity
- `ringbuf_free(ringbuf_t *rb) -> void` — Free allocated memory and reset state
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — Return number of bytes currently stored
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — Return available bytes for writing
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — Write bytes into buffer, return count written
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — Read bytes from buffer, return count read

#### Dependencies
- `ringbuf.h` — Header defining `ringbuf_t`, `ringbuf_status_t`, and public function prototypes
- `stdlib.h` — `malloc`, `free`
- `string.h` — `memcpy`

#### Exceptions / Errors
- `RINGBUF_EINVAL`: Returned if `capacity` is not a power of two
- `RINGBUF_ENOMEM`: Returned if `malloc` fails during initialization
