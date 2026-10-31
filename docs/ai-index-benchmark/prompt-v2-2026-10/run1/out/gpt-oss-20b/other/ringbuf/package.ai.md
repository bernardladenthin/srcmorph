### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T14:42:46Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Implements a lock‑free, power‑of‑two circular buffer for byte streams, exposing a simple API for initialization, read/write, and space queries.

#### Purpose
- Provide a single‑producer/single‑consumer ring buffer.
- Offer fast, memory‑efficient byte stream handling.

#### Responsibilities
- **Buffer lifecycle**: allocate, initialize, free.
- **Data movement**: write to buffer, read from buffer.
- **Space accounting**: report used and free bytes.
- **Error handling**: validate capacity, report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head/tail indices.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up.
- `ringbuf_free(ringbuf_t *rb)` – deallocate buffer.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy up to `len` bytes, respecting free space.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy up to `len` bytes, respecting used data.
- `ringbuf_used(const ringbuf_t *rb)` – compute `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space(const ringbuf_t *rb)` – compute `(capacity - 1) - used`.

#### Data flow
1. **Initialization** → `ringbuf_init` allocates a power‑of‑two buffer, sets `head = tail = 0`.
2. **Writing** → `ringbuf_write` copies data into the buffer, updates `head`, handles wrap‑around in two contiguous chunks.
3. **Reading** → `ringbuf_read` copies data out, updates `tail`, handles wrap‑around similarly.
4. **Querying** → `ringbuf_used` and `ringbuf_free_space` compute current usage via modulo arithmetic.
5. **Cleanup** → `ringbuf_free` releases the allocated memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and prototypes; `ringbuf.c` implements the logic.

#### Cross-cutting
- **Power‑of‑two requirement**: enforced in `ringbuf_init`; invalid capacity yields `RINGBUF_EINVAL`.
- **Single‑producer/single‑consumer**: no locks; head and tail are updated atomically by distinct threads.
- **Wrap‑around handling**: both read and write split into up to two `memcpy` calls to maintain contiguous data flow.
- **Error codes**: only `ringbuf_init` reports errors; other operations return the number of bytes processed.
