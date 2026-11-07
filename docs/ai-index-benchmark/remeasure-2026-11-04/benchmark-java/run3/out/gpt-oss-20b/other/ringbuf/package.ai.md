### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T07:49:44Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a fixed‑size ring buffer API for byte streams.
- Support initialization, cleanup, read/write, and space queries.

#### Responsibilities
- **Buffer management** – allocate, free, and maintain head/tail indices.
- **Data transfer** – read/write with wrap‑around handling.
- **Space accounting** – report used and free bytes.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – deallocate internal memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy up to `len` bytes into buffer.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy up to `len` bytes out of buffer.
- `ringbuf_used(const ringbuf_t *rb)` – compute bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – compute remaining capacity.

#### Data flow
1. **Initialization** – caller supplies power‑of‑two `capacity`; `ringbuf_init` allocates memory and zeroes indices.
2. **Writing** – producer calls `ringbuf_write`; data is copied into buffer, head advanced modulo capacity.
3. **Reading** – consumer calls `ringbuf_read`; data is copied out, tail advanced modulo capacity.
4. **Querying** – `ringbuf_used` and `ringbuf_free_space` use `(head - tail) & (capacity - 1)` to report status.
5. **Cleanup** – `ringbuf_free` releases allocated memory.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and API; `ringbuf.c` implements logic.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` validates capacity; errors returned as `RINGBUF_EINVAL`.
- **Wrap‑around logic** – both read and write handle up to two contiguous memory copies to accommodate buffer wrap.
- **Error handling** – `ringbuf_init` reports `RINGBUF_ENOMEM` on allocation failure; other functions return byte counts only.
