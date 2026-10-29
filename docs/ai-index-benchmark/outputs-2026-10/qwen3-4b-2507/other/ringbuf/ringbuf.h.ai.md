### ringbuf.h
- H: 1.0
- C: 601BDB34
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:14:48Z
- G: 
- A: 0.0.0
- X: file
---
> Provides a fixed-capacity, single-producer-single-consumer byte ring buffer with efficient read/write operations and space tracking.

#### Purpose
- Implements a thread-safe ring buffer for efficient data transfer between producer and consumer.  
- Enables bounded, circular buffer operations with precise tracking of used and free space.

#### Type
- Header file (C)  
- Contains struct `ringbuf_t`, enum `ringbuf_status_t`, and function prototypes  
- Uses `volatile` for atomic access to head/tail indices  
- Capacity must be a power of two; one slot is unused (holds `capacity - 1` bytes)

#### Input
- `capacity`: size of buffer (must be power of two, > 0)  
- `src`: source data for write operations  
- `dst`: destination buffer for read operations  
- `len`: number of bytes to copy (max limited by available space)

#### Output
- `ringbuf_write()`: returns number of bytes actually written (0 to `len`)  
- `ringbuf_read()`: returns number of bytes actually read (0 to `len`)  
- `ringbuf_used()`: returns number of bytes currently in buffer  
- `ringbuf_free_space()`: returns available bytes for writing  

#### Core logic
- Buffer is circular: head and tail indices wrap around using modulo arithmetic.  
- Head is incremented by producer; tail is incremented by consumer.  
- Write operation checks free space before copying; read operation checks available data.  
- Buffer capacity is enforced at compile time via power-of-two constraint.  
- All access to head/tail is volatile to support concurrent access.  

#### Public API
- `ringbuf_init(rb, capacity)` → `ringbuf_status_t` (initializes buffer, validates capacity)  
- `ringbuf_free(rb)` → void (frees allocated memory)  
- `ringbuf_write(rb, src, len)` → `size_t` (writes up to len bytes, returns actual count)  
- `ringbuf_read(rb, dst, len)` → `size_t` (reads up to len bytes, returns actual count)  
- `ringbuf_used(rb)` → `size_t` (returns current data size)  
- `ringbuf_free_space(rb)` → `size_t` (returns available space)

#### Dependencies
- `stddef.h` (for `size_t`)  
- `stdint.h` (for `uint8_t`, `uint32_t`)  

#### Exceptions / Errors
- `RINGBUF_EINVAL`: capacity is zero or not a power of two  
- `RINGBUF_ENOMEM`: memory allocation failed during initialization  
- No runtime exceptions; errors are returned via status codes
