package ca.uottawa.seg3102.carhms.domain.common

import java.time.OffsetDateTime
import java.util.UUID

class DomainRuleException(message: String) : IllegalArgumentException(message)

interface DomainEvent {
    val aggregateId: UUID
    val actorId: String
    val occurredAt: OffsetDateTime
}

fun interface DomainEventPublisher { fun publish(event: DomainEvent) }
fun interface TimeProvider { fun now(): OffsetDateTime }

fun ensure(condition: Boolean, message: String) {
    if (!condition) throw DomainRuleException(message)
}
