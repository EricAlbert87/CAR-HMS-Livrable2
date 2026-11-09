package ca.uottawa.seg3102.carhms.application.dtos.queries

import ca.uottawa.seg3102.carhms.domain.members.MembershipStatus
import ca.uottawa.seg3102.carhms.domain.members.SkillLevel
import ca.uottawa.seg3102.carhms.domain.trails.TrailDifficulty
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class BrowseHikesQuery(
    val trailName: String? = null,
    val date: LocalDate? = null,
    val difficulty: TrailDifficulty? = null,
    val includeFull: Boolean = true,
    val viewerId: UUID? = null
)

data class RegisterHikerQuery(
    val invitationCode: String,
    val email: String,
    val password: String,
    val fullName: String,
    val telephone: String,
    val address: String,
    val birthDate: LocalDate,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val skillLevel: SkillLevel,
    val waiverAccepted: Boolean,
    val guardianName: String? = null,
    val guardianAcceptedWaiver: Boolean = false
)

data class SignUpForHikeQuery(val hikerId: UUID, val hikeId: UUID)

data class CancelSignUpQuery(val hikerId: UUID, val registrationId: UUID)

data class UpdateHikerProfileQuery(
    val actorId: UUID,
    val hikerId: UUID,
    val fullName: String,
    val telephone: String,
    val address: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val skillLevel: SkillLevel
)

data class UpdateMembershipStatusQuery(
    val administratorId: UUID,
    val hikerId: UUID,
    val status: MembershipStatus
)

data class ApproveOrganizerQuery(
    val administratorId: UUID,
    val hikerId: UUID,
    val certificateNumber: String,
    val issueDate: LocalDate,
    val expirationDate: LocalDate,
    val maximumDifficulty: TrailDifficulty
)

data class CloseHikeQuery(val hikeId: UUID, val actorId: String = "SYSTEM")

data class SendHikeReminderQuery(val hikeId: UUID)

data class CreateHikeQuery(
    val organizerId: UUID,
    val title: String,
    val description: String,
    val trailId: String,
    val startAt: OffsetDateTime,
    val plannedDurationMinutes: Int,
    val registrationDeadline: OffsetDateTime,
    val capacity: Int
)

data class CancelHikeQuery(
    val hikeId: UUID,
    val actorId: UUID,
    val reason: String
)
