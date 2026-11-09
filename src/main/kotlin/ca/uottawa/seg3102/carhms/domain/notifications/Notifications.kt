package ca.uottawa.seg3102.carhms.domain.notifications

import ca.uottawa.seg3102.carhms.domain.common.*
import java.time.OffsetDateTime
import java.util.UUID

data class EmailMessage(val recipient: String, val subject: String, val body: String)
data class Forecast(val rainExpected: Boolean = false, val snowExpected: Boolean = false)

data class NotificationRequested(
    override val aggregateId: UUID, override val actorId: String,
    override val occurredAt: OffsetDateTime, val recipient: String
) : DomainEvent

class NotificationFacade(private val publisher: DomainEventPublisher, private val clock: TimeProvider) {
    fun requested(aggregateId: UUID, message: EmailMessage) {
        publisher.publish(NotificationRequested(aggregateId, "SYSTEM", clock.now(), message.recipient))
    }
}
