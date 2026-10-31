### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T15:17:55Z
- G: 
- A: 0.0.0
- X: file
---
> Single-producer single-consumer byte FIFO queue with fixed power-of-two capacity.
#### Purpose
- Fixed-capacity byte ring buffer for SPSC producer-consumer transfer
- Header-only API declaration for ringbuf_t lifecycle and byte copy
#### Type
- C header file, include-guarded `RINGBUF_H`, `extern "C"` compatible
- Defines struct `ringbuf_t` and enum `ringbuf_status_t`
#### Input
- `rb` ringbuf_t pointer, `capacity` byte capacity, `data` uint8_t buffer
- `src`/`len` bytes to enqueue, `dst`/`len` sink for dequeue
- `head` write index owned by producer, `tail` read index owned by consumer
#### Output
- `data` circular byte storage holding up to capacity-1 bytes
- Updated volatile `head`, `tail` indices, allocated/freed buffer memory
- Copied-byte counts, used-byte count, free-space count, status code
#### Core logic
- Requires power-of-two capacity, reserves one slot empty to distinguish full/empty
- SPSC ownership split: producer advances head, consumer advances tail via volatile size_t
- Partial-copy semantics: write/read copy up to len, return actual count
- Used/free-space derived from head-tail distance modulo capacity
#### Public API
- `ringbuf_init(ringbuf_t *rb, size_t capacity) -> ringbuf_status_t` - validates capacity, allocates buffer
- `ringbuf_free(ringbuf_t *rb) -> void` - releases buffer memory
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len) -> size_t` - enqueues available bytes
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len) -> size_t` - dequeues available bytes
- `ringbuf_used(const ringbuf_t *rb) -> size_t` - reports occupied byte count
- `ringbuf_free_space(const ringbuf_t *rb) -> size_t` - reports writable byte count
#### Dependencies
- `stddef.h` for size_t, `stdint.h` for uint8_t
#### Exceptions / Errors
- `RINGBUF_OK` success, `RINGBUF_EINVAL` zero/non-power-of-two capacity, `RINGBUF_ENOMEM` allocation failed
