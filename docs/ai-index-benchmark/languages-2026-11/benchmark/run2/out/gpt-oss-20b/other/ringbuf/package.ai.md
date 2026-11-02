### other/ringbuf
- H: 1.0
- C: 44E432C7
- D: 2026-10-29T10:08:51Z
- T: 2026-11-01T01:44:15Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock‑free, power‑of‑two circular buffer for byte streams, enabling non‑blocking FIFO read/write in single‑producer single‑consumer contexts.

#### Purpose
- Offer a reusable, efficient FIFO for byte data.
- Support non‑blocking, wrap‑around operations with minimal overhead.

#### Responsibilities
- **Buffer lifecycle**: initialization, allocation, deallocation.
- **Data movement**: write and read with wrap‑around handling.
- **State queries**: used space and free space calculations.
- **Error handling**: validate capacity and report allocation failures.

#### Key units
- `ringbuf_t` – struct holding buffer pointer, capacity, head, tail.
- `ringbuf_status_t` enum – `RINGBUF_OK`, `RINGBUF_EINVAL`, `RINGBUF_ENOMEM`.
- `ringbuf_init(ringbuf_t *rb, size_t capacity)` – allocate and set up buffer.
- `ringbuf_free(ringbuf_t *rb)` – release resources.
- `ringbuf_write(ringbuf_t *rb, const uint8_t *src, size_t len)` – enqueue bytes.
- `ringbuf_read(ringbuf_t *rb, uint8_t *dst, size_t len)` – dequeue bytes.
- `ringbuf_used(const ringbuf_t *rb)` – current byte count.
- `ringbuf_free_space(const ringbuf_t *rb)` – available capacity.

#### Data flow
1. **Init** → allocate `data` array, set `head = tail = 0`.
2. **Write** → copy up to `len` bytes from `src` into `data` at `head`, wrap if needed, update `head`.
3. **Read** → copy up to `len` bytes from `data` at `tail` into `dst`, wrap if needed, update `tail`.
4. **Query** → `ringbuf_used` and `ringbuf_free_space` compute values via modulo arithmetic.

#### Dependencies
- Standard C headers: `<stddef.h>`, `<stdint.h>`, `<stdlib.h>`, `<string.h>`.
- Uses `malloc`, `free`, `memcpy`.
- Exposes API via `ringbuf.h`.

#### Cross-cutting
- **Power‑of‑two constraint**: `is_power_of_two` ensures capacity validity.
- **Modulo arithmetic**: `(index & (capacity - 1))` for wrap‑around.
- **Non‑blocking semantics**: functions return actual bytes processed; caller handles partial operations.
- **Error codes**: `RINGBUF_EINVAL` for invalid capacity, `RINGBUF_ENOMEM` for allocation failure.
