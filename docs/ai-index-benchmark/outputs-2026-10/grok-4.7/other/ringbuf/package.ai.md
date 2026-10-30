### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-30T00:46:21Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
I will produce a dense, structured markdown summary of the ringbuf package based on the per-file summaries.

The lead blockquote will describe the ring buffer as a fixed-capacity single-producer, single-consumer byte ring with power-of-two capacity.> Fixed-capacity single-producer single-consumer byte ring that enqueues and dequeues `uint8_t` data with wraparound copies and power-of-two capacity.

#### Purpose
- Provides a C SPSC byte circular buffer (`ringbuf_t`) that stores at most `capacity - 1` bytes.
- Tracks occupancy with masked `head`/`tail` indexes so one unused slot distinguishes full from empty.

#### Responsibilities
- Header API (`ringbuf.h`): types, status codes, and declarations for init, free, copy-in, copy-out, and occupancy queries.
- Implementation (`ringbuf.c`): power-of-two checks, allocation, masked index math, and at-most-two-chunk wraparound `memcpy` for write and read.

#### Key units
- `ringbuf_t` — struct with `uint8_t *data`, `size_t capacity`, volatile producer `head`, and volatile consumer `tail`.
- `ringbuf_status_t` — init result enum: `RINGBUF_OK` (0), `RINGBUF_EINVAL` (-1), `RINGBUF_ENOMEM` (-2).
- `ringbuf_init` — allocates a power-of-two buffer and zeros `capacity`, `head`, and `tail`.
- `ringbuf_free` — releases `rb->data` and clears `data`, `capacity`, `head`, and `tail`.
- `ringbuf_write` / `ringbuf_read` — enqueue or dequeue up to available bytes, returning the count copied.
- `ringbuf_used` / `ringbuf_free_space` — occupancy and remaining writable bytes from masked `head` and `tail`.
- `is_power_of_two`, `min_size` — static helpers; power-of-two check validates the `capacity - 1` mask.

#### Data flow
- Caller passes `ringbuf_t *` and a non-zero power-of-two `capacity` to `ringbuf_init`; success allocates `rb->data` or returns `RINGBUF_EINVAL` / `RINGBUF_ENOMEM`.
- Producer `ringbuf_write` limits `len` to free space, copies a first chunk from `head` to the end of `data` and a second from index 0, then sets `head = (head + n) & (capacity - 1)`.
- Consumer `ringbuf_read` limits `len` to used bytes, performs the same two-chunk wrap from `tail` into `dst`, and advances `tail` with the same mask.
- Occupancy is `(head - tail) & (capacity - 1)`; free space is `capacity - 1 - ringbuf_used`. Short transfers return `n` less than requested `len`.

#### Dependencies
- `ringbuf.c` implements the `ringbuf.h` API.
- `stddef.h`, `stdint.h` for `size_t` and `uint8_t`.
- `stdlib.h` (`malloc`, `free`) and `string.h` (`memcpy`).

#### Cross-cutting
- `extern "C"` linkage when the header is compiled as C++.
- Producer owns volatile `head`; consumer owns volatile `tail`.
- No C++ exceptions; init failures are integer status codes. Short writes and reads are not errors.
