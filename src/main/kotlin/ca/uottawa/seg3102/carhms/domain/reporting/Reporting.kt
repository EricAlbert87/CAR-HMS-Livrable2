package ca.uottawa.seg3102.carhms.domain.reporting

import ca.uottawa.seg3102.carhms.domain.hikes.*
import ca.uottawa.seg3102.carhms.domain.registrations.*
import java.time.LocalDate

data class TrailUsage(val trailId: String, val hikes: Int, val confirmedRegistrations: Int, val cancelledHikes: Int)

class ReportingFacade(private val hikes: HikeRepository, private val registrations: RegistrationRepository) {
    fun usage(trailId: String, from: LocalDate, to: LocalDate): TrailUsage {
        require(!to.isBefore(from)) { "Période invalide" }
        val matched = hikes.all().filter { it.trailId == trailId && !it.startAt.toLocalDate().isBefore(from) && !it.startAt.toLocalDate().isAfter(to) }
        return TrailUsage(trailId, matched.size,
            matched.sumOf { hike -> registrations.forHike(hike.id).count { it.status == RegistrationStatus.CONFIRMED } },
            matched.count { it.status == HikeStatus.CANCELLED })
    }
}
