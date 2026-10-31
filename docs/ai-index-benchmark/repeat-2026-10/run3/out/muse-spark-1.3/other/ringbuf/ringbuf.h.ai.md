### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:24:18Z
- G: 
- A: 0.0.0
- X: file
---
> Single-producer single-consumer fixed-capacity byte FIFO queue for lock-free buffering.
#### Purpose
- Fixed-capacity byte ring buffer for single producer and single consumer
- Power-of-two capacity header-only API with one unused slot
#### Type
- C header file, `extern "C"` guarded
- `struct ringbuf` / `ringbuf_t` with `data`, `capacity`, volatile `head`, `tail`
- `enum ringbuf_status` / `ringbuf_status_t` status codes
#### Input
- `rb` ringbuf pointer, `src` write source bytes, `dst` read destination buffer
- `len` requested byte count, `capacity` power-of-two size
- `head` producer-owned write index, `tail` consumer-owned read index
#### Output
- Copied byte count from `ringbuf_write`, `ringbuf_read`
- Used byte count from `ringbuf_used`, free byte count from `ringbuf_free_space`
- Initialized `ringbuf_t.data/capacity/head/tail`, freed buffer via `ringbuf_free`
#### Core logic
- Circular byte FIFO holding `capacity - 1` bytes maximum
- SPSC ownership split: producer advances `head`, consumer advances `tail`
- Partial copy semantics: copies up to `len` bytes actually available
#### Public API
- `ringbuf_init(rb, capacity) -> ringbuf_status_t` - allocates power-of-two buffer
- `ringbuf_free(rb) -> void` - releases buffer memory
- `ringbuf_write(rb, src, len) -> size_t` - enqueues available bytes
- `ringbuf_read(rb, dst, len) -> size_t` - dequeues available bytes
- `ringbuf_used(rb) -> size_t` - reports occupied bytes
- `ringbuf_free_space(rb) -> size_t` - reports writable bytes
#### Dependencies
- `stddef.h` for `size_t`
- `stdint.h` for `uint8_t`
#### Exceptions / Errors
- `RINGBUF_OK = 0` success
- `RINGBUF_EINVAL = -1` zero or non-power-of-two capacity
- `RINGBUF_ENOMEM = -2` allocation failed
