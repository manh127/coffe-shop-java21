# Virtual Threads vs Reactive Programming

## Overview

This document explains the differences between Virtual Threads (Project Loom) and Reactive Programming (Spring WebFlux, Reactor), and when to choose each approach.

## Comparison

| Aspect | Virtual Threads | Reactive (WebFlux) |
|--------|----------------|-------------------|
| **Programming Model** | Imperative (blocking) | Declarative (non-blocking) |
| **Learning Curve** | Low (familiar to Java devs) | High (new concepts) |
| **Code Readability** | High | Medium to Low |
| **Debugging** | Easy (normal stack traces) | Difficult (async stack traces) |
| **Concurrency** | Millions of threads | Event loop + thread pool |
| **I/O Operations** | Blocking (but efficient) | Non-blocking |
| **Database Support** | JDBC (blocking) | R2DBC (reactive) |
| **Compatibility** | Works with existing libs | Requires reactive libs |
| **Error Handling** | Try-catch | onError operators |
| **Testing** | Standard testing | Reactive testing (StepVerifier) |

## Virtual Threads Example

```java
public Order createOrder(CreateOrderRequest request) {
    // Simple, imperative code
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        var productsFuture = scope.fork(() -> loadProducts(ids));
        var discountFuture = scope.fork(() -> calculateDiscount(customerId));
        
        scope.join();
        scope.throwIfFailed();
        
        // Use results
        var products = productsFuture.get();
        var discount = discountFuture.get();
        
        return createOrder(products, discount);
    }
}
```

## Reactive Example

```java
public Mono<Order> createOrder(CreateOrderRequest request) {
    return Mono.zip(
        loadProducts(ids),
        calculateDiscount(customerId)
    )
    .flatMap(tuple -> {
        var products = tuple.getT1();
        var discount = tuple.getT2();
        return Mono.just(createOrder(products, discount));
    })
    .onErrorResume(e -> Mono.error(new BusinessException(e.getMessage())));
}
```

## When to Use Virtual Threads

✅ **Choose Virtual Threads when:**
- You have **existing blocking code** (JDBC, RestTemplate, etc.)
- Team is **familiar with imperative programming**
- You need **easy debugging and stack traces**
- Code **readability is a priority**
- You're building a **monolithic application**
- Database is the primary bottleneck

## When to Use Reactive

✅ **Choose Reactive when:**
- You need **non-blocking I/O end-to-end**
- You're building **event-driven systems**
- You have **streaming data** (SSE, WebSockets)
- You need **backpressure handling**
- You're using **R2DBC** for database access
- Team is **experienced with reactive programming**

## Performance Comparison

### Throughput (Requests/Second)

For I/O-bound operations with similar connection limits:
- **Virtual Threads**: ~10,000 req/s (32 DB connections)
- **Reactive**: ~10,000 req/s (32 DB connections)

**Conclusion:** Similar performance for I/O-bound operations.

### Resource Usage

- **Virtual Threads**: Lower memory overhead per thread
- **Reactive**: Fewer platform threads, but more complex memory patterns

## Migration Path

### From Traditional Threads → Virtual Threads

**Easy migration:**
1. Enable Virtual Threads: `spring.threads.virtual.enabled=true`
2. No code changes required!
3. Existing blocking code works as-is

### From Traditional Threads → Reactive

**Complex migration:**
1. Rewrite services to return `Mono`/`Flux`
2. Replace JDBC with R2DBC
3. Update all dependencies to reactive versions
4. Rewrite tests with StepVerifier
5. Handle backpressure

## Project Loom Advantages

### 1. Structured Concurrency

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    var task1 = scope.fork(() -> operation1());
    var task2 = scope.fork(() -> operation2());
    
    scope.join();  // Wait for all
    scope.throwIfFailed();  // Fail fast
    
    // All tasks completed successfully
}
```

### 2. Scoped Values (JEP 446)

Better alternative to ThreadLocal for Virtual Threads:
```java
ScopedValue<String> USER = ScopedValue.newInstance();

ScopedValue.where(USER, "john").run(() -> {
    // USER.get() returns "john"
    processOrder();
});
```

### 3. Simple Error Handling

```java
try {
    var result = blockingOperation();
    return result;
} catch (IOException e) {
    throw new BusinessException("Failed", e);
}
```

## Conclusion

**For most applications**, Virtual Threads provide:
- ✅ Similar performance to reactive
- ✅ Much simpler code
- ✅ Easier debugging
- ✅ Better compatibility with existing libraries
- ✅ Lower learning curve

**Choose Reactive only if:**
- You specifically need non-blocking I/O end-to-end
- You're building event-driven/streaming systems
- Your team is already experienced with reactive programming

## Resources

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 453: Structured Concurrency](https://openjdk.org/jeps/453)
- [Spring Boot Virtual Threads Support](https://spring.io/blog/2023/09/09/all-together-now-spring-boot-3-2-graalvm-native-images-java-21-and-virtual)

