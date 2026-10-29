### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-28T23:08:50Z
- G: 
- A: 0.0.0
- X: file
---
> Fixed-capacity ring buffer for single producer and consumer data transfer

#### Purpose
- Fixed-capacity ring buffer for single producer and single consumer

#### Type
- C header defining `ringbuf_t` struct with `data`, `capacity`, `head`, `tail` fields
- Exported types: `ringbuf_status_t` (with `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
- `extern "C"` linkage for C++ compatibility

#### Input
- `ringbuf_t *rb`: ring buffer instance
- `size_t capacity`: must be power of two; one slot permanently reserved
- `const uint8_t *src`: source data for writing
- `uint8_t *dst`: destination buffer for reading
- `size_t len`: number of bytes to transfer

#### Output
- `size_t`: actual bytes copied or transferred
- `ringbuf_status_t`: completion status (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`)
- Updates `head` index (producer) and `tail` index (consumer) via wrap-around

#### Core logic
- Buffer usable space = capacity - 1 (one slot never used to distinguish full/empty)
- Write: advance `head` with bitmask wrap-around, copy data from `src` into `data[head]`
- Read: advance `tail` with bitmask wrap-around, copy data from `data[tail]` to `dst`
- `ringbuf_used` returns `(head - tail) & (capacity - 1)`
- `ringbuf_free_space` returns `capacity - ringbuf_used(rb) - 1`
- Write returns zero if no free space; read returns zero if no data

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: init and allocate buffer
- `ringbuf_free(ringbuf_t *rb)`: release buffer resources
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: copy bytes into buffer
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: copy bytes from buffer
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: return stored byte count
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: return free write capacity

#### Dependencies
- `<stddef.h>`: `size_t`
- `<stdint.h>`: `uint8_t`
- C `extern "C"` block for C++ linkage

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity zero or not power of two
- `RINGBUF_ENOMEM`: allocation failure
- Write returns 0 when buffer full (no free slots); read returns 0 when empty
