package ca.uottawa.seg3102.carhms.application.usecases.implementation

import ca.uottawa.seg3102.carhms.application.dtos.queries.*
import ca.uottawa.seg3102.carhms.application.dtos.results.*
import ca.uottawa.seg3102.carhms.application.services.*
import ca.uottawa.seg3102.carhms.application.usecases.*
import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.hikes.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import ca.uottawa.seg3102.carhms.domain.members.*
import ca.uottawa.seg3102.carhms.domain.notifications.*
import ca.uottawa.seg3102.carhms.domain.registrations.*
import ca.uottawa.seg3102.carhms.domain.trails.*
import java.util.UUID

class BrowseHikesImpl(
    private val hikes: HikeFacade, private val trails: TrailFacade,
    private val registrations: RegistrationFacade, private val members: MemberFacade
) : BrowseHikes {
    override fun browse(query: BrowseHikesQuery): List<HikeSummaryDto> = hikes.all()
        .asSequence()
        .filter { it.status == HikeStatus.OPEN || (query.includeFull && it.status == HikeStatus.FULL) }
        .filter { query.date == null || it.startAt.toLocalDate() == query.date }
        .filter { query.trailName == null || trails.get(it.trailId).name.contains(query.trailName, ignoreCase = true) }
        .filter { query.difficulty == null || trails.get(it.trailId).difficulty == query.difficulty }
        .sortedBy { it.startAt }
        .map { hike ->
            val trail = trails.get(hike.trailId)
            HikeSummaryDto(hike.id, hike.title, trail.name, trail.difficulty,
                hike.startAt, members.get(hike.organizerId).fullName,
                registrations.freePlaces(hike), hike.status,
                query.viewerId?.let { id ->
                    val viewer = members.get(id)
                    viewer.membership == MembershipStatus.ACTIVE &&
                        trail.difficulty.ordinal <= viewer.skillLevel.ordinal + 1
                })
        }.toList()
}

class RegisterHikerImpl(private val members: MemberFacade, private val credentials: CredentialService) : RegisterHiker {
    override fun register(query: RegisterHikerQuery): UUID {
        ensure(query.password.length >= 8, "Mot de passe trop court")
        val id = members.register(
            NewMember(query.email, query.fullName, query.telephone, query.address,
                query.birthDate, query.emergencyContactName, query.emergencyContactPhone, query.skillLevel),
            query.invitationCode, query.waiverAccepted, query.guardianName, query.guardianAcceptedWaiver)
        credentials.register(id, query.password)
        return id
    }
}

class SignUpForHikeImpl(
    private val registrations: RegistrationFacade, private val members: MemberFacade,
    private val email: EmailService, private val notifications: NotificationFacade
) : SignUpForHike {
    override fun signUp(query: SignUpForHikeQuery): RegistrationResultDto {
        val enrollment = registrations.signUp(query.hikerId, query.hikeId)
        val message = EmailMessage(members.get(query.hikerId).email, "Inscription à une randonnée",
            if (enrollment.waitlistPosition == null) "Votre inscription est confirmée."
            else "Position dans la liste d'attente : ${enrollment.waitlistPosition}")
        notifications.requested(query.hikeId, message)
        runCatching { email.enqueue(message) } // Publication d’événement préalable : livraison asynchrone ultérieure
        return RegistrationResultDto(enrollment.registration.id, enrollment.registration.status, enrollment.waitlistPosition)
    }
}

class CancelSignUpImpl(
    private val registrations: RegistrationFacade, private val members: MemberFacade,
    private val email: EmailService, private val notifications: NotificationFacade
) : CancelSignUp {
    override fun cancel(query: CancelSignUpQuery) {
        val outcome = registrations.cancel(query.hikerId, query.registrationId)
        outcome.promoted?.let { promoted ->
            val message = EmailMessage(members.get(promoted.hikerId).email,
                "Place confirmée", "Une place s'est libérée : votre inscription est confirmée.")
            notifications.requested(promoted.hikeId, message)
            runCatching { email.enqueue(message) } // Publication d’événement préalable : livraison asynchrone ultérieure
        }
    }
}

class UpdateHikerImpl(private val members: MemberFacade) : UpdateHiker {
    override fun updateProfile(query: UpdateHikerProfileQuery) = members.updateProfile(
        query.actorId, query.hikerId, query.fullName, query.telephone, query.address,
        query.emergencyContactName, query.emergencyContactPhone, query.skillLevel)

    override fun updateMembershipStatus(query: UpdateMembershipStatusQuery) =
        members.setMembership(query.administratorId, query.hikerId, query.status)

    override fun approveOrganizer(query: ApproveOrganizerQuery) = members.approve(
        query.administratorId, query.hikerId,
        OrganizerCertificate(query.certificateNumber, query.issueDate, query.expirationDate, query.maximumDifficulty))
}

class CloseHikeImpl(private val hikes: HikeFacade) : CloseHike {
    override fun close(query: CloseHikeQuery) = hikes.close(query.hikeId, query.actorId)
}

class SendHikeReminderImpl(
    private val hikes: HikeFacade, private val registrations: RegistrationFacade,
    private val members: MemberFacade, private val trails: TrailFacade,
    private val weather: WeatherService, private val email: EmailService,
    private val notifications: NotificationFacade, private val clock: TimeProvider
) : SendHikeReminder {
    override fun send(query: SendHikeReminderQuery): Int {
        val hike = hikes.get(query.hikeId)
        ensure(hike.status != HikeStatus.CANCELLED, "Randonnée annulée")
        ensure(hike.startAt.toLocalDate() == clock.now().toLocalDate().plusDays(1), "Rappel seulement la veille")
        val trail = trails.get(hike.trailId)
        val forecast = runCatching { weather.forecast(hike.trailId) }.getOrNull()
        val warning = if (forecast?.rainExpected == true || forecast?.snowExpected == true)
            " Attention : pluie ou neige prévue." else ""
        for (registration in registrations.confirmed(hike.id)) {
            val message = EmailMessage(members.get(registration.hikerId).email,
                "Rappel : ${hike.title}",
                "Randonnée ${hike.title} le ${hike.startAt}, sentier ${trail.name}, départ ${trail.meetingPoint}.$warning")
            notifications.requested(hike.id, message)
            runCatching { email.enqueue(message) } // Publication d’événement préalable : livraison asynchrone ultérieure
        }
        return registrations.confirmed(hike.id).size
    }
}

class CreateHikeImpl(private val hikes: HikeFacade) : CreateHike {
    override fun create(query: CreateHikeQuery): UUID = hikes.create(
        NewHike(query.organizerId, query.title, query.description, query.trailId,
            query.startAt, query.plannedDurationMinutes, query.registrationDeadline, query.capacity))
}

class CancelHikeImpl(
    private val hikes: HikeFacade, private val registrations: RegistrationFacade,
    private val members: MemberFacade, private val email: EmailService,
    private val notifications: NotificationFacade
) : CancelHike {
    override fun cancel(query: CancelHikeQuery) {
        hikes.cancel(query.hikeId, query.actorId, query.reason)
        val affected = registrations.forHike(query.hikeId).filter { it.status != RegistrationStatus.CANCELLED }
        registrations.cancelAll(query.hikeId)
        for (registration in affected) {
            val message = EmailMessage(members.get(registration.hikerId).email,
                "Randonnée annulée", "La randonnée est annulée. Motif : ${query.reason}")
            notifications.requested(query.hikeId, message)
            runCatching { email.enqueue(message) } // Publication d’événement préalable : livraison asynchrone ultérieure
        }
    }
}
