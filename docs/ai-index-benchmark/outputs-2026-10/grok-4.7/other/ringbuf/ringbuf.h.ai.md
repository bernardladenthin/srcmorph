### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:45:02Z
- G: 
- A: 0.0.0
- X: file
---
I need to index the provided ringbuf.h source file following specific formatting rules.> Fixed-capacity single-producer single-consumer byte ring buffer with power-of-two capacity.

#### Purpose
- Declares a C SPSC byte ring buffer (`ringbuf_t`) that stores at most `capacity - 1` bytes.
- Exposes init, free, copy-in, copy-out, and occupancy queries.

#### Type
- C header; exported types and functions under `extern "C"` when compiled as C++.
- `ringbuf_t`: struct with `uint8_t *data`, `size_t capacity`, volatile `head` (producer write index), volatile `tail` (consumer read index).
- `ringbuf_status_t`: enum of init results.

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be non-zero and a power of two).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- Occupancy helpers take `const ringbuf_t *rb`.

#### Output
- `ringbuf_status_t` from init: `RINGBUF_OK` (0), `RINGBUF_EINVAL` (-1), `RINGBUF_ENOMEM` (-2).
- `ringbuf_write` / `ringbuf_read` return the number of bytes actually copied (up to `len`).
- `ringbuf_used` / `ringbuf_free_space` return `size_t` occupancy or free bytes.
- `ringbuf_free` releases the buffer; no return value.

#### Core logic
- One unused slot distinguishes full from empty, so usable size is `capacity - 1`.
- Producer owns `head` (next write index); consumer owns `tail` (next read index); both are `volatile`.
- Write copies from `src` into the ring until full or `len` bytes; read copies out to `dst` until empty or `len` bytes.
- Implementation of wrap, allocation, and index math is not in this header.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release ring storage.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into ring.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of ring.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — bytes still writable.

#### Dependencies
- `stddef.h`, `stdint.h` (`size_t`, `uint8_t`).

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two.
- `RINGBUF_ENOMEM`: allocation failed.
- No C++ exceptions; status is an integer enum.
