package ca.uottawa.seg3102.carhms.domain.members

import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import ca.uottawa.seg3102.carhms.domain.trails.TrailDifficulty
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class OrganizerCertificate(
    val number: String,
    val issuedAt: LocalDate,
    val expiresAt: LocalDate,
    val maximumDifficulty: TrailDifficulty
) {
    fun validAt(date: LocalDate) = !date.isBefore(issuedAt) && !date.isAfter(expiresAt)
}

// Entité : identité stable, informations modifiables et certification facultative.
data class Member(
    val id: UUID,
    val email: String,
    var fullName: String,
    var telephone: String,
    var address: String,
    val birthDate: LocalDate,
    var emergencyName: String,
    var emergencyPhone: String,
    var skillLevel: SkillLevel,
    var membership: MembershipStatus = MembershipStatus.ACTIVE,
    var certificate: OrganizerCertificate? = null
)

data class NewMember(
    val email: String, val fullName: String, val telephone: String,
    val address: String, val birthDate: LocalDate,
    val emergencyName: String, val emergencyPhone: String, val skill: SkillLevel
)

interface MemberFactory { fun create(data: NewMember): Member }
interface MemberRepository {
    fun byId(id: UUID): Member?
    fun byEmail(email: String): Member?
    fun save(member: Member)
}
interface InvitationCodes { fun consume(code: String): Boolean }

sealed interface MemberEvent : DomainEvent

data class MemberChanged(
    override val aggregateId: UUID, override val actorId: String,
    override val occurredAt: OffsetDateTime, val change: String
) : MemberEvent

class MemberFacade(
    private val members: MemberRepository, private val factory: MemberFactory,
    private val invitations: InvitationCodes, private val identity: IdentityFacade,
    private val events: DomainEventPublisher, private val clock: TimeProvider
) {
    fun get(id: UUID) = requireNotNull(members.byId(id)) { "Randonneur inexistant" }

    fun register(data: NewMember, invitation: String, waiver: Boolean,
                 guardian: String?, guardianWaiver: Boolean): UUID {
        ensure(data.email.contains('@') && data.fullName.isNotBlank(), "Données de membre invalides")
        ensure(members.byEmail(data.email) == null, "Courriel déjà enregistré")
        val age = java.time.Period.between(data.birthDate, clock.now().toLocalDate()).years
        ensure(age >= 0 && waiver, "Décharge obligatoire")
        if (age < 18) ensure(!guardian.isNullOrBlank() && guardianWaiver, "Consentement du tuteur requis")
        ensure(invitations.consume(invitation), "Code d'invitation invalide")
        val member = factory.create(data)
        members.save(member)
        identity.grant(member.id, Role.HIKER)
        events.publish(MemberChanged(member.id, member.id.toString(), clock.now(), "REGISTERED"))
        return member.id
    }

    fun updateProfile(actorId: UUID, memberId: UUID, fullName: String, telephone: String,
                      address: String, emergencyName: String, emergencyPhone: String, skill: SkillLevel) {
        identity.requireOwnerOrAdmin(actorId, memberId)
        ensure(fullName.isNotBlank(), "Nom obligatoire")
        val member = get(memberId)
        member.fullName = fullName; member.telephone = telephone; member.address = address
        member.emergencyName = emergencyName; member.emergencyPhone = emergencyPhone; member.skillLevel = skill
        members.save(member)
        events.publish(MemberChanged(memberId, actorId.toString(), clock.now(), "PROFILE_UPDATED"))
    }

    fun setMembership(actorId: UUID, memberId: UUID, status: MembershipStatus) {
        identity.require(actorId, Role.ADMINISTRATOR)
        val member = get(memberId)
        member.membership = status
        members.save(member)
        events.publish(MemberChanged(memberId, actorId.toString(), clock.now(), "MEMBERSHIP_CHANGED"))
    }

    fun approve(actorId: UUID, memberId: UUID, certificate: OrganizerCertificate) {
        identity.require(actorId, Role.ADMINISTRATOR)
        ensure(!certificate.expiresAt.isBefore(certificate.issuedAt), "Dates de certification invalides")
        val member = get(memberId)
        ensure(member.membership == MembershipStatus.ACTIVE, "Adhésion inactive")
        member.certificate = certificate
        identity.grant(member.id, Role.ORGANIZER)
        members.save(member)
        events.publish(MemberChanged(memberId, actorId.toString(), clock.now(), "ORGANIZER_APPROVED"))
    }
}
