### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:27:17Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a fixed‑size ring buffer API for byte‑level I/O.
- Support initialization, deallocation, reading, writing, and space queries.

#### Responsibilities
- **Buffer Management** – allocate, free, and maintain head/tail indices.
- **Data Transfer** – read/write operations with wrap‑around handling.
- **Space Accounting** – compute used bytes and free space.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – create buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Data flow
1. **Initialization** → `ringbuf_init` allocates memory, sets indices to 0.  
2. **Writing** → `ringbuf_write` copies up to `len` bytes into the buffer, updates `head`, handles wrap‑around.  
3. **Reading** → `ringbuf_read` copies up to `len` bytes from the buffer to destination, updates `tail`, handles wrap‑around.  
4. **Space queries** → `ringbuf_used` and `ringbuf_free_space` compute counts via modulo arithmetic.  
5. **Cleanup** → `ringbuf_free` releases allocated memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and API; `ringbuf.c` implements logic.

#### Cross-cutting
- Power‑of‑two capacity enforcement (`is_power_of_two`).
- Lock‑free single‑producer/single‑consumer design (no mutexes).
- Error handling via `ringbuf_status_t` return codes.
- Memory management: dynamic allocation with `malloc`/`free`.
