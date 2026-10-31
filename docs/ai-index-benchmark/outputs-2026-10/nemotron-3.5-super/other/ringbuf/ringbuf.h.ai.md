### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:52:15Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a fixed-capacity byte ring buffer for single-producer/single-consumer data transfer.

#### Purpose
- Declares a fixed-capacity SPSC byte ring buffer with producer-owned head and consumer-owned tail indices.
- Provides initialization, teardown, write, read, and occupancy query operations.

#### Type
- C header file.
- Struct `ringbuf_t`; enum `ringbuf_status_t`.
- No base types or interfaces; plain C aggregation.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be non-zero and a power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init`: `ringbuf_status_t` indicating success, invalid capacity, or allocation failure.
- `ringbuf_write` / `ringbuf_read`: `size_t` number of bytes actually copied.
- `ringbuf_used` / `ringbuf_free_space`: `size_t` count of available bytes.
- Side effects: allocates and frees the internal `data` buffer; mutates `head` and `tail`.

#### Core logic
- Capacity must be a power of two; one slot is never used, so usable capacity is `capacity - 1` bytes.
- Producer exclusively updates `head`; consumer exclusively updates `tail`; both are `volatile` for cross-thread visibility.
- `ringbuf_write` copies up to `len` bytes from `src` into `data`, bounded by free space, and advances `head`.
- `ringbuf_read` copies up to `len` bytes from `data` into `dst`, bounded by used bytes, and advances `tail`.
- `ringbuf_used` derives occupancy from `head - tail`; `ringbuf_free_space` derives writable space from `(capacity - 1) - used`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` initializes buffer and allocates storage.
- `ringbuf_free(ringbuf_t *rb) -> void` releases allocated buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` enqueues bytes and returns count written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` dequeues bytes and returns count read.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` returns occupied byte count.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` returns writable byte count.

#### Dependencies
- `<stddef.h>` for `size_t`.
- `<stdint.h>` for `uint8_t`.
- `extern "C"` linkage guard for C++ compatibility.

#### Exceptions / Errors
- `RINGBUF_EINVAL` returned when capacity is zero or not a power of two.
- `RINGBUF_ENOMEM` returned when memory allocation fails.
