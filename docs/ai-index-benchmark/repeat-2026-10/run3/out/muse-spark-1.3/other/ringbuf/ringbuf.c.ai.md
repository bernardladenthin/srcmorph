### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:24:10Z
- G: 
- A: 0.0.0
- X: file
---
> Byte FIFO circular buffer with wrap-around reads/writes for streaming data storage.
#### Purpose
- Power-of-two ring buffer implementation for byte streaming with head/tail indices
- Bounded FIFO queue backing `ringbuf_t` lifecycle, occupancy query, read/write
#### Type
- C implementation file, includes `ringbuf.h`, static helpers + exported functions
#### Input
- `rb` pointer to `ringbuf_t` with `data`, `capacity`, `head`, `tail`
- `capacity` power-of-two size for `ringbuf_init`
- `src` + `len` byte stream for `ringbuf_write`
- `dst` + `len` destination for `ringbuf_read`
#### Output
- `ringbuf_status_t` init result: `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`
- `size_t` bytes written/read, used bytes, free space
- Mutated `ringbuf_t` head/tail, malloced/freed `data` buffer
#### Core logic
- Validates `capacity` via `is_power_of_two` bit trick `n & (n-1)`
- Mallocs `capacity` bytes in `ringbuf_init`, zeroes `head`/`tail`
- Computes `ringbuf_used` as `(head-tail) & (capacity-1)` mask wrap
- Reserves one slot: `free_space = capacity-1-used` distinguishes full vs empty
- Splits `ringbuf_write`/`ringbuf_read` into two `memcpy` chunks at array end
- Advances `head`/`tail` modulo `capacity` via `& (capacity-1)` bitmask
- Clamps transfers via `min_size` to `free_space` / `used`
#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` allocates power-of-two buffer
- `ringbuf_free(rb) -> void` releases buffer and resets state
- `ringbuf_used(rb) -> size_t` returns occupied byte count
- `ringbuf_free_space(rb) -> size_t` returns writable byte count
- `ringbuf_write(rb, src, len) -> size_t` enqueues bytes, returns written
- `ringbuf_read(rb, dst, len) -> size_t` dequeues bytes, returns read
#### Dependencies
- `ringbuf.h` for `ringbuf_t`, `ringbuf_status_t`
- `stdlib.h` for `malloc`, `free`
- `string.h` for `memcpy`
- `stdint.h` types via `uint8_t`, `stddef.h` via `size_t`
#### Exceptions / Errors
- `RINGBUF_EINVAL` on non-power-of-two `capacity`
- `RINGBUF_ENOMEM` on `malloc` failure, `data` NULL
