### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T09:33:03Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity, power-of-two ring buffer for byte streams with zero-copy-free copy in/out.

#### Purpose
- Provides a fixed-size circular byte buffer for streaming data (e.g., UART, DMA, audio).
- Exposes bounded non-blocking read/write with capacity that must be a power of two.

#### Type
- Header implementation; functions defined in `ringbuf.c`, declared in `ringbuf.h`.
- Language: C.

#### Input
- `ringbuf_init(ringbuf_t *rb, size_t capacity)`: buffer pointer and desired capacity (must be power of two).
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)`: source byte array and requested length.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)`: destination byte array and requested length.
- `ringbuf_free(ringbuf_t *rb)`, `ringbuf_used(const ringbuf_t *rb)`, `ringbuf_free_space(const ringbuf_t *rb)`: operate on `ringbuf_t`.

#### Output
- `ringbuf_init` returns `ringbuf_status_t`: `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`.
- `ringbuf_write` returns number of bytes actually written (may be less than `len`).
- `ringbuf_read` returns number of bytes actually read (may be less than `len`).
- `ringbuf_free` sets `data` to NULL and zeroes `capacity`, `head`, `tail`.
- `ringbuf_used` returns number of bytes currently stored.
- `ringbuf_free_space` returns number of bytes available for writing.

#### Core logic
- Capacity check: `is_power_of_two` rejects `0` and non-power-of-two capacities.
- Buffer layout: `head` points to next write position; `tail` points to next read position; one slot is left unused to distinguish full from empty.
- `ringbuf_used` computes `(head - tail) & (capacity - 1)`, relying on power-of-two wrapping.
- `ringbuf_write` caps `len` to free space, copies up to `capacity - head` bytes starting at `head`, then wraps and copies the remainder from index 0, then advances `head` with mask `(head + n) & (capacity - 1)`.
- `ringbuf_read` caps `len` to used bytes, copies from `tail` up to end of array, then wraps from index 0, then advances `tail` with mask `(tail + n) & (capacity - 1)`.
- `ringbuf_free_space` returns `capacity - 1 - ringbuf_used(rb)`.

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize ring buffer.
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer and reset state.
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — return bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — return bytes available for writing.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — copy bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — copy bytes out of buffer.

#### Dependencies
- `ringbuf.h` (defines `ringbuf_t`, `ringbuf_status_t`, `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- Standard library: `stdlib.h` for `malloc`, `free`; `string.h` for `memcpy`.

#### Exceptions / Errors
- `ringbuf_init` returns `RINGBUF_EINVAL` when `capacity` is not a power of two.
- `ringbuf_init` returns `RINGBUF_ENOMEM` when `malloc` fails.
- No errors are reported by read/write; they silently return a length shorter than requested when the buffer is full/empty.
