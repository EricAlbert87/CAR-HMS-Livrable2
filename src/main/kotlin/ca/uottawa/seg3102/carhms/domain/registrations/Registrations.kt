package ca.uottawa.seg3102.carhms.domain.registrations

import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.hikes.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import ca.uottawa.seg3102.carhms.domain.members.*
import ca.uottawa.seg3102.carhms.domain.trails.*
import java.time.OffsetDateTime
import java.util.UUID

data class Registration(
    val id: UUID, val hikerId: UUID, val hikeId: UUID,
    val registeredAt: OffsetDateTime, var status: RegistrationStatus
)

interface RegistrationFactory {
    fun create(hikerId: UUID, hikeId: UUID, at: OffsetDateTime, status: RegistrationStatus): Registration
}
interface RegistrationRepository {
    fun byId(id: UUID): Registration?
    fun forHike(hikeId: UUID): List<Registration>
    fun save(registration: Registration)
}

sealed interface RegistrationEvent : DomainEvent

data class RegistrationChanged(
    override val aggregateId: UUID, override val actorId: String,
    override val occurredAt: OffsetDateTime, val change: RegistrationStatus
) : RegistrationEvent

data class Enrollment(val registration: Registration, val waitlistPosition: Int?)
data class CancelOutcome(val cancelled: Registration, val promoted: Registration?)

class RegistrationFacade(
    private val registrations: RegistrationRepository, private val factory: RegistrationFactory,
    private val hikes: HikeFacade, private val members: MemberFacade, private val trails: TrailFacade,
    private val identity: IdentityFacade, private val events: DomainEventPublisher,
    private val clock: TimeProvider
) {
    fun forHike(hikeId: UUID) = registrations.forHike(hikeId).sortedWith(compareBy({ it.registeredAt }, { it.id }))
    fun confirmed(hikeId: UUID) = forHike(hikeId).filter { it.status == RegistrationStatus.CONFIRMED }
    fun waiting(hikeId: UUID) = forHike(hikeId).filter { it.status == RegistrationStatus.WAITLISTED }
    fun freePlaces(hike: Hike) = (hike.capacity - confirmed(hike.id).size).coerceAtLeast(0)

    fun signUp(hikerId: UUID, hikeId: UUID): Enrollment {
        identity.require(hikerId, Role.HIKER, Role.ORGANIZER, Role.ADMINISTRATOR)
        val hike = hikes.get(hikeId)
        val member = members.get(hikerId)
        ensure(member.membership == MembershipStatus.ACTIVE, "Adhésion inactive")
        ensure(clock.now().isBefore(hike.deadline), "Date limite dépassée")
        ensure(hike.status == HikeStatus.OPEN || hike.status == HikeStatus.FULL, "Randonnée non accessible")
        ensure(trails.get(hike.trailId).difficulty.ordinal <= member.skillLevel.ordinal + 1, "Difficulté excessive")
        ensure(forHike(hikeId).none { it.hikerId == hikerId && it.status != RegistrationStatus.CANCELLED }, "Déjà inscrit")
        val confirmedCount = confirmed(hikeId).size
        val status = if (confirmedCount < hike.capacity) RegistrationStatus.CONFIRMED else RegistrationStatus.WAITLISTED
        val registration = factory.create(hikerId, hikeId, clock.now(), status)
        registrations.save(registration)
        events.publish(RegistrationChanged(registration.id, hikerId.toString(), clock.now(), status))
        if (status == RegistrationStatus.CONFIRMED && confirmedCount + 1 == hike.capacity)
            hikes.changeStatus(hikeId, HikeStatus.FULL, "SYSTEM")
        return Enrollment(registration, if (status == RegistrationStatus.WAITLISTED) waiting(hikeId).indexOfFirst { it.id == registration.id } + 1 else null)
    }

    fun cancel(hikerId: UUID, registrationId: UUID): CancelOutcome {
        identity.require(hikerId, Role.HIKER, Role.ORGANIZER, Role.ADMINISTRATOR)
        val registration = requireNotNull(registrations.byId(registrationId)) { "Inscription inconnue" }
        ensure(registration.hikerId == hikerId, "Inscription d'un autre membre")
        val hike = hikes.get(registration.hikeId)
        ensure(clock.now().isBefore(hike.deadline), "Date limite dépassée")
        ensure(registration.status != RegistrationStatus.CANCELLED, "Déjà annulée")
        val previouslyConfirmed = registration.status == RegistrationStatus.CONFIRMED
        registration.status = RegistrationStatus.CANCELLED
        registrations.save(registration)
        events.publish(RegistrationChanged(registration.id, hikerId.toString(), clock.now(), RegistrationStatus.CANCELLED))
        var promoted: Registration? = null
        if (previouslyConfirmed) {
            promoted = waiting(hike.id).firstOrNull()
            if (promoted != null) {
                promoted.status = RegistrationStatus.CONFIRMED
                registrations.save(promoted)
                events.publish(RegistrationChanged(promoted.id, "SYSTEM", clock.now(), RegistrationStatus.CONFIRMED))
            } else if (hike.status == HikeStatus.FULL) hikes.changeStatus(hike.id, HikeStatus.OPEN, "SYSTEM")
        }
        return CancelOutcome(registration, promoted)
    }

    fun cancelAll(hikeId: UUID) {
        for (registration in forHike(hikeId).filter { it.status != RegistrationStatus.CANCELLED }) {
            registration.status = RegistrationStatus.CANCELLED
            registrations.save(registration)
            events.publish(RegistrationChanged(registration.id, "SYSTEM", clock.now(), RegistrationStatus.CANCELLED))
        }
    }
}
