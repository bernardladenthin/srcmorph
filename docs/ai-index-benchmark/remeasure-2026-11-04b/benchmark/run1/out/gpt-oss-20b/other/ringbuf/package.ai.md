### other/ringbuf
- H: 1.0
- C: 040A7597
- D: 2026-10-29T10:08:51Z
- T: 2026-11-04T11:55:15Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> A lock‑free, power‑of‑two sized circular buffer for byte streams, exposing a minimal API for allocation, read/write, and space queries.

#### Purpose
- Provide a single‑producer/single‑consumer byte ring buffer.
- Enforce power‑of‑two capacity and efficient wrap‑around.

#### Responsibilities
- **Buffer lifecycle**: allocation, deallocation, and capacity validation.
- **Data movement**: contiguous or split copy for read/write with wrap‑around.
- **Space accounting**: used bytes and free space calculation.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail indices.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and initialize.
- `ringbuf_free(ringbuf_t *rb)` – release memory.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – write bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – read bytes.
- `ringbuf_used(const ringbuf_t *rb)` – bytes currently stored.
- `ringbuf_free_space(const ringbuf_t *rb)` – bytes available for writing.

#### Data flow
1. **Initialization**: `ringbuf_init` validates `capacity` (non‑zero, power‑of‑two), allocates buffer, sets `head = tail = 0`.
2. **Write**: `ringbuf_write` copies up to `len` bytes from source into buffer, possibly in two chunks if wrap‑around occurs, updates `head`.
3. **Read**: `ringbuf_read` copies up to `len` bytes from buffer to destination, possibly in two chunks, updates `tail`.
4. **Query**: `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic on `head` and `tail`.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Internal: `ringbuf.h` declares types and API; `ringbuf.c` implements them.

#### Cross-cutting
- **Power‑of‑two enforcement**: `is_power_of_two` helper ensures capacity validity.
- **Modulo arithmetic**: `(index & (capacity - 1))` for wrap‑around.
- **Error handling**: `ringbuf_init` returns specific status codes for invalid capacity or allocation failure; other functions return actual byte counts.
- **Thread model**: intended for single‑producer/single‑consumer use; no locks required.
