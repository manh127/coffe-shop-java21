package com.coffeeshop.domain.shared;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
}



