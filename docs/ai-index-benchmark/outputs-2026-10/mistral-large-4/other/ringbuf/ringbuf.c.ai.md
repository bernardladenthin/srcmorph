### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T16:45:17Z
- G: 
- A: 0.0.0
- X: file
---
> Implements a fixed-capacity circular byte buffer for streaming data using power-of-two sizing and bitmask wraparound with head/tail indices.

#### Purpose
- Provides a fixed-size ring buffer (circular queue) for `uint8_t` byte streams
- Requires power-of-two capacity to enable bitmask `& (capacity - 1)` wraparound instead of modulo

#### Type
- C implementation file (`.c`) for the `ringbuf` module
- Operates on `ringbuf_t` struct (defined in `ringbuf.h`) with fields `data`, `capacity`, `head`, `tail`
- Returns status codes via `ringbuf_status_t` enum

#### Input
- `ringbuf_init`: `ringbuf_t *rb` handle, `size_t capacity` (must be non-zero power of two)
- `ringbuf_write`: `ringbuf_t *rb`, source buffer `const uint8_t *src`, `size_t len`
- `ringbuf_read`: `ringbuf_t *rb`, destination buffer `uint8_t *dst`, `size_t len`
- `ringbuf_used` / `ringbuf_free_space`: `const ringbuf_t *rb`

#### Output
- `ringbuf_init` returns `ringbuf_status_t`: `RINGBUF_OK`, `RINGBUF_EINVAL`, or `RINGBUF_ENOMEM`
- `ringbuf_write` / `ringbuf_read` return `size_t`: actual bytes copied (clamped to available space/used data)
- `ringbuf_used` returns `size_t`: bytes currently stored
- `ringbuf_free_space` returns `size_t`: writable bytes remaining (`capacity - 1 - used`)
- `ringbuf_free` deallocates heap memory and zeroes struct fields

#### Core logic
- Validates capacity via `is_power_of_two` using bit trick `(n & (n - 1)) == 0`
- Allocates `capacity` bytes with `malloc`; initializes `head = 0`, `tail = 0`
- Reserves one slot to distinguish full from empty; usable capacity is `capacity - 1`
- `ringbuf_used` computes `(head - tail) & (capacity - 1)` using bitmask wraparound
- `ringbuf_write` clamps `len` to free space via `min_size`, splits copy into two `memcpy` chunks (end-of-buffer then index 0), advances `head` with `(head + n) & (capacity - 1)`
- `ringbuf_read` clamps `len` to used bytes, splits copy into two `memcpy` chunks, advances `tail` with `(tail + n) & (capacity - 1)`
- `ringbuf_free` calls `free` on `data` and resets all fields to zero/NULL

#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` — allocate and initialize buffer
- `ringbuf_free(ringbuf_t *rb) -> void` — release buffer memory
- `ringbuf_used(const ringbuf_t *rb) -> size_t` — get bytes currently stored
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` — get available write space
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` — enqueue bytes up to free space
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` — dequeue bytes up to used count

#### Dependencies
- `ringbuf.h` — `ringbuf_t` struct and `ringbuf_status_t` enum definitions
- `<stdlib.h>` — `malloc`, `free`
- `<string.h>` — `memcpy`

#### Exceptions / Errors
- `RINGBUF_EINVAL` when capacity is zero or not a power of two
- `RINGBUF_ENOMEM` when `malloc` fails
- `ringbuf_write` / `ringbuf_read` silently truncate to available space or used bytes rather than returning an error
