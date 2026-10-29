### other/ringbuf
- H: 1.0
- C: 850DF002
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T10:00:41Z
- G: 
- A: 0.0.0
- X: package
- F: [ringbuf.c](ringbuf.c.ai.md)
- F: [ringbuf.h](ringbuf.h.ai.md)
---
> Provides a lock-free, single-producer/single-consumer circular byte buffer for efficient, thread-safe data exchange between concurrent entities.

#### Purpose
- Implements a fixed-capacity, power-of-two ring buffer for `uint8_t` data.
- Enables lock-free data transfer between one producer and one consumer using volatile index tracking.

#### Responsibilities
- **Buffer Lifecycle**: Handles allocation, initialization, and deallocation of the underlying byte array.
- **Data Operations**: Manages wrap-around read and write operations using bitwise modular arithmetic.
- **State Tracking**: Computes current occupancy and available free space, reserving one slot to distinguish full from empty states.

#### Key units
- `ringbuf_t`: Core structure holding `data` pointer, `capacity`, `head` (producer-owned), and `tail` (consumer-owned) indices.
- `ringbuf_status_t`: Enum defining `RINGBUF_OK`, `RINGBUF_EINVAL`, and `RINGBUF_ENOMEM` return codes.
- `ringbuf_init`: Allocates and initializes the buffer, validating that capacity is a power of two.
- `ringbuf_write`: Copies bytes into the buffer, handling wrap-around via up to two `memcpy` chunks.
- `ringbuf_read`: Copies bytes from the buffer to a destination, handling wrap-around via up to two `memcpy` chunks.
- `ringbuf_used`: Calculates current byte occupancy using `(head - tail) & (capacity - 1)`.
- `ringbuf_free_space`: Calculates available write space as `capacity - 1 - used`.
- `ringbuf_free`: Releases heap memory and resets structure fields.

#### Data flow
- **Initialization**: Caller provides a power-of-two `capacity`; `ringbuf_init` allocates memory and sets `head`/`tail` to zero.
- **Production**: Producer calls `ringbuf_write` with source data; function copies bytes into `rb->data` starting at `head`, advancing `head` with wrap-around.
- **Consumption**: Consumer calls `ringbuf_read` with destination buffer; function copies bytes from `rb->data` starting at `tail`, advancing `tail` with wrap-around.
- **Monitoring**: `ringbuf_used` and `ringbuf_free_space` provide real-time occupancy metrics based on the difference between `head` and `tail`.

#### Dependencies
- **Internal**: `ringbuf.h` (type definitions, API declarations), `ringbuf.c` (implementation).
- **External**: `stdlib.h` (`malloc`, `free`), `string.h` (`memcpy`), `stddef.h` (`size_t`), `stdint.h` (`uint8_t`).

#### Cross-cutting
- **Concurrency**: Uses `volatile` qualifiers on `head` and `tail` to ensure memory visibility across threads without explicit locking mechanisms.
- **Error Handling**: Returns `RINGBUF_EINVAL` for invalid capacity (non-power-of-two or zero) and `RINGBUF_ENOMEM` for allocation failures; no bounds checking on `memcpy` operations, requiring caller validation.
- **C++ Compatibility**: Header includes `extern "C"` guards for C++ interoperability.
