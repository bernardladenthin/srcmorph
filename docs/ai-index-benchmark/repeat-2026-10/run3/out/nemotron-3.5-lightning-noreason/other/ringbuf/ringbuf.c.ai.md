### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T14:41:12Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates buffer capacity and manages read/write pointers for a fixed-size ring buffer.

#### Purpose
Implements a fixed-size, power-of-two capacity ring buffer with write and read operations.

#### Type
C source file (ringbuf.c); implements ringbuf_t defined in ringbuf.h.

#### Input
- `ringbuf_t *rb`: pointer to ring buffer structure
- `size_t capacity`: must be a power of two
- `const uint8_t *src`: source data to write
- `size_t len`: number of bytes to write/read
- `uint8_t *dst`: destination buffer for read operations

#### Output
- `ringbuf_status_t`: initialization status (OK, EINVAL, ENOMEM)
- `size_t`: number of bytes actually written or read
- Ring buffer state updates (head/tail pointers, data storage)

#### Core logic
- Initialization validates capacity is power of two, allocates memory, zeroes head/tail pointers
- `ringbuf_used`: calculates occupied slots using bitmask `(head - tail) & (capacity - 1)`
- `ringbuf_free_space`: returns `capacity - 1 - used` (one slot reserved as empty flag)
- `ringbuf_write`: copies data in up to two chunks (end-of-buffer then wrap-around), updates head pointer
- `ringbuf_read`: copies data in up to two chunks from tail, updates tail pointer
- Both read/write use `min_size` to respect requested length and available space

#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t`: initializes ring buffer with power-of-two capacity
- `ringbuf_free(rb)`: frees allocated memory and resets state
- `ringbuf_used(rb) -> size_t`: returns number of bytes currently stored
- `ringbuf_free_space(rb) -> size_t`: returns available space for new data
- `ringbuf_write(rb, src, len) -> size_t`: writes bytes into buffer, returns actual count written
- `ringbuf_read(rb, dst, len) -> size_t`: reads bytes from buffer, returns actual count read

#### Dependencies
- `ringbuf.h`: defines `ringbuf_t`, `ringbuf_status_t`, and status constants
- Standard library: `stdlib.h` (malloc, free), `string.h` (memcpy)

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned if capacity is not a power of two
- `RINGBUF_ENOMEM`: returned if malloc fails to allocate buffer memory
