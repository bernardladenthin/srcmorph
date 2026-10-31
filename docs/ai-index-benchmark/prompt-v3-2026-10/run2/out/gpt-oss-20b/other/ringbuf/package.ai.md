### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:21:47Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two circular byte buffer for single‑producer/single‑consumer scenarios, exposing a minimal API for initialization, non‑blocking read/write, and space queries.

#### Purpose
- Provide a lightweight, thread‑safe ring buffer for byte streams.
- Offer non‑blocking enqueue/dequeue with wrap‑around handling.

#### Responsibilities
- **Buffer lifecycle**: allocate, initialize, and free the underlying byte array.
- **Data movement**: copy data into/out of the buffer while maintaining head/tail indices.
- **Space accounting**: compute used bytes and free space using modulo arithmetic.
- **Error handling**: validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, and tail indices.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – create buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – available space.

#### Data flow
1. **Initialization** → `ringbuf_init` validates capacity (power‑of‑two, non‑zero), allocates buffer, sets head/tail to 0.  
2. **Write** → `ringbuf_write` copies up to `len` bytes from source into buffer, wrapping around if necessary, and updates head.  
3. **Read** → `ringbuf_read` copies up to `len` bytes from buffer into destination, wrapping around if necessary, and updates tail.  
4. **Queries** → `ringbuf_used` and `ringbuf_free_space` compute values via `(head - tail) & (capacity - 1)` and complementary arithmetic.  
5. **Cleanup** → `ringbuf_free` releases the allocated memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal header `ringbuf.h` defines types and API signatures.

#### Cross-cutting
- **Power‑of‑two enforcement**: both init and write/read rely on modulo arithmetic for wrap‑around.
- **Single‑producer/single‑consumer**: no locks; head and tail are updated atomically by distinct threads.
- **Error codes**: consistent `ringbuf_status_t` values for invalid capacity or memory failure.
- **Memory management**: explicit `malloc`/`free` with zeroing of indices on init and reset on free.
