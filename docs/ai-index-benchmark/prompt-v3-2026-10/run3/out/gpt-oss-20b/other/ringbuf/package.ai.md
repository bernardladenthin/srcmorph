### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-10-31T20:46:26Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two sized byte ring buffer for single‑producer, single‑consumer use.

#### Purpose
- Offer a lightweight circular buffer for byte streams.
- Enable efficient, non‑blocking read/write with capacity checks.

#### Responsibilities
- **Buffer lifecycle** – initialization, allocation, deallocation.
- **Data movement** – write and read operations handling wrap‑around.
- **Metrics** – expose used bytes and free space.
- **Error handling** – validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – opaque struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` – enum with `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – copy up to `len` bytes, respecting free space.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – copy up to `len` bytes, respecting used space.
- `ringbuf_used(const ringbuf_t *rb)` – current byte count.
- `ringbuf_free_space(const ringbuf_t *rb)` – available byte count.

#### Data flow
1. **Init** → allocate memory, set indices, validate power‑of‑two capacity.  
2. **Write** → copy data into buffer, update head, wrap via modulo.  
3. **Read** → copy data from buffer, update tail, wrap via modulo.  
4. **Metrics** → `ringbuf_used` and `ringbuf_free_space` compute differences using bit‑masking.

#### Dependencies
- Standard C: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Header `ringbuf.h` defines types and API; implementation in `ringbuf.c`.

#### Cross-cutting
- **Power‑of‑two enforcement** – `is_power_of_two` helper ensures capacity validity.  
- **Modulo arithmetic** – indices wrap using `capacity - 1` mask.  
- **Single‑producer/single‑consumer** – lock‑free design relies on this assumption.  
- **Error codes** – consistent `ringbuf_status_t` return values for init failures.
