### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T15:08:29Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling efficient single‑producer/single‑consumer data transfer.

#### Purpose
- Offer a fixed‑size, thread‑safe ring buffer for byte streams.
- Expose initialization, cleanup, read/write, and space queries.

#### Responsibilities
- **Buffer lifecycle**: allocate, initialize, free memory.
- **Data movement**: write to buffer, read from buffer, handle wrap‑around.
- **Capacity management**: compute used bytes and free space, enforce power‑of‑two size.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum (`RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`).
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy data into buffer, return bytes written.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy data out, return bytes read.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Data flow
1. **Initialization** → `ringbuf_init` validates capacity, allocates memory, sets head/tail to 0.  
2. **Writing** → `ringbuf_write` copies up to `len` bytes, wrapping at capacity, updates head.  
3. **Reading** → `ringbuf_read` copies up to `len` bytes, wrapping at capacity, updates tail.  
4. **Querying** → `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic.  
5. **Cleanup** → `ringbuf_free` releases allocated buffer.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` defines types and API; `ringbuf.c` implements logic.

#### Cross-cutting
- **Power‑of‑two enforcement**: `is_power_of_two` helper ensures capacity validity.
- **Wrap‑around handling**: `ringbuf_write` and `ringbuf_read` copy data in up to two contiguous chunks to manage buffer wrap.
- **Error handling**: `ringbuf_init` returns `RINGBUF_EINVAL` for invalid capacity, `RINGBUF_ENOMEM` for allocation failure.  
- **Lock‑free design**: intended for single‑producer/single‑consumer use without mutexes.
