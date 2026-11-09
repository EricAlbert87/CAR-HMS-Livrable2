package ca.uottawa.seg3102.carhms.domain.hikes

import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.members.*
import ca.uottawa.seg3102.carhms.domain.trails.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import java.time.OffsetDateTime
import java.util.UUID

// Entité racine de l'agrégat randonnée.
data class Hike(
    val id: UUID, val organizerId: UUID, val title: String, val description: String,
    val trailId: String, val startAt: OffsetDateTime, val durationMinutes: Int,
    val deadline: OffsetDateTime, val capacity: Int,
    var status: HikeStatus = HikeStatus.OPEN,
    var cancellationReason: String? = null
)

data class NewHike(val organizerId: UUID, val title: String, val description: String,
                   val trailId: String, val startAt: OffsetDateTime, val durationMinutes: Int,
                   val deadline: OffsetDateTime, val capacity: Int)

interface HikeFactory { fun create(data: NewHike): Hike }
interface HikeRepository {
    fun byId(id: UUID): Hike?
    fun all(): List<Hike>
    fun save(hike: Hike)
}

sealed interface HikeEvent : DomainEvent

data class HikeChanged(
    override val aggregateId: UUID, override val actorId: String,
    override val occurredAt: OffsetDateTime, val change: HikeStatus
) : HikeEvent

class HikeFacade(
    private val hikes: HikeRepository, private val factory: HikeFactory,
    private val trails: TrailFacade, private val members: MemberFacade,
    private val identity: IdentityFacade, private val events: DomainEventPublisher,
    private val clock: TimeProvider
) {
    fun get(id: UUID): Hike = requireNotNull(hikes.byId(id)) { "Randonnée inconnue" }
    fun all() = hikes.all()

    fun create(data: NewHike): UUID {
        identity.require(data.organizerId, Role.ORGANIZER, Role.ADMINISTRATOR)
        val organizer = members.get(data.organizerId)
        ensure(organizer.membership == MembershipStatus.ACTIVE, "Adhésion inactive")
        val cert = organizer.certificate ?: throw DomainRuleException("Certification manquante")
        ensure(cert.validAt(data.startAt.toLocalDate()), "Certification invalide")
        val trail = trails.get(data.trailId)
        ensure(trail.hikingAllowed && trail.availableAt(data.startAt.toLocalDate()), "Sentier non admissible")
        ensure(cert.maximumDifficulty.ordinal >= trail.difficulty.ordinal, "Niveau d'organisateur insuffisant")
        ensure(data.title.isNotBlank() && data.description.isNotBlank() && data.capacity > 0 && data.durationMinutes > 0, "Informations invalides")
        ensure(data.startAt.isAfter(clock.now()) && data.deadline.isAfter(clock.now()) && data.deadline.isBefore(data.startAt), "Dates invalides")
        ensure(!data.deadline.isBefore(data.startAt.minusDays(14)), "Date limite trop éloignée")
        val hike = factory.create(data)
        hikes.save(hike)
        events.publish(HikeChanged(hike.id, data.organizerId.toString(), clock.now(), HikeStatus.OPEN))
        return hike.id
    }

    fun changeStatus(id: UUID, status: HikeStatus, actor: String) {
        val hike = get(id)
        if (hike.status == status) return
        hike.status = status
        hikes.save(hike)
        events.publish(HikeChanged(id, actor, clock.now(), status))
    }

    fun close(id: UUID, actor: String) {
        val hike = get(id)
        ensure(!clock.now().isBefore(hike.deadline), "Date limite non atteinte")
        if (hike.status == HikeStatus.OPEN || hike.status == HikeStatus.FULL) changeStatus(id, HikeStatus.CLOSED, actor)
    }

    fun markCompleted(id: UUID) {
        val hike = get(id)
        ensure(clock.now().isAfter(hike.startAt), "Randonnée encore à venir")
        if (hike.status != HikeStatus.CANCELLED) changeStatus(id, HikeStatus.COMPLETED, "SYSTEM")
    }

    fun cancel(id: UUID, actorId: UUID, reason: String) {
        val hike = get(id)
        ensure(reason.isNotBlank(), "Motif requis")
        ensure(clock.now().isBefore(hike.startAt), "Randonnée déjà commencée")
        ensure(hike.status != HikeStatus.CANCELLED, "Randonnée déjà annulée")
        if (hike.organizerId != actorId) identity.require(actorId, Role.ADMINISTRATOR)
        else identity.require(actorId, Role.ORGANIZER, Role.ADMINISTRATOR)
        hike.cancellationReason = reason
        changeStatus(id, HikeStatus.CANCELLED, actorId.toString())
    }
}
