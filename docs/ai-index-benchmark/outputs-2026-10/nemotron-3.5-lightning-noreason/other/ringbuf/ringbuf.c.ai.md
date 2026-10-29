### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:54:03Z
- G: 
- A: 0.0.0
- X: file
---
> A lock-free single-producer single-consumer circular buffer implementation for high-throughput data streaming.

#### Purpose
Implements a fixed-capacity circular buffer optimized for power-of-two sizes, enabling efficient single-producer and single-consumer operations.

#### Type
C source file (ringbuf.c); implements ringbuf_t defined in ringbuf.h. Uses power-of-two masking for wrap-around.

#### Input
- `ringbuf_t *rb`: pointer to buffer state
- `size_t capacity`: must be a power of two; determines buffer size
- `const uint8_t *src`: source data to write
- `size_t len`: number of bytes to write (capped by free space)
- `uint8_t *dst`: destination buffer for read data
- `size_t len`: number of bytes to read (capped by used space)

#### Output
- `ringbuf_status_t`: init result (RINGBUF_OK, RINGBUF_EINVAL, RINGBUF_ENOMEM)
- `size_t`: number of bytes actually written or read
- Buffer state updates: head/tail pointers and data array populated

#### Core logic
- **Init**: validates capacity is power-of-two; allocates data array; sets head=tail=0
- **Write**: computes usable space; copies up to two chunks (tail wrap-around); advances head via power-of-two mask
- **Read**: computes available data; copies up to two chunks from tail; advances tail via power-of-two mask
- **Free space**: total capacity minus 1 minus used count (one slot always empty to distinguish full/empty)
- **Used size**: head minus tail masked to capacity

#### Public API
- `ringbuf_status_t ringbuf_init(ringbuf_t *rb, size_t capacity)` — initializes buffer with power-of-two capacity
- `void ringbuf_free(ringbuf_t *rb)` — releases allocated data memory
- `size_t ringbuf_used(const ringbuf_t *rb)` — returns number of bytes currently stored
- `size_t ringbuf_free_space(const ringbuf_t *rb)` — returns bytes available for writing
- `size_t ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` — writes bytes into buffer
- `size_t ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` — reads bytes from buffer

#### Dependencies
- `ringbuf.h` — defines ringbuf_t, ringbuf_status_t, and constants
- `<stdlib.h>` — for malloc/free
- `<string.h>` — for memcpy

#### Exceptions / Errors
- `RINGBUF_EINVAL`: returned if capacity is not a power of two
- `RINGBUF_ENOMEM`: returned if malloc fails to allocate data array
