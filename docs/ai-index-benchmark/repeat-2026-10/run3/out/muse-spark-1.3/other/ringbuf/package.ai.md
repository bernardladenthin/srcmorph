### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:25:18Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Single-producer single-consumer byte FIFO circular buffer for lock-free streaming data storage.
#### Purpose
- Fixed-capacity power-of-two byte ring buffer for streaming FIFO queueing
- Bounded `capacity-1` byte store with `head`/`tail` lifecycle, occupancy query, wrap-around read/write
#### Responsibilities
- Lifecycle management: `ringbuf_init` malloc power-of-two `data`, `ringbuf_free` release and reset
- Enqueue/dequeue I/O: `ringbuf_write` / `ringbuf_read` with clamped partial copies, two-chunk `memcpy` at array end
- Occupancy accounting: `ringbuf_used` occupied bytes, `ringbuf_free_space` writable bytes
#### Key units
- `ringbuf_t` / `struct ringbuf` — `data`, `capacity`, volatile `head`, `tail` byte FIFO state
- `ringbuf_status_t` / `enum ringbuf_status` — `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM` init result
- `ringbuf_init(rb, capacity)` — validates power-of-two, mallocs buffer, zeroes indices
- `ringbuf_free(rb)` — frees `data`, resets state
- `ringbuf_write(rb, src, len)` — enqueues up to `free_space` bytes, returns written
- `ringbuf_read(rb, dst, len)` — dequeues up to `used` bytes, returns read
- `ringbuf_used(rb)` — `(head-tail) & (capacity-1)` occupied count
- `ringbuf_free_space(rb)` — `capacity-1-used` writable count
#### Data flow
- `src` + `len` -> `ringbuf_write` clamped to `free_space` -> split `memcpy` -> `head` advanced via `& (capacity-1)`
- Buffered bytes -> `ringbuf_read` clamped to `used` -> split `memcpy` to `dst` -> `tail` advanced via `& (capacity-1)`
- `head`/`tail` delta drives `ringbuf_used` / `ringbuf_free_space` for next transfer size
#### Dependencies
- Internal: `ringbuf.c` implements `ringbuf.h` `ringbuf_t`, `ringbuf_status_t` API
- External: `stdlib.h` for `malloc`/`free`, `string.h` for `memcpy`, `stddef.h` for `size_t`, `stdint.h` for `uint8_t`
#### Cross-cutting
- Power-of-two invariant: `is_power_of_two` via `n & (n-1)`, wrap/modulo via `& (capacity-1)`
- One-slot reserve: full vs empty disambiguation, max `capacity-1` bytes
- SPSC split: producer-owned `head`, consumer-owned `tail`, volatile indices for lock-free buffering
- Error handling: `RINGBUF_EINVAL` on zero/non-power-of-two `capacity`, `RINGBUF_ENOMEM` on `malloc` failure, `min_size` clamp for partial copies
