package ca.uottawa.seg3102.carhms.contracts

import ca.uottawa.seg3102.carhms.application.services.*
import ca.uottawa.seg3102.carhms.application.usecases.implementation.*
import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.hikes.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import ca.uottawa.seg3102.carhms.domain.members.*
import ca.uottawa.seg3102.carhms.domain.notifications.*
import ca.uottawa.seg3102.carhms.domain.registrations.*
import ca.uottawa.seg3102.carhms.domain.trails.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/** Doubles de test, dans src/test uniquement. Ils ne remplacent pas les dépôts réels du livrable 3. */
class World {
    val administrator = UUID(0, 1)
    val organizer = UUID(0, 2)
    val hiker = UUID(0, 3)
    val secondHiker = UUID(0, 4)
    val thirdHiker = UUID(0, 5)
    val otherOrganizer = UUID(0, 6)
    val hikeId = UUID(0, 40)
    val trailId = "A"
    var time = OffsetDateTime.parse("2026-11-09T12:00:00-05:00")
    val clock = TimeProvider { time }
    val published = mutableListOf<DomainEvent>()
    val publisher = DomainEventPublisher { published.add(it) }
    val permissions = mutableMapOf<UUID, UserPermissions>()
    val identity = IdentityFacade(object : IdentityDirectory {
        override fun findActor(id: UUID) = permissions[id]
        override fun grant(id: UUID, role: Role) {
            val current = permissions[id]
            permissions[id] = UserPermissions(id, (current?.roles ?: emptySet()) + role)
        }
    })
    val members = linkedMapOf<UUID, Member>()
    val memberRepository = object : MemberRepository {
        override fun byId(id: UUID) = members[id]
        override fun byEmail(email: String) = members.values.firstOrNull { it.email == email }
        override fun save(member: Member) { members[member.id] = member }
    }
    val invitations = mutableSetOf("INVIT-2026")
    val invitationCodes = object : InvitationCodes {
        override fun consume(code: String) = invitations.remove(code)
    }
    val memberFactory = object : MemberFactory {
        override fun create(data: NewMember) = Member(UUID.randomUUID(), data.email, data.fullName,
            data.telephone, data.address, data.birthDate, data.emergencyName,
            data.emergencyPhone, data.skill)
    }
    val memberFacade = MemberFacade(memberRepository, memberFactory, invitationCodes, identity, publisher, clock)
    val trails = linkedMapOf<String, Trail>()
    val trailRepository = object : TrailRepository {
        override fun findById(id: String) = trails[id]
        override fun findAll() = trails.values.toList()
    }
    val trailFacade = TrailFacade(trailRepository)
    val hikes = linkedMapOf<UUID, Hike>()
    val hikeRepository = object : HikeRepository {
        override fun byId(id: UUID) = hikes[id]
        override fun all() = hikes.values.toList()
        override fun save(hike: Hike) { hikes[hike.id] = hike }
    }
    val hikeFactory = object : HikeFactory {
        override fun create(data: NewHike): Hike = Hike(UUID.randomUUID(), data.organizerId,
            data.title, data.description, data.trailId, data.startAt,
            data.durationMinutes, data.deadline, data.capacity)
    }
    val hikeFacade = HikeFacade(hikeRepository, hikeFactory, trailFacade, memberFacade,
        identity, publisher, clock)
    val registrations = linkedMapOf<UUID, Registration>()
    val registrationRepository = object : RegistrationRepository {
        override fun byId(id: UUID) = registrations[id]
        override fun forHike(hikeId: UUID) = registrations.values.filter { it.hikeId == hikeId }
        override fun save(registration: Registration) { registrations[registration.id] = registration }
    }
    val registrationFactory = object : RegistrationFactory {
        override fun create(hikerId: UUID, hikeId: UUID, at: OffsetDateTime,
            status: RegistrationStatus) = Registration(UUID.randomUUID(), hikerId, hikeId, at, status)
    }
    val registrationFacade = RegistrationFacade(registrationRepository, registrationFactory,
        hikeFacade, memberFacade, trailFacade, identity, publisher, clock)
    val emails = mutableListOf<EmailMessage>()
    val emailService = EmailService { emails.add(it) }
    var forecast: Forecast? = Forecast()
    var weatherFails = false
    val weatherService = WeatherService { _ ->
        if (weatherFails) error("Service météo indisponible") else forecast
    }
    val credentials = mutableMapOf<UUID, String>()
    val credentialService = CredentialService { id, _ -> credentials[id] = "hachage simulé (aucun mot de passe brut stocké)" }
    val notificationFacade = NotificationFacade(publisher, clock)
    val browse = BrowseHikesImpl(hikeFacade, trailFacade, registrationFacade, memberFacade)
    val register = RegisterHikerImpl(memberFacade, credentialService)
    val signUp = SignUpForHikeImpl(registrationFacade, memberFacade, emailService, notificationFacade)
    val cancelSignUp = CancelSignUpImpl(registrationFacade, memberFacade, emailService, notificationFacade)
    val update = UpdateHikerImpl(memberFacade)
    val close = CloseHikeImpl(hikeFacade)
    val remind = SendHikeReminderImpl(hikeFacade, registrationFacade, memberFacade,
        trailFacade, weatherService, emailService, notificationFacade, clock)
    val create = CreateHikeImpl(hikeFacade)
    val cancelHike = CancelHikeImpl(hikeFacade, registrationFacade, memberFacade, emailService, notificationFacade)

    init {
        permissions[administrator] = UserPermissions(administrator, setOf(Role.ADMINISTRATOR, Role.HIKER))
        permissions[organizer] = UserPermissions(organizer, setOf(Role.ORGANIZER, Role.HIKER))
        permissions[otherOrganizer] = UserPermissions(otherOrganizer, setOf(Role.ORGANIZER, Role.HIKER))
        for (id in listOf(hiker, secondHiker, thirdHiker)) permissions[id] = UserPermissions(id, setOf(Role.HIKER))
        for (id in listOf(administrator, organizer, otherOrganizer, hiker, secondHiker, thirdHiker)) {
            val cert = if (id == organizer || id == otherOrganizer) OrganizerCertificate(
                "CERT-${id}", time.toLocalDate().minusDays(30), time.toLocalDate().plusDays(60), TrailDifficulty.VERY_DIFFICULT
            ) else null
            members[id] = Member(id, "${id.leastSignificantBits}@example.test", "Randonneur ${id.leastSignificantBits}",
                "613-555-0000", "Ottawa", LocalDate.of(2000, 1, 1), "Urgence", "613-555-1111",
                SkillLevel.INTERMEDIATE, MembershipStatus.ACTIVE, cert)
        }
        trails[trailId] = Trail(trailId, "Sentier A", TrailDifficulty.EASY, true,
            TrailSeason.YEAR_ROUND, "Stationnement A")
        hikes[hikeId] = Hike(hikeId, organizer, "Randonnée A", "Sortie du club", trailId,
            time.plusDays(7), 120, time.plusDays(5), 2)
    }

    fun seedRegistration(member: UUID, status: RegistrationStatus, at: OffsetDateTime = time): Registration {
        val r = Registration(UUID.randomUUID(), member, hikeId, at, status)
        registrations[r.id] = r
        return r
    }
    fun full() {
        hikes[hikeId]!!.status = HikeStatus.FULL
        if (registrationFacade.confirmed(hikeId).size < 2) {
            seedRegistration(secondHiker, RegistrationStatus.CONFIRMED, time.minusMinutes(2))
            seedRegistration(thirdHiker, RegistrationStatus.CONFIRMED, time.minusMinutes(1))
        }
    }
}
