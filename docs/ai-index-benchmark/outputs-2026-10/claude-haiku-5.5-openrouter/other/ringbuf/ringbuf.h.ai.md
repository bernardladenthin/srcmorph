### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:36:23Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-size byte ring buffer for lock-free single-producer, single-consumer data transfer.

#### Purpose
- Buffers bytes between one producer thread and one consumer thread without locks.
- Capacity fixed at init; must be a power of two.

#### Type
- C header (public interface, C/C++ compatible via `extern "C"` guard).
- Struct `ringbuf_t` with fields `data`, `capacity`, `head`, `tail`.
- Enum `ringbuf_status_t` (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).

#### Input
- `ringbuf_init`: `ringbuf_t*` target, `size_t capacity` (power of two, nonzero).
- `ringbuf_write`: `ringbuf_t*`, `const uint8_t* src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t*`, `uint8_t* dst`, `size_t len`.
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t*`.

#### Output
- `ringbuf_init` returns status code; allocates `data` buffer.
- `ringbuf_write` / `ringbuf_read` return count of bytes actually copied (may be less than `len`).
- `ringbuf_used` / `ringbuf_free_space` return byte counts.
- `ringbuf_free` releases `data` storage.

#### Core logic
- Index state: `head` (next write index, producer-owned), `tail` (next read index, consumer-owned), both `volatile size_t`.
- One slot always unused, so usable capacity is `capacity - 1`; distinguishes full from empty.
- Write copies min(`len`, free space) bytes, wrapping at capacity boundary.
- Read copies min(`len`, used bytes) bytes, wrapping at capacity boundary.
- Used bytes derived from `head - tail` masked by `capacity - 1`.
- Free space = `capacity - 1 - used`.
- Power-of-two capacity enables bitmask index wrapping.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` : allocate and initialize buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` : release buffer memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` : partial-copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` : partial-copy bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` : count of readable bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` : count of writable bytes.

#### Dependencies
- `stddef.h` (`size_t`)
- `stdint.h` (`uint8_t`)

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity zero or not power of two.
- `RINGBUF_ENOMEM`: allocation failure in `ringbuf_init`.
- Write/read return short counts instead of errors when buffer full or empty.
