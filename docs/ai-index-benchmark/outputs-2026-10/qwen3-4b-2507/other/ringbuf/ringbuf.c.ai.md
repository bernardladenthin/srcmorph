### ringbuf.c
- H: 1.0
- C: 9F74BBA2
- D: 2026-10-28T21:09:44Z
- T: 2026-10-29T08:14:44Z
- G: 
- A: 0.0.0
- X: file
---
> Initializes and manages a circular buffer with efficient read/write operations

#### Purpose
- Manages a fixed-size circular buffer with power-of-two capacity requirements  
- Provides thread-safe read/write operations with bounded memory usage  

#### Type
- C implementation of a ring buffer (struct-based)  
- Implements circular buffer logic with wrap-around using modulo arithmetic  
- Includes initialization, cleanup, and access functions  

#### Input
- `capacity`: size of buffer (must be power of two)  
- `src`: source data for writing  
- `dst`: destination buffer for reading  
- `len`: number of bytes to read or write  

#### Output
- Returns number of bytes successfully written or read  
- On failure, returns error code (e.g., RINGBUF_EINVAL, RINGBUF_ENOMEM)  

#### Core logic
- Validates capacity is a power of two during initialization  
- Allocates memory for buffer if valid  
- Uses modulo arithmetic to wrap head/tail pointers in circular fashion  
- Splits writes/reads into two chunks: first to end of buffer, then from start  
- Updates head/tail positions after each operation to maintain circular state  

#### Public API
- `ringbuf_init(rb, capacity)` → `ringbuf_status_t` (initialize buffer)  
- `ringbuf_free(rb)` → `void` (free buffer memory)  
- `ringbuf_used(rb)` → `size_t` (returns bytes currently in buffer)  
- `ringbuf_free_space(rb)` → `size_t` (returns available space)  
- `ringbuf_write(rb, src, len)` → `size_t` (writes up to len bytes)  
- `ringbuf_read(rb, dst, len)` → `size_t` (reads up to len bytes)  

#### Dependencies
- `ringbuf.h` (header definition)  
- `stdlib.h` (for malloc/free)  
- `string.h` (for memcpy)  

#### Exceptions / Errors
- `RINGBUF_EINVAL`: invalid capacity (not power of two)  
- `RINGBUF_ENOMEM`: memory allocation failed  
- All other operations return success or error based on available space
