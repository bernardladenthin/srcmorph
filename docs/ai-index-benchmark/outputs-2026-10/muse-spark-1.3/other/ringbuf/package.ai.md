### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T10:18:17Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Single-producer single-consumer FIFO byte queue buffering variable-length streams in power-of-two wraparound storage.
#### Purpose
- Fixed-capacity circular byte buffer for producer-consumer byte transfer.
- Power-of-two `ringbuf_t` FIFO with wraparound enqueue/dequeue and occupancy tracking.
#### Responsibilities
- Lifecycle management: `ringbuf_init` validation/allocation, `ringbuf_free` release/reset.
- Data transfer: `ringbuf_write` enqueue, `ringbuf_read` dequeue with two-chunk `memcpy` wraparound.
- Occupancy accounting: `ringbuf_used`, `ringbuf_free_space` via masked `head-tail` distance.
#### Key units
- `ringbuf_t` — state with `data`, `capacity`, volatile `head` write index, `tail` read index.
- `ringbuf_status_t` — init result: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(rb, capacity)` — validates power-of-two, `malloc`s buffer, zeroes indices.
- `ringbuf_free(rb)` — frees buffer and resets.
- `ringbuf_write(rb, src, len)` — enqueues up to free space, advances `head`.
- `ringbuf_read(rb, dst, len)` — dequeues up to used bytes, advances `tail`.
- `ringbuf_used(rb)` — occupied bytes.
- `ringbuf_free_space(rb)` — writable bytes.
#### Data flow
- `src/len` -> `ringbuf_write` clamped by `free_space` -> split `memcpy` at array end -> `head` advanced via `& (capacity-1)`.
- Buffered bytes -> `ringbuf_read` clamped by `used` -> split `memcpy` -> `dst/len`, `tail` advanced.
- `used = (head-tail) & (capacity-1)`, `free_space = capacity-1-used`.
#### Dependencies
- Internal: `ringbuf.c` implements `ringbuf.h` `ringbuf_t`, `ringbuf_status_t` declarations.
- External: `stdlib.h` `malloc`/`free`, `string.h` `memcpy`, `stddef.h` `size_t`, `stdint.h` `uint8_t`.
#### Cross-cutting
- Power-of-two requirement via `is_power_of_two` bitmask check, `& (capacity-1)` modulo indexing.
- Capacity-1 usable bytes, one slot always empty, `min_size` clamping to `free_space`/`used`.
- SPSC ownership: producer owns `head`, consumer owns `tail`.
- Error handling: `RINGBUF_EINVAL` on zero/non-power-of-two capacity, `RINGBUF_ENOMEM` on allocation failure.
