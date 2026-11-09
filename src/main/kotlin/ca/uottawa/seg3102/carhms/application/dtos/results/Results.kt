package ca.uottawa.seg3102.carhms.application.dtos.results

import ca.uottawa.seg3102.carhms.domain.hikes.HikeStatus
import ca.uottawa.seg3102.carhms.domain.registrations.RegistrationStatus
import ca.uottawa.seg3102.carhms.domain.trails.TrailDifficulty
import java.time.OffsetDateTime
import java.util.UUID

data class HikeSummaryDto(
    val id: UUID,
    val title: String,
    val trailName: String,
    val difficulty: TrailDifficulty,
    val startsAt: OffsetDateTime,
    val organizerName: String,
    val availablePlaces: Int,
    val status: HikeStatus,
    val eligibleForCurrentHiker: Boolean?
)

data class RegistrationResultDto(
    val registrationId: UUID,
    val status: RegistrationStatus,
    val waitlistPosition: Int?
)
