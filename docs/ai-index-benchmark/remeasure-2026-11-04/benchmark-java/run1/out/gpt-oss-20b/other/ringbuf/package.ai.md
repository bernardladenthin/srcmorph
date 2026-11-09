### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:18:54Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for initialization, read/write, and space queries.

#### Purpose
- Offer a reusable, single‑producer/single‑consumer ring buffer.
- Enable efficient byte‑level buffering without dynamic resizing.

#### Responsibilities
- **Buffer lifecycle**: allocate, initialize, free.
- **Data movement**: write to and read from the circular buffer with wrap‑around handling.
- **Space accounting**: report used bytes and available free space.
- **Error handling**: validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy up to `len` bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy up to `len` bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb)` – current byte count.
- `ringbuf_free_space(const ringbuf_t *rb)` – remaining capacity.

#### Data flow
1. **Initialization** → `ringbuf_init` validates capacity (power‑of‑two), allocates memory, sets head/tail to 0.  
2. **Writing** → `ringbuf_write` copies data from source into buffer, handling up to two contiguous chunks if wrap‑around occurs, updates head.  
3. **Reading** → `ringbuf_read` copies data from buffer to destination, handling wrap‑around, updates tail.  
4. **Status queries** → `ringbuf_used` and `ringbuf_free_space` compute metrics via bit‑mask arithmetic.  
5. **Cleanup** → `ringbuf_free` releases allocated memory.

#### Dependencies
- Standard C library: `<stdlib.h>` (`malloc`, `free`), `<string.h>` (`memcpy`), `<stddef.h>`, `<stdint.h>`.
- Internal header `ringbuf.h` defines types and API.

#### Cross-cutting
- **Power‑of‑two constraint**: all capacity checks enforce this rule; non‑compliant sizes return `RINGBUF_EINVAL`.
- **Lock‑free design**: single‑producer/single‑consumer assumption; no mutexes or atomic operations required.
- **Wrap‑around logic**: both read and write functions split operations into two contiguous copies when necessary.
- **Error codes**: consistent use of `ringbuf_status_t` for initialization failures; write/read return byte counts only.
