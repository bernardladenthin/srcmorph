### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-29T23:47:31Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a fixed-capacity byte queue for single-producer/single-consumer data transfer.

#### Purpose
- Defines a lock-free-style ring buffer contract for byte streams with single producer and single consumer ownership.
- Exposes capacity validation, allocation lifecycle, read/write operations, and occupancy queries.

#### Type
- Kind: header / C interface.
- Types:
  - `ringbuf_t` struct with `data`, `capacity`, `head`, `tail`.
  - `ringbuf_status_t` enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- Language: C.
- Notes: `capacity` must be a power of two; one slot is unused so effective usable bytes are `capacity - 1`.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` consumes a caller-owned `ringbuf_t` and a desired `capacity`.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` consumes a source byte buffer and a length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` consumes a destination buffer and a length.
- `ringbuf_used(const ringbuf_t *rb)` and `ringbuf_free_space(const ringbuf_t *rb)` read the current buffer state.
- External state: `uint8_t *data`, `size_t capacity`, `volatile size_t head`, `volatile size_t tail`.

#### Output
- `ringbuf_init` returns `ringbuf_status_t` indicating initialization success or error.
- `ringbuf_free` releases buffer-owned memory from `ringbuf_t`.
- `ringbuf_write` returns the number of bytes actually copied.
- `ringbuf_read` returns the number of bytes actually copied.
- `ringbuf_used` returns used byte count.
- `ringbuf_free_space` returns free byte count.
- Side effects: modifies `data`, `head`, `tail`, and memory allocation state.

#### Core logic
- `ringbuf_t` tracks producer-owned `head` and consumer-owned `tail` indexes in a fixed-capacity circular buffer.
- `ringbuf_init` validates `capacity`, requiring power-of-two and non-zero, then initializes storage.
- `ringbuf_write` copies up to `len` bytes from `src` into the ring and returns the actual count copied.
- `ringbuf_read` copies up to `len` bytes out to `dst` and returns the actual count copied.
- The contract notes one slot is kept unused so a buffer holds `capacity - 1` bytes for actual data.
- `ringbuf_used` and `ringbuf_free_space` expose occupancy state for producer/consumer coordination.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` initializes buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` releases buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` writes bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` reads bytes.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` reports used bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` reports free bytes.
- `ringbuf_t` type defines buffer state.
- `ringbuf_status_t` enum defines result codes.

#### Dependencies
- Standard C headers:
  - `stddef.h`
  - `stdint.h`
- C linkage guard:
  - `extern "C"` when compiled as C++.

#### Exceptions / Errors
- `ringbuf_status_t` includes:
  - `RINGBUF_OK = 0` success.
  - `RINGBUF_EINVAL = -1` invalid `capacity` (zero or not power of two).
  - `RINGBUF_ENOMEM = -2` allocation failed.
- The header comments explicitly state `ringbuf_write` and `ringbuf_read` return how many bytes were actually copied, rather than implying full-length transfers.
