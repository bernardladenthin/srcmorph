### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:19:08Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Single-producer single-consumer circular byte FIFO for streaming producer-consumer byte transfer with wraparound reads/writes.
#### Purpose
- Fixed-capacity power-of-two `ringbuf_t` circular byte queue for SPSC producer-consumer transfer
- Allocation, occupancy tracking, wraparound copy with partial-copy semantics
#### Responsibilities
- API declaration (`ringbuf.h`): `ringbuf_t` struct with `data/capacity/head/tail`, `ringbuf_status_t` enum, lifecycle and byte-copy signatures, include-guarded `RINGBUF_H` with `extern "C"`
- Implementation (`ringbuf.c`): capacity validation, `malloc`/`free` buffer management, `used`/`free_space` accounting, two-chunk `memcpy` wrap enqueue/dequeue
#### Key units
- `ringbuf_t` — circular storage with `uint8_t *data`, `capacity`, volatile `head` producer index, volatile `tail` consumer index
- `ringbuf_status_t` — `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`
- `ringbuf_init(rb, capacity)` — validates power-of-two, allocates buffer
- `ringbuf_free(rb)` — releases buffer, resets state
- `ringbuf_write(rb, src, len)` — enqueues up to `free_space` bytes, returns written count
- `ringbuf_read(rb, dst, len)` — dequeues up to `used` bytes, returns read count
- `ringbuf_used(rb)` — occupied byte count from `head-tail` distance
- `ringbuf_free_space(rb)` — writable count as `capacity-1-used`
- `is_power_of_two`, `min_size` — static validation and transfer clamping helpers
#### Data flow
- `ringbuf_init` → allocated `rb->data` heap buffer with `capacity`
- `ringbuf_write(src,len)` → clamped by `min_size` to `free_space` → split into two `memcpy` chunks at array end → advances `head` via `capacity-1` mask
- `ringbuf_read(dst,len)` → clamped by `min_size` to `used` → split into two `memcpy` chunks at array end → advances `tail` via mask
- `ringbuf_used` / `ringbuf_free_space` derive occupancy from masked `head-tail` difference
#### Dependencies
- Internal: `ringbuf.c` → `ringbuf.h` for `ringbuf_t`, `ringbuf_status_t`
- External: `stdlib.h` for `malloc/free`, `string.h` for `memcpy`, `stddef.h` for `size_t`, `stdint.h` for `uint8_t`
#### Cross-cutting
- Power-of-two capacity required, bitmask `capacity-1` modulo wrap, reserves one slot to distinguish full/empty
- SPSC ownership split: producer advances `head`, consumer advances `tail` via volatile `size_t`
- Partial-copy semantics: transfers clamped to available space/data, return actual byte counts
- Error handling via `RINGBUF_EINVAL` on zero/non-power-of-two capacity, `RINGBUF_ENOMEM` on allocation failure
