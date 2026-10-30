### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:32:25Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-size byte ring buffer for lock-free single-producer, single-consumer data transfer.

#### Purpose
- Declares a byte FIFO ring buffer for one producer thread and one consumer thread.
- Exposes init, free, bulk write/read, and occupancy queries via C linkage.

#### Type
- C header (`ringbuf.h`), exported API, `extern "C"` guarded for C++.
- Struct `ringbuf_t` (`uint8_t *data`, `size_t capacity`, `volatile size_t head`, `volatile size_t tail`).
- Enum `ringbuf_status_t` (`RINGBUF_OK = 0`, `RINGBUF_EINVAL = -1`, `RINGBUF_ENOMEM = -2`).

#### Input
- `ringbuf_init`: `ringbuf_t *rb`, `size_t capacity` (must be power of two, nonzero).
- `ringbuf_write`: `ringbuf_t *rb`, `const uint8_t *src`, `size_t len`.
- `ringbuf_read`: `ringbuf_t *rb`, `uint8_t *dst`, `size_t len`.
- `ringbuf_used`, `ringbuf_free_space`: `const ringbuf_t *rb`.

#### Output
- `ringbuf_init` returns `ringbuf_status_t`.
- `ringbuf_write` and `ringbuf_read` return `size_t` bytes actually copied (may be less than `len`).
- `ringbuf_used` and `ringbuf_free_space` return `size_t` byte counts.
- `ringbuf_free` returns void; releases `data`.

#### Core logic
- Capacity must be a power of two; one slot stays unused, so usable size is `capacity - 1`.
- `head` is the next write index, written only by the producer.
- `tail` is the next read index, written only by the consumer.
- Write copies up to `len` bytes from `src` into `data` at `head`, bounded by free space.
- Read copies up to `len` bytes from `data` at `tail` into `dst`, bounded by used bytes.
- Used bytes derive from `head - tail` with wraparound via `capacity - 1` mask.
- Free space derives as `capacity - 1 - used`.
- `volatile` on `head`/`tail` signals cross-thread visibility; no explicit memory barriers declared here.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t`: allocates buffer, sets indices.
- `ringbuf_free(ringbuf_t *rb) -> void`: releases internal data buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t`: producer copies bytes in.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t`: consumer copies bytes out.
- `ringbuf_used(const ringbuf_t *rb) -> size_t`: count of readable bytes.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t`: count of writable bytes.

#### Dependencies
- `<stddef.h>` (`size_t`)
- `<stdint.h>` (`uint8_t`)

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity zero or not power of two.
- `RINGBUF_ENOMEM`: allocation failure in `ringbuf_init`.
- Short writes/reads: return value below `len` signals partial transfer, not an error.
