# Code complet — Livrable 2 CAR-HMS

Les fichiers sont reproduits avec leur chemin exact, sans omission.

## `build.gradle.kts`

```kotlin
plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
}
group = "ca.uottawa.seg3502"
version = "0.2.0"
java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }
repositories { mavenCentral() }
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.cucumber:cucumber-java:7.20.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.20.1")
    testImplementation("org.junit.platform:junit-platform-suite:1.11.4")
}
tasks.test { useJUnitPlatform() }
```

## `settings.gradle.kts`

```kotlin
rootProject.name = "car-hms"
```

## `.github/workflows/contracts-manuel.yml`

```yaml
name: Contrats Cucumber - exécution manuelle
on:
  workflow_dispatch:
jobs:
  contrats:
    runs-on: ubuntu-latest
    timeout-minutes: 10
    steps:
      - name: Récupérer le dépôt
        uses: actions/checkout@v4
      - name: Installer Java 21
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
      - name: Installer Gradle
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.14.3'
      - name: Exécuter les contrats Cucumber
        run: gradle test --no-daemon
      - name: Archiver les rapports de tests
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: rapports-tests
          path: build/reports/tests/
          if-no-files-found: warn
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/CarHmsApplication.kt`

```kotlin
package ca.uottawa.seg3102.carhms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarHmsApplication

fun main(args: Array<String>) {
    runApplication<CarHmsApplication>(*args)
}
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/application/dtos/queries/Queries.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/application/dtos/results/Results.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/application/services/Services.kt`

```kotlin
package ca.uottawa.seg3102.carhms.application.services

import ca.uottawa.seg3102.carhms.domain.notifications.EmailMessage
import ca.uottawa.seg3102.carhms.domain.notifications.Forecast
import java.util.UUID

// Ports pour les services externes. Le livrable 2 utilise des doubles en tests.
fun interface EmailService { fun enqueue(message: EmailMessage) }
fun interface WeatherService { fun forecast(trailId: String): Forecast? }
fun interface CredentialService { fun register(memberId: UUID, rawPassword: String) }
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/application/usecases/UseCases.kt`

```kotlin
package ca.uottawa.seg3102.carhms.application.usecases

import ca.uottawa.seg3102.carhms.application.dtos.queries.*
import ca.uottawa.seg3102.carhms.application.dtos.results.*
import java.util.UUID

interface BrowseHikes {
    fun browse(query: BrowseHikesQuery): List<HikeSummaryDto>
}

interface RegisterHiker {
    fun register(query: RegisterHikerQuery): UUID
}

interface SignUpForHike {
    fun signUp(query: SignUpForHikeQuery): RegistrationResultDto
}

interface CancelSignUp {
    fun cancel(query: CancelSignUpQuery)
}

interface UpdateHiker {
    fun updateProfile(query: UpdateHikerProfileQuery)
    fun updateMembershipStatus(query: UpdateMembershipStatusQuery)
    fun approveOrganizer(query: ApproveOrganizerQuery)
}

interface CloseHike {
    fun close(query: CloseHikeQuery)
}

interface SendHikeReminder {
    fun send(query: SendHikeReminderQuery): Int
}

interface CreateHike {
    fun create(query: CreateHikeQuery): UUID
}

interface CancelHike {
    fun cancel(query: CancelHikeQuery)
}
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/application/usecases/implementation/UseCasesImpl.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/common/Domain.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/hikes/HikeStatus.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.hikes

enum class HikeStatus { OPEN, FULL, CLOSED, CANCELLED, COMPLETED }
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/hikes/Hikes.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/identity/Identity.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.identity

import ca.uottawa.seg3102.carhms.domain.common.ensure
import java.util.UUID

enum class Role { HIKER, ORGANIZER, ADMINISTRATOR }

data class UserPermissions(val id: UUID, val roles: Set<Role>, val authenticated: Boolean = true)

interface IdentityDirectory {
    fun findActor(id: UUID): UserPermissions?
    fun grant(id: UUID, role: Role)
}

class IdentityFacade(private val directory: IdentityDirectory) {
    fun grant(id: UUID, role: Role) = directory.grant(id, role)
    fun require(id: UUID, vararg allowed: Role) {
        val actor = directory.findActor(id)
        ensure(actor?.authenticated == true && allowed.any { it in actor.roles }, "Accès refusé")
    }
    fun requireOwnerOrAdmin(actorId: UUID, ownerId: UUID) {
        if (actorId == ownerId) require(actorId, Role.HIKER, Role.ORGANIZER, Role.ADMINISTRATOR)
        else require(actorId, Role.ADMINISTRATOR)
    }
    fun isAuthenticated(id: UUID) = directory.findActor(id)?.authenticated == true
}
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/members/MemberEnums.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.members

enum class SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }
enum class MembershipStatus { ACTIVE, EXPIRED, SUSPENDED }
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/members/Members.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/notifications/Notifications.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/registrations/RegistrationStatus.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.registrations

enum class RegistrationStatus { CONFIRMED, WAITLISTED, CANCELLED }
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/registrations/Registrations.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/reporting/Reporting.kt`

```kotlin
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
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/trails/TrailDifficulty.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.trails

enum class TrailDifficulty { EASY, MODERATE, DIFFICULT, VERY_DIFFICULT }
```

## `src/main/kotlin/ca/uottawa/seg3102/carhms/domain/trails/Trails.kt`

```kotlin
package ca.uottawa.seg3102.carhms.domain.trails

import java.time.LocalDate

enum class TrailSeason { YEAR_ROUND, SUMMER, WINTER }

data class Trail(
    val id: String,
    val name: String,
    val difficulty: TrailDifficulty,
    val hikingAllowed: Boolean,
    val season: TrailSeason,
    val meetingPoint: String
) {
    // Règle illustrative : à adapter à la disponibilité exacte publiée par la CCN.
    fun availableAt(date: LocalDate): Boolean = when (season) {
        TrailSeason.YEAR_ROUND -> true
        TrailSeason.SUMMER -> date.monthValue in 5..10
        TrailSeason.WINTER -> date.monthValue in 1..3 || date.monthValue == 12
    }
}

interface TrailRepository {
    fun findById(id: String): Trail?
    fun findAll(): List<Trail>
}

class TrailFacade(private val repository: TrailRepository) {
    fun get(id: String): Trail = requireNotNull(repository.findById(id)) { "Sentier inconnu" }
    fun findByName(name: String): List<Trail> = repository.findAll().filter {
        it.name.contains(name, ignoreCase = true)
    }
}
```

## `src/test/kotlin/ca/uottawa/seg3102/carhms/contracts/RunContractsTest.kt`

```kotlin
package ca.uottawa.seg3102.carhms.contracts

import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite
import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("contracts")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ca.uottawa.seg3102.carhms.contracts")
class RunContractsTest
```

## `src/test/kotlin/ca/uottawa/seg3102/carhms/contracts/StepBindings.kt`

```kotlin
package ca.uottawa.seg3102.carhms.contracts

import io.cucumber.java.en.Given

/** Correspondances exactes des étapes; générées depuis les 11 contrats. */
class StepBindings {
    private val handler = Steps()

    @Given("^\\Qaucun compte de randonneur n'est créé\\E${'$'}")
    fun etape001() = handler.step("aucun compte de randonneur n'est créé")

    @Given("^\\Qaucun inscrit uniquement en attente ne reçoit ce rappel\\E${'$'}")
    fun etape002() = handler.step("aucun inscrit uniquement en attente ne reçoit ce rappel")

    @Given("^\\Qaucune deuxième inscription active n'est créée\\E${'$'}")
    fun etape003() = handler.step("aucune deuxième inscription active n'est créée")

    @Given("^\\Qaucune nouvelle inscription ni annulation n'est acceptée\\E${'$'}")
    fun etape004() = handler.step("aucune nouvelle inscription ni annulation n'est acceptée")

    @Given("^\\Qaucune randonnée n'est créée\\E${'$'}")
    fun etape005() = handler.step("aucune randonnée n'est créée")

    @Given("^\\Qchaque inscrit confirmé reçoit un rappel avec les détails de la randonnée\\E${'$'}")
    fun etape006() = handler.step("chaque inscrit confirmé reçoit un rappel avec les détails de la randonnée")

    @Given("^\\Qdes randonnées ouvertes et une randonnée annulée\\E${'$'}")
    fun etape007() = handler.step("des randonnées ouvertes et une randonnée annulée")

    @Given("^\\Qdes randonnées sur plusieurs sentiers et à diverses dates\\E${'$'}")
    fun etape008() = handler.step("des randonnées sur plusieurs sentiers et à diverses dates")

    @Given("^\\Qil demande de modifier le profil d'un autre randonneur\\E${'$'}")
    fun etape009() = handler.step("il demande de modifier le profil d'un autre randonneur")

    @Given("^\\Qil indique une expiration antérieure à la délivrance\\E${'$'}")
    fun etape010() = handler.step("il indique une expiration antérieure à la délivrance")

    @Given("^\\Qil rejoint la fin de la liste d'attente horodatée\\E${'$'}")
    fun etape011() = handler.step("il rejoint la fin de la liste d'attente horodatée")

    @Given("^\\Qil soumet un numéro de téléphone et une adresse valides\\E${'$'}")
    fun etape012() = handler.step("il soumet un numéro de téléphone et une adresse valides")

    @Given("^\\Qil tente de créer la randonnée\\E${'$'}")
    fun etape013() = handler.step("il tente de créer la randonnée")

    @Given("^\\Qil tente de modifier un statut d'adhésion\\E${'$'}")
    fun etape014() = handler.step("il tente de modifier un statut d'adhésion")

    @Given("^\\Qil tente de s'y inscrire une deuxième fois\\E${'$'}")
    fun etape015() = handler.step("il tente de s'y inscrire une deuxième fois")

    @Given("^\\Ql'administrateur annule la randonnée avec un motif non vide\\E${'$'}")
    fun etape016() = handler.step("l'administrateur annule la randonnée avec un motif non vide")

    @Given("^\\Ql'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal\\E${'$'}")
    fun etape017() = handler.step("l'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal")

    @Given("^\\Ql'administrateur place son adhésion au statut « SUSPENDUE »\\E${'$'}")
    fun etape018() = handler.step("l'administrateur place son adhésion au statut « SUSPENDUE »")

    @Given("^\\Ql'annulation est refusée\\E${'$'}")
    fun etape019() = handler.step("l'annulation est refusée")

    @Given("^\\Ql'annulation est refusée et l'inscription ne change pas\\E${'$'}")
    fun etape020() = handler.step("l'annulation est refusée et l'inscription ne change pas")

    @Given("^\\Ql'auteur de la transition est enregistré comme « SYSTEM »\\E${'$'}")
    fun etape021() = handler.step("l'auteur de la transition est enregistré comme « SYSTEM »")

    @Given("^\\Ql'identité de l'administrateur est enregistrée\\E${'$'}")
    fun etape022() = handler.step("l'identité de l'administrateur est enregistrée")

    @Given("^\\Ql'inscription du titulaire passe à « ANNULÉE »\\E${'$'}")
    fun etape023() = handler.step("l'inscription du titulaire passe à « ANNULÉE »")

    @Given("^\\Ql'inscription est refusée\\E${'$'}")
    fun etape024() = handler.step("l'inscription est refusée")

    @Given("^\\Ql'opération est refusée\\E${'$'}")
    fun etape025() = handler.step("l'opération est refusée")

    @Given("^\\Ql'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides\\E${'$'}")
    fun etape026() = handler.step("l'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides")

    @Given("^\\Qla certification n'est pas enregistrée\\E${'$'}")
    fun etape027() = handler.step("la certification n'est pas enregistrée")

    @Given("^\\Qla création du compte est refusée\\E${'$'}")
    fun etape028() = handler.step("la création du compte est refusée")

    @Given("^\\Qla création est refusée\\E${'$'}")
    fun etape029() = handler.step("la création est refusée")

    @Given("^\\Qla date limite d'inscription est atteinte\\E${'$'}")
    fun etape030() = handler.step("la date limite d'inscription est atteinte")

    @Given("^\\Qla date limite d'inscription n'est pas atteinte\\E${'$'}")
    fun etape031() = handler.step("la date limite d'inscription n'est pas atteinte")

    @Given("^\\Qla date limite d'inscription se situe dans les 14 jours précédant le départ\\E${'$'}")
    fun etape032() = handler.step("la date limite d'inscription se situe dans les 14 jours précédant le départ")

    @Given("^\\Qla date limite est fixée à 15 jours avant le départ\\E${'$'}")
    fun etape033() = handler.step("la date limite est fixée à 15 jours avant le départ")

    @Given("^\\Qla première inscription en attente passe à « CONFIRMÉE »\\E${'$'}")
    fun etape034() = handler.step("la première inscription en attente passe à « CONFIRMÉE »")

    @Given("^\\Qla randonnée annulée n'est pas présentée\\E${'$'}")
    fun etape035() = handler.step("la randonnée annulée n'est pas présentée")

    @Given("^\\Qla randonnée complète est affichée avec zéro place disponible\\E${'$'}")
    fun etape036() = handler.step("la randonnée complète est affichée avec zéro place disponible")

    @Given("^\\Qla randonnée devient complète\\E${'$'}")
    fun etape037() = handler.step("la randonnée devient complète")

    @Given("^\\Qla randonnée passe au statut « FERMÉE »\\E${'$'}")
    fun etape038() = handler.step("la randonnée passe au statut « FERMÉE »")

    @Given("^\\Qla randonnée redevient ouverte\\E${'$'}")
    fun etape039() = handler.step("la randonnée redevient ouverte")

    @Given("^\\Qla seconde inscription conserve sa place dans la file\\E${'$'}")
    fun etape040() = handler.step("la seconde inscription conserve sa place dans la file")

    @Given("^\\Qle code d'invitation ne peut plus servir à une autre inscription\\E${'$'}")
    fun etape041() = handler.step("le code d'invitation ne peut plus servir à une autre inscription")

    @Given("^\\Qle membre soumet ses données sans consentement du tuteur légal\\E${'$'}")
    fun etape042() = handler.step("le membre soumet ses données sans consentement du tuteur légal")

    @Given("^\\Qle message contient un avertissement météorologique\\E${'$'}")
    fun etape043() = handler.step("le message contient un avertissement météorologique")

    @Given("^\\Qle nom du sentier, l'organisateur et les places disponibles sont indiqués\\E${'$'}")
    fun etape044() = handler.step("le nom du sentier, l'organisateur et les places disponibles sont indiqués")

    @Given("^\\Qle nouveau statut est enregistré\\E${'$'}")
    fun etape045() = handler.step("le nouveau statut est enregistré")

    @Given("^\\Qle randonneur demande son inscription\\E${'$'}")
    fun etape046() = handler.step("le randonneur demande son inscription")

    @Given("^\\Qle randonneur devient organisateur de randonnée\\E${'$'}")
    fun etape047() = handler.step("le randonneur devient organisateur de randonnée")

    @Given("^\\Qle randonneur ne peut plus effectuer de nouvelle inscription\\E${'$'}")
    fun etape048() = handler.step("le randonneur ne peut plus effectuer de nouvelle inscription")

    @Given("^\\Qle randonneur s'inscrit à la randonnée\\E${'$'}")
    fun etape049() = handler.step("le randonneur s'inscrit à la randonnée")

    @Given("^\\Qle randonneur tente de s'inscrire à une randonnée\\E${'$'}")
    fun etape050() = handler.step("le randonneur tente de s'inscrire à une randonnée")

    @Given("^\\Qle rappel est envoyé sans avertissement météorologique\\E${'$'}")
    fun etape051() = handler.step("le rappel est envoyé sans avertissement météorologique")

    @Given("^\\Qle service météorologique annonce de la pluie au point de départ\\E${'$'}")
    fun etape052() = handler.step("le service météorologique annonce de la pluie au point de départ")

    @Given("^\\Qle service météorologique est indisponible\\E${'$'}")
    fun etape053() = handler.step("le service météorologique est indisponible")

    @Given("^\\Qle statut de la randonnée devient « ANNULÉE »\\E${'$'}")
    fun etape054() = handler.step("le statut de la randonnée devient « ANNULÉE »")

    @Given("^\\Qle statut de la randonnée reste « ANNULÉE »\\E${'$'}")
    fun etape055() = handler.step("le statut de la randonnée reste « ANNULÉE »")

    @Given("^\\Qle système demande le nom du tuteur et son acceptation de la décharge\\E${'$'}")
    fun etape056() = handler.step("le système demande le nom du tuteur et son acceptation de la décharge")

    @Given("^\\Qle système exécute la fermeture automatique\\E${'$'}")
    fun etape057() = handler.step("le système exécute la fermeture automatique")

    @Given("^\\Qle système prépare le rappel\\E${'$'}")
    fun etape058() = handler.step("le système prépare le rappel")

    @Given("^\\Qle système prépare les rappels\\E${'$'}")
    fun etape059() = handler.step("le système prépare les rappels")

    @Given("^\\Qle système vérifie les fermetures à effectuer\\E${'$'}")
    fun etape060() = handler.step("le système vérifie les fermetures à effectuer")

    @Given("^\\Qle titulaire de la place confirmée annule son inscription\\E${'$'}")
    fun etape061() = handler.step("le titulaire de la place confirmée annule son inscription")

    @Given("^\\Qles détails de certification sont enregistrés\\E${'$'}")
    fun etape062() = handler.step("les détails de certification sont enregistrés")

    @Given("^\\Qles randonnées ouvertes sont présentées par date croissante\\E${'$'}")
    fun etape063() = handler.step("les randonnées ouvertes sont présentées par date croissante")

    @Given("^\\Qsa position lui est communiquée\\E${'$'}")
    fun etape064() = handler.step("sa position lui est communiquée")

    @Given("^\\Qses informations personnelles sont mises à jour\\E${'$'}")
    fun etape065() = handler.step("ses informations personnelles sont mises à jour")

    @Given("^\\Qseules les randonnées correspondant aux trois filtres sont présentées\\E${'$'}")
    fun etape066() = handler.step("seules les randonnées correspondant aux trois filtres sont présentées")

    @Given("^\\Qson créateur est enregistré\\E${'$'}")
    fun etape067() = handler.step("son créateur est enregistré")

    @Given("^\\Qson inscription est confirmée et horodatée\\E${'$'}")
    fun etape068() = handler.step("son inscription est confirmée et horodatée")

    @Given("^\\Qson organisateur demande son annulation\\E${'$'}")
    fun etape069() = handler.step("son organisateur demande son annulation")

    @Given("^\\Qson statut initial est « OUVERTE »\\E${'$'}")
    fun etape070() = handler.step("son statut initial est « OUVERTE »")

    @Given("^\\Qtoutes les inscriptions confirmées et en attente deviennent « ANNULÉE »\\E${'$'}")
    fun etape071() = handler.step("toutes les inscriptions confirmées et en attente deviennent « ANNULÉE »")

    @Given("^\\Qun administrateur authentifié\\E${'$'}")
    fun etape072() = handler.step("un administrateur authentifié")

    @Given("^\\Qun administrateur authentifié et un randonneur actif\\E${'$'}")
    fun etape073() = handler.step("un administrateur authentifié et un randonneur actif")

    @Given("^\\Qun code d'invitation inconnu ou déjà utilisé\\E${'$'}")
    fun etape074() = handler.step("un code d'invitation inconnu ou déjà utilisé")

    @Given("^\\Qun code d'invitation valide et non utilisé\\E${'$'}")
    fun etape075() = handler.step("un code d'invitation valide et non utilisé")

    @Given("^\\Qun compte de randonneur est créé avec une adhésion active\\E${'$'}")
    fun etape076() = handler.step("un compte de randonneur est créé avec une adhésion active")

    @Given("^\\Qun courriel contenant le motif est demandé pour chaque inscrit\\E${'$'}")
    fun etape077() = handler.step("un courriel contenant le motif est demandé pour chaque inscrit")

    @Given("^\\Qun courriel de confirmation est demandé\\E${'$'}")
    fun etape078() = handler.step("un courriel de confirmation est demandé")

    @Given("^\\Qun courriel de promotion est demandé\\E${'$'}")
    fun etape079() = handler.step("un courriel de promotion est demandé")

    @Given("^\\Qun identifiant unique est attribué à la randonnée\\E${'$'}")
    fun etape080() = handler.step("un identifiant unique est attribué à la randonnée")

    @Given("^\\Qun membre adulte soumet ses données obligatoires et accepte la décharge\\E${'$'}")
    fun etape081() = handler.step("un membre adulte soumet ses données obligatoires et accepte la décharge")

    @Given("^\\Qun membre tente de s'enregistrer\\E${'$'}")
    fun etape082() = handler.step("un membre tente de s'enregistrer")

    @Given("^\\Qun membre âgé de moins de 18 ans\\E${'$'}")
    fun etape083() = handler.step("un membre âgé de moins de 18 ans")

    @Given("^\\Qun niveau d'organisateur suffisant pour la difficulté du sentier\\E${'$'}")
    fun etape084() = handler.step("un niveau d'organisateur suffisant pour la difficulté du sentier")

    @Given("^\\Qun organisateur authentifié dont le certificat est valide à la date prévue\\E${'$'}")
    fun etape085() = handler.step("un organisateur authentifié dont le certificat est valide à la date prévue")

    @Given("^\\Qun organisateur dont le certificat expire avant la date prévue\\E${'$'}")
    fun etape086() = handler.step("un organisateur dont le certificat expire avant la date prévue")

    @Given("^\\Qun organisateur et un sentier admissibles\\E${'$'}")
    fun etape087() = handler.step("un organisateur et un sentier admissibles")

    @Given("^\\Qun organisateur non administrateur tente de l'annuler\\E${'$'}")
    fun etape088() = handler.step("un organisateur non administrateur tente de l'annuler")

    @Given("^\\Qun organisateur tente d'y créer une randonnée\\E${'$'}")
    fun etape089() = handler.step("un organisateur tente d'y créer une randonnée")

    @Given("^\\Qun randonneur annule son inscription confirmée avant la limite\\E${'$'}")
    fun etape090() = handler.step("un randonneur annule son inscription confirmée avant la limite")

    @Given("^\\Qun randonneur authentifié avec un profil existant\\E${'$'}")
    fun etape091() = handler.step("un randonneur authentifié avec un profil existant")

    @Given("^\\Qun randonneur authentifié sans droit d'administration\\E${'$'}")
    fun etape092() = handler.step("un randonneur authentifié sans droit d'administration")

    @Given("^\\Qun randonneur authentifié sans rôle d'administrateur\\E${'$'}")
    fun etape093() = handler.step("un randonneur authentifié sans rôle d'administrateur")

    @Given("^\\Qun randonneur avec une adhésion active\\E${'$'}")
    fun etape094() = handler.step("un randonneur avec une adhésion active")

    @Given("^\\Qun randonneur connecté admissible sans inscription à cette randonnée\\E${'$'}")
    fun etape095() = handler.step("un randonneur connecté admissible sans inscription à cette randonnée")

    @Given("^\\Qun randonneur connecté dont l'adhésion est active\\E${'$'}")
    fun etape096() = handler.step("un randonneur connecté dont l'adhésion est active")

    @Given("^\\Qun randonneur demande l'annulation de son inscription\\E${'$'}")
    fun etape097() = handler.step("un randonneur demande l'annulation de son inscription")

    @Given("^\\Qun randonneur dont l'adhésion est suspendue\\E${'$'}")
    fun etape098() = handler.step("un randonneur dont l'adhésion est suspendue")

    @Given("^\\Qun randonneur déjà inscrit à une randonnée\\E${'$'}")
    fun etape099() = handler.step("un randonneur déjà inscrit à une randonnée")

    @Given("^\\Qun sentier CCN non admissible à la date demandée\\E${'$'}")
    fun etape100() = handler.step("un sentier CCN non admissible à la date demandée")

    @Given("^\\Qun sentier CCN où la randonnée est permise, ouvert à la saison choisie\\E${'$'}")
    fun etape101() = handler.step("un sentier CCN où la randonnée est permise, ouvert à la saison choisie")

    @Given("^\\Qun visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »\\E${'$'}")
    fun etape102() = handler.step("un visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »")

    @Given("^\\Qun visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »\\E${'$'}")
    fun etape103() = handler.step("un visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »")

    @Given("^\\Qun visiteur parcourt les randonnées sans filtre\\E${'$'}")
    fun etape104() = handler.step("un visiteur parcourt les randonnées sans filtre")

    @Given("^\\Qune adresse courriel non enregistrée\\E${'$'}")
    fun etape105() = handler.step("une adresse courriel non enregistrée")

    @Given("^\\Qune inscription confirmée et deux inscriptions en attente ordonnées\\E${'$'}")
    fun etape106() = handler.step("une inscription confirmée et deux inscriptions en attente ordonnées")

    @Given("^\\Qune randonnée admissible ouverte avec une place restante\\E${'$'}")
    fun etape107() = handler.step("une randonnée admissible ouverte avec une place restante")

    @Given("^\\Qune randonnée annulée avant la date limite\\E${'$'}")
    fun etape108() = handler.step("une randonnée annulée avant la date limite")

    @Given("^\\Qune randonnée complète avant la date limite d'inscription\\E${'$'}")
    fun etape109() = handler.step("une randonnée complète avant la date limite d'inscription")

    @Given("^\\Qune randonnée complète avant sa date limite\\E${'$'}")
    fun etape110() = handler.step("une randonnée complète avant sa date limite")

    @Given("^\\Qune randonnée complète dont la date limite d'inscription n'est pas atteinte\\E${'$'}")
    fun etape111() = handler.step("une randonnée complète dont la date limite d'inscription n'est pas atteinte")

    @Given("^\\Qune randonnée complète sans liste d'attente\\E${'$'}")
    fun etape112() = handler.step("une randonnée complète sans liste d'attente")

    @Given("^\\Qune randonnée dont la date de début est passée\\E${'$'}")
    fun etape113() = handler.step("une randonnée dont la date de début est passée")

    @Given("^\\Qune randonnée dont la date limite d'inscription est passée\\E${'$'}")
    fun etape114() = handler.step("une randonnée dont la date limite d'inscription est passée")

    @Given("^\\Qune randonnée future avec des inscrits confirmés et en attente\\E${'$'}")
    fun etape115() = handler.step("une randonnée future avec des inscrits confirmés et en attente")

    @Given("^\\Qune randonnée future créée par un autre organisateur\\E${'$'}")
    fun etape116() = handler.step("une randonnée future créée par un autre organisateur")

    @Given("^\\Qune randonnée ouverte ou complète\\E${'$'}")
    fun etape117() = handler.step("une randonnée ouverte ou complète")

    @Given("^\\Qune randonnée prévue le lendemain avec des inscriptions confirmées\\E${'$'}")
    fun etape118() = handler.step("une randonnée prévue le lendemain avec des inscriptions confirmées")

    @Given("^\\Qune randonnée prévue le lendemain avec une inscription confirmée\\E${'$'}")
    fun etape119() = handler.step("une randonnée prévue le lendemain avec une inscription confirmée")

}
```

## `src/test/kotlin/ca/uottawa/seg3102/carhms/contracts/Steps.kt`

```kotlin
package ca.uottawa.seg3102.carhms.contracts

import ca.uottawa.seg3102.carhms.application.dtos.queries.*
import ca.uottawa.seg3102.carhms.application.dtos.results.*
import ca.uottawa.seg3102.carhms.domain.common.*
import ca.uottawa.seg3102.carhms.domain.hikes.*
import ca.uottawa.seg3102.carhms.domain.identity.*
import ca.uottawa.seg3102.carhms.domain.members.*
import ca.uottawa.seg3102.carhms.domain.notifications.*
import ca.uottawa.seg3102.carhms.domain.registrations.*
import ca.uottawa.seg3102.carhms.domain.trails.*
import java.time.LocalDate
import java.util.UUID

/**
 * Définitions des étapes du livrable 1. Une seule expression régulière route chaque
 * texte et rejette explicitement toute étape inconnue (aucun scénario ne passe silencieusement).
 */
class Steps {
    private val w = World() // Nouveau contexte par scénario Cucumber
    private var resultId: UUID? = null
    private var enrollment: RegistrationResultDto? = null
    private var found: List<HikeSummaryDto> = emptyList()
    private var failed: Throwable? = null
    private var priorCount = 0
    private var selectedRegistration: UUID? = null
    private var selectedMember = w.hiker
    private var lastQuery: CreateHikeQuery? = null

    private fun attempt(action: () -> Unit) {
        failed = runCatching(action).exceptionOrNull()
    }
    private fun requireSuccess() = check(failed == null) { "Erreur inattendue : ${failed?.message}" }
    private fun requireFailure() = check(failed != null) { "La commande aurait dû échouer" }

    private fun registrationQuery() = RegisterHikerQuery(
        "INVIT-2026", "nouveau@example.test", "motdepasse123", "Nouveau Membre",
        "613-555-1234", "Ottawa", LocalDate.of(2000, 1, 1), "Contact Urgence",
        "613-555-9999", SkillLevel.BEGINNER, true
    )
    private fun createQuery() = CreateHikeQuery(w.organizer, "Nouvelle randonnée",
        "Sortie organisée", w.trailId, w.time.plusDays(8), 120, w.time.plusDays(3), 4)
    private fun currentHike() = w.hikes[w.hikeId]!!

    // L'annotation Cucumber est la même pour Étant donné, Quand, Alors et Et.
    // En Gherkin, le mot-clé n'intervient pas dans la correspondance des étapes.
    fun step(text: String) {
        when (text) {
            // Parcourir
            "des randonnées ouvertes et une randonnée annulée" -> {
                w.hikes[UUID(0, 51)] = currentHike().copy(id = UUID(0, 51), startAt = w.time.plusDays(2))
                w.hikes[UUID(0, 52)] = currentHike().copy(id = UUID(0, 52), status = HikeStatus.CANCELLED, startAt = w.time.plusDays(1))
            }
            "des randonnées sur plusieurs sentiers et à diverses dates" -> {
                w.trails["B"] = Trail("B", "Sentier B", TrailDifficulty.DIFFICULT, true, TrailSeason.YEAR_ROUND, "Départ B")
                w.hikes[UUID(0, 53)] = currentHike().copy(id = UUID(0, 53), trailId = "B", startAt = w.time.plusDays(9))
            }
            "une randonnée complète dont la date limite d'inscription n'est pas atteinte" -> w.full()
            "un visiteur parcourt les randonnées sans filtre" -> found = w.browse.browse(BrowseHikesQuery())
            "un visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »" ->
                found = w.browse.browse(BrowseHikesQuery("Sentier A", currentHike().startAt.toLocalDate(), TrailDifficulty.EASY))
            "un visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »" ->
                found = w.browse.browse(BrowseHikesQuery(includeFull = true))
            "les randonnées ouvertes sont présentées par date croissante" -> {
                check(found.all { it.status == HikeStatus.OPEN }); check(found.map { it.startsAt } == found.map { it.startsAt }.sorted())
            }
            "la randonnée annulée n'est pas présentée" -> check(found.none { it.status == HikeStatus.CANCELLED })
            "le nom du sentier, l'organisateur et les places disponibles sont indiqués" ->
                check(found.all { it.trailName.isNotBlank() && it.organizerName.isNotBlank() && it.availablePlaces >= 0 })
            "seules les randonnées correspondant aux trois filtres sont présentées" ->
                check(found.size == 1 && found.all { it.trailName == "Sentier A" && it.difficulty == TrailDifficulty.EASY })
            "la randonnée complète est affichée avec zéro place disponible" ->
                check(found.any { it.status == HikeStatus.FULL && it.availablePlaces == 0 })

            // Enregistrer un randonneur
            "un code d'invitation valide et non utilisé", "une adresse courriel non enregistrée" -> Unit
            "un membre âgé de moins de 18 ans" -> Unit
            "un code d'invitation inconnu ou déjà utilisé" -> w.invitations.clear()
            "un membre adulte soumet ses données obligatoires et accepte la décharge" -> attempt { resultId = w.register.register(registrationQuery()) }
            "le membre soumet ses données sans consentement du tuteur légal" -> attempt {
                w.register.register(registrationQuery().copy(birthDate = LocalDate.of(2012, 1, 1), guardianName = null, guardianAcceptedWaiver = false))
            }
            "un membre tente de s'enregistrer" -> attempt { w.register.register(registrationQuery()) }
            "un compte de randonneur est créé avec une adhésion active" -> {
                requireSuccess(); check(resultId != null && w.members[resultId]?.membership == MembershipStatus.ACTIVE)
            }
            "le code d'invitation ne peut plus servir à une autre inscription" -> check("INVIT-2026" !in w.invitations)
            "la création du compte est refusée", "aucun compte de randonneur n'est créé" -> {
                requireFailure(); check(w.members.size == 6)
            }
            "le système demande le nom du tuteur et son acceptation de la décharge" ->
                check(failed?.message?.contains("tuteur") == true)

            // Inscription à une randonnée
            "un randonneur connecté dont l'adhésion est active" -> Unit
            "une randonnée admissible ouverte avec une place restante" -> w.seedRegistration(w.secondHiker, RegistrationStatus.CONFIRMED)
            "la date limite d'inscription n'est pas atteinte" -> check(w.time.isBefore(currentHike().deadline))
            "une randonnée complète avant sa date limite" -> w.full()
            "un randonneur connecté admissible sans inscription à cette randonnée" -> Unit
            "un randonneur dont l'adhésion est suspendue" -> w.members[w.hiker]!!.membership = MembershipStatus.SUSPENDED
            "un randonneur déjà inscrit à une randonnée" -> w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED)
            "le randonneur s'inscrit à la randonnée", "le randonneur demande son inscription" -> attempt {
                enrollment = w.signUp.signUp(SignUpForHikeQuery(w.hiker, w.hikeId))
            }
            "le randonneur tente de s'inscrire à une randonnée", "il tente de s'y inscrire une deuxième fois" -> attempt {
                w.signUp.signUp(SignUpForHikeQuery(w.hiker, w.hikeId))
            }
            "son inscription est confirmée et horodatée" -> {
                requireSuccess(); check(enrollment?.status == RegistrationStatus.CONFIRMED)
                check(w.registrations[enrollment!!.registrationId]?.registeredAt == w.time)
            }
            "la randonnée devient complète" -> check(currentHike().status == HikeStatus.FULL)
            "un courriel de confirmation est demandé" -> check(w.emails.any { it.body.contains("confirmée") })
            "il rejoint la fin de la liste d'attente horodatée" -> {
                requireSuccess(); check(enrollment?.status == RegistrationStatus.WAITLISTED)
                check(enrollment?.waitlistPosition == w.registrationFacade.waiting(w.hikeId).size)
            }
            "sa position lui est communiquée" -> check(w.emails.any { it.body.contains("Position") })
            "l'inscription est refusée" -> requireFailure()
            "aucune deuxième inscription active n'est créée" -> {
                requireFailure(); check(w.registrationFacade.forHike(w.hikeId).count { it.hikerId == w.hiker && it.status != RegistrationStatus.CANCELLED } == 1)
            }

            // Annuler une inscription
            "une randonnée complète avant la date limite d'inscription" -> w.full()
            "une inscription confirmée et deux inscriptions en attente ordonnées" -> {
                w.registrations.clear()
                w.hikes[w.hikeId] = currentHike().copy(capacity = 1, status = HikeStatus.FULL)
                val r = w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED, w.time.minusMinutes(3))
                selectedRegistration = r.id; selectedMember = w.hiker
                w.seedRegistration(w.secondHiker, RegistrationStatus.WAITLISTED, w.time.minusMinutes(2))
                w.seedRegistration(w.thirdHiker, RegistrationStatus.WAITLISTED, w.time.minusMinutes(1))
            }
            "une randonnée complète sans liste d'attente" -> {
                w.full(); selectedMember = w.secondHiker
                selectedRegistration = w.registrations.values.first { it.hikerId == selectedMember }.id
            }
            "une randonnée dont la date limite d'inscription est passée" -> {
                selectedRegistration = w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED).id
                w.time = currentHike().deadline.plusMinutes(1)
            }
            "le titulaire de la place confirmée annule son inscription",
            "un randonneur annule son inscription confirmée avant la limite" -> attempt {
                w.cancelSignUp.cancel(CancelSignUpQuery(selectedMember, selectedRegistration!!))
            }
            "un randonneur demande l'annulation de son inscription" -> attempt {
                w.cancelSignUp.cancel(CancelSignUpQuery(w.hiker, selectedRegistration!!))
            }
            "l'inscription du titulaire passe à « ANNULÉE »" -> check(w.registrations[selectedRegistration]?.status == RegistrationStatus.CANCELLED)
            "la première inscription en attente passe à « CONFIRMÉE »" ->
                check(w.registrations.values.any { it.hikerId == w.secondHiker && it.status == RegistrationStatus.CONFIRMED })
            "un courriel de promotion est demandé" -> check(w.emails.any { it.subject.contains("Place confirmée") })
            "la seconde inscription conserve sa place dans la file" ->
                check(w.registrations.values.any { it.hikerId == w.thirdHiker && it.status == RegistrationStatus.WAITLISTED })
            "la randonnée redevient ouverte" -> { requireSuccess(); check(currentHike().status == HikeStatus.OPEN) }
            "l'annulation est refusée et l'inscription ne change pas" -> {
                requireFailure(); check(w.registrations[selectedRegistration]?.status == RegistrationStatus.CONFIRMED)
            }

            // Modifier le profil
            "un randonneur authentifié avec un profil existant", "un randonneur authentifié sans droit d'administration" -> Unit
            "il soumet un numéro de téléphone et une adresse valides" -> attempt {
                w.update.updateProfile(UpdateHikerProfileQuery(w.hiker, w.hiker, "Nom modifié", "613-555-3333",
                    "Adresse modifiée", "Contact", "613-555-1234", SkillLevel.INTERMEDIATE))
            }
            "il demande de modifier le profil d'un autre randonneur" -> attempt {
                w.update.updateProfile(UpdateHikerProfileQuery(w.hiker, w.secondHiker, "Fraude", "123",
                    "Adresse", "Contact", "456", SkillLevel.EXPERT))
            }
            "ses informations personnelles sont mises à jour" -> {
                requireSuccess(); check(w.members[w.hiker]?.fullName == "Nom modifié")
                check(w.published.any { it is MemberChanged && it.change == "PROFILE_UPDATED" })
            }
            "l'opération est refusée" -> requireFailure()

            // Modifier une adhésion
            "un administrateur authentifié et un randonneur actif", "un randonneur authentifié sans rôle d'administrateur" -> Unit
            "l'administrateur place son adhésion au statut « SUSPENDUE »" -> attempt {
                w.update.updateMembershipStatus(UpdateMembershipStatusQuery(w.administrator, w.hiker, MembershipStatus.SUSPENDED))
            }
            "il tente de modifier un statut d'adhésion" -> attempt {
                w.update.updateMembershipStatus(UpdateMembershipStatusQuery(w.hiker, w.secondHiker, MembershipStatus.SUSPENDED))
            }
            "le nouveau statut est enregistré" -> { requireSuccess(); check(w.members[w.hiker]?.membership == MembershipStatus.SUSPENDED) }
            "le randonneur ne peut plus effectuer de nouvelle inscription" -> {
                check(runCatching { w.signUp.signUp(SignUpForHikeQuery(w.hiker, w.hikeId)) }.isFailure)
            }

            // Approuver un organisateur
            "un administrateur authentifié", "un randonneur avec une adhésion active" -> Unit
            "l'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal" -> attempt {
                w.update.approveOrganizer(ApproveOrganizerQuery(w.administrator, w.hiker,
                    "CERT-100", w.time.toLocalDate().minusDays(3), w.time.toLocalDate().plusDays(40), TrailDifficulty.DIFFICULT))
            }
            "il indique une expiration antérieure à la délivrance" -> attempt {
                w.update.approveOrganizer(ApproveOrganizerQuery(w.administrator, w.hiker,
                    "CERT-INVALID", w.time.toLocalDate(), w.time.toLocalDate().minusDays(1), TrailDifficulty.EASY))
            }
            "les détails de certification sont enregistrés" -> {
                requireSuccess(); check(w.members[w.hiker]?.certificate?.number == "CERT-100")
            }
            "le randonneur devient organisateur de randonnée" ->
                check(Role.ORGANIZER in w.permissions[w.hiker]!!.roles)
            "la certification n'est pas enregistrée" -> { requireFailure(); check(w.members[w.hiker]?.certificate == null) }

            // Fermer
            "une randonnée ouverte ou complète" -> Unit
            "la date limite d'inscription est atteinte" -> { w.time = currentHike().deadline }
            "une randonnée annulée avant la date limite" -> {
                currentHike().status = HikeStatus.CANCELLED; w.time = currentHike().deadline
            }
            "le système exécute la fermeture automatique", "le système vérifie les fermetures à effectuer" -> attempt {
                w.close.close(CloseHikeQuery(w.hikeId))
            }
            "la randonnée passe au statut « FERMÉE »" -> { requireSuccess(); check(currentHike().status == HikeStatus.CLOSED) }
            "l'auteur de la transition est enregistré comme « SYSTEM »" ->
                check(w.published.any { it is HikeChanged && it.change == HikeStatus.CLOSED && it.actorId == "SYSTEM" })
            "aucune nouvelle inscription ni annulation n'est acceptée" -> {
                check(runCatching { w.signUp.signUp(SignUpForHikeQuery(w.hiker, w.hikeId)) }.isFailure)
                val existing = w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED)
                check(runCatching { w.cancelSignUp.cancel(CancelSignUpQuery(w.hiker, existing.id)) }.isFailure)
            }
            "le statut de la randonnée reste « ANNULÉE »" -> { requireSuccess(); check(currentHike().status == HikeStatus.CANCELLED) }

            // Envoyer des rappels
            "une randonnée prévue le lendemain avec des inscriptions confirmées" -> {
                w.hikes[w.hikeId] = currentHike().copy(startAt = w.time.plusDays(1))
                w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED)
                w.seedRegistration(w.secondHiker, RegistrationStatus.CONFIRMED)
                w.seedRegistration(w.thirdHiker, RegistrationStatus.WAITLISTED)
            }
            "une randonnée prévue le lendemain avec une inscription confirmée" -> {
                w.hikes[w.hikeId] = currentHike().copy(startAt = w.time.plusDays(1))
                w.seedRegistration(w.hiker, RegistrationStatus.CONFIRMED)
            }
            "le service météorologique annonce de la pluie au point de départ" -> w.forecast = Forecast(rainExpected = true)
            "le service météorologique est indisponible" -> w.weatherFails = true
            "le système prépare les rappels", "le système prépare le rappel" -> attempt { w.remind.send(SendHikeReminderQuery(w.hikeId)) }
            "chaque inscrit confirmé reçoit un rappel avec les détails de la randonnée" -> {
                requireSuccess(); check(w.emails.size == 2)
                check(w.emails.all { it.body.contains("Sentier A") && it.body.contains("Stationnement A") })
            }
            "le message contient un avertissement météorologique" -> check(w.emails.all { it.body.contains("pluie ou neige") })
            "aucun inscrit uniquement en attente ne reçoit ce rappel" ->
                check(w.emails.none { it.recipient == w.members[w.thirdHiker]!!.email })
            "le rappel est envoyé sans avertissement météorologique" -> {
                requireSuccess(); check(w.emails.size == 1 && w.emails.none { it.body.contains("pluie ou neige") })
            }

            // Créer une randonnée
            "un organisateur authentifié dont le certificat est valide à la date prévue",
            "un sentier CCN où la randonnée est permise, ouvert à la saison choisie",
            "un niveau d'organisateur suffisant pour la difficulté du sentier" -> Unit
            "un organisateur dont le certificat expire avant la date prévue" -> {
                w.members[w.organizer]!!.certificate = w.members[w.organizer]!!.certificate!!.copy(
                    expiresAt = w.time.toLocalDate().minusDays(1))
            }
            "un sentier CCN non admissible à la date demandée" -> {
                w.trails[w.trailId] = w.trails[w.trailId]!!.copy(hikingAllowed = false)
            }
            "un organisateur et un sentier admissibles" -> Unit
            "l'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides",
            "il tente de créer la randonnée", "un organisateur tente d'y créer une randonnée" -> attempt {
                lastQuery = createQuery(); resultId = w.create.create(lastQuery!!)
            }
            "la date limite d'inscription se situe dans les 14 jours précédant le départ" -> {
                check(lastQuery != null && !lastQuery!!.registrationDeadline.isBefore(lastQuery!!.startAt.minusDays(14)))
            }
            "la date limite est fixée à 15 jours avant le départ" -> attempt {
                lastQuery = createQuery().copy(startAt = w.time.plusDays(18), registrationDeadline = w.time.plusDays(3))
                resultId = w.create.create(lastQuery!!)
            }
            "un identifiant unique est attribué à la randonnée" -> { requireSuccess(); check(resultId != null && w.hikes.containsKey(resultId)) }
            "son statut initial est « OUVERTE »" -> check(w.hikes[resultId]?.status == HikeStatus.OPEN)
            "son créateur est enregistré" -> check(w.hikes[resultId]?.organizerId == w.organizer)
            "aucune randonnée n'est créée", "la création est refusée" -> {
                requireFailure(); check(w.hikes.size == 1 && resultId == null)
            }

            // Annuler une randonnée
            "une randonnée future avec des inscrits confirmés et en attente" -> {
                w.full(); w.seedRegistration(w.hiker, RegistrationStatus.WAITLISTED)
            }
            "une randonnée future créée par un autre organisateur" -> Unit
            "une randonnée dont la date de début est passée" -> { w.time = currentHike().startAt.plusHours(1) }
            "l'administrateur annule la randonnée avec un motif non vide" -> attempt {
                w.cancelHike.cancel(CancelHikeQuery(w.hikeId, w.administrator, "Mauvais temps"))
            }
            "un organisateur non administrateur tente de l'annuler" -> attempt {
                w.cancelHike.cancel(CancelHikeQuery(w.hikeId, w.otherOrganizer, "Indisponibilité"))
            }
            "son organisateur demande son annulation" -> attempt {
                w.cancelHike.cancel(CancelHikeQuery(w.hikeId, w.organizer, "Retard"))
            }
            "le statut de la randonnée devient « ANNULÉE »" -> { requireSuccess(); check(currentHike().status == HikeStatus.CANCELLED) }
            "toutes les inscriptions confirmées et en attente deviennent « ANNULÉE »" ->
                check(w.registrations.values.all { it.status == RegistrationStatus.CANCELLED })
            "un courriel contenant le motif est demandé pour chaque inscrit" ->
                check(w.emails.size == 3 && w.emails.all { it.body.contains("Mauvais temps") })
            "l'identité de l'administrateur est enregistrée" ->
                check(w.published.any { it is HikeChanged && it.change == HikeStatus.CANCELLED && it.actorId == w.administrator.toString() })
            "l'annulation est refusée" -> { requireFailure(); check(currentHike().status != HikeStatus.CANCELLED) }

            else -> error("Étape Cucumber non implémentée : « $text »")
        }
    }
}
```

## `src/test/kotlin/ca/uottawa/seg3102/carhms/contracts/World.kt`

```kotlin
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
```

## `src/test/resources/contracts/annuler_inscription.feature`

```gherkin
# language: fr
Fonctionnalité: Annuler une inscription
  Une annulation confirme la désinscription et peut libérer une place.

  Scénario: Promouvoir la première personne en attente
    Étant donné une randonnée complète avant la date limite d'inscription
    Et une inscription confirmée et deux inscriptions en attente ordonnées
    Quand le titulaire de la place confirmée annule son inscription
    Alors l'inscription du titulaire passe à « ANNULÉE »
    Et la première inscription en attente passe à « CONFIRMÉE »
    Et un courriel de promotion est demandé
    Et la seconde inscription conserve sa place dans la file

  Scénario: Redevenir ouverte lorsqu'aucune personne n'attend
    Étant donné une randonnée complète sans liste d'attente
    Quand un randonneur annule son inscription confirmée avant la limite
    Alors la randonnée redevient ouverte

  Scénario: Refuser l'annulation après la date limite
    Étant donné une randonnée dont la date limite d'inscription est passée
    Quand un randonneur demande l'annulation de son inscription
    Alors l'annulation est refusée et l'inscription ne change pas
```

## `src/test/resources/contracts/annuler_randonnee.feature`

```gherkin
# language: fr
Fonctionnalité: Annuler une randonnée
  L'organisateur responsable ou un administrateur peut annuler avant le départ.

  Scénario: Annuler une randonnée et prévenir les inscrits
    Étant donné une randonnée future avec des inscrits confirmés et en attente
    Et un administrateur authentifié
    Quand l'administrateur annule la randonnée avec un motif non vide
    Alors le statut de la randonnée devient « ANNULÉE »
    Et toutes les inscriptions confirmées et en attente deviennent « ANNULÉE »
    Et un courriel contenant le motif est demandé pour chaque inscrit
    Et l'identité de l'administrateur est enregistrée

  Scénario: Refuser l'annulation par un organisateur non responsable
    Étant donné une randonnée future créée par un autre organisateur
    Quand un organisateur non administrateur tente de l'annuler
    Alors l'annulation est refusée

  Scénario: Refuser l'annulation après le début
    Étant donné une randonnée dont la date de début est passée
    Quand son organisateur demande son annulation
    Alors l'annulation est refusée
```

## `src/test/resources/contracts/approuver_organisateur.feature`

```gherkin
# language: fr
Fonctionnalité: Approuver un organisateur
  Un administrateur saisit la certification de chef de randonnée.

  Scénario: Accorder le rôle d'organisateur à un membre certifié
    Étant donné un administrateur authentifié
    Et un randonneur avec une adhésion active
    Quand l'administrateur fournit un certificat numéroté, des dates valides et un niveau maximal
    Alors les détails de certification sont enregistrés
    Et le randonneur devient organisateur de randonnée

  Scénario: Refuser une certification dont les dates sont incohérentes
    Étant donné un administrateur authentifié
    Quand il indique une expiration antérieure à la délivrance
    Alors la certification n'est pas enregistrée
```

## `src/test/resources/contracts/creer_randonnee.feature`

```gherkin
# language: fr
Fonctionnalité: Créer une randonnée
  Seul un organisateur qualifié peut publier un événement conforme.

  Scénario: Créer une randonnée admissible
    Étant donné un organisateur authentifié dont le certificat est valide à la date prévue
    Et un sentier CCN où la randonnée est permise, ouvert à la saison choisie
    Et un niveau d'organisateur suffisant pour la difficulté du sentier
    Quand l'organisateur soumet un titre, une description, un horaire, une durée et une capacité valides
    Et la date limite d'inscription se situe dans les 14 jours précédant le départ
    Alors un identifiant unique est attribué à la randonnée
    Et son statut initial est « OUVERTE »
    Et son créateur est enregistré

  Scénario: Rejeter un certificat expiré le jour de la randonnée
    Étant donné un organisateur dont le certificat expire avant la date prévue
    Quand il tente de créer la randonnée
    Alors aucune randonnée n'est créée

  Scénario: Refuser un sentier fermé en saison ou interdit à la randonnée
    Étant donné un sentier CCN non admissible à la date demandée
    Quand un organisateur tente d'y créer une randonnée
    Alors la création est refusée

  Scénario: Refuser une date limite antérieure de plus de 14 jours
    Étant donné un organisateur et un sentier admissibles
    Quand la date limite est fixée à 15 jours avant le départ
    Alors la création est refusée
```

## `src/test/resources/contracts/enregistrer_randonneur.feature`

```gherkin
# language: fr
Fonctionnalité: Enregistrer un randonneur
  Un membre du club utilise un code d'invitation pour ouvrir un compte.

  Scénario: Inscription valide d'un membre adulte
    Étant donné un code d'invitation valide et non utilisé
    Et une adresse courriel non enregistrée
    Quand un membre adulte soumet ses données obligatoires et accepte la décharge
    Alors un compte de randonneur est créé avec une adhésion active
    Et le code d'invitation ne peut plus servir à une autre inscription

  Scénario: Décharge d'un membre mineur
    Étant donné un membre âgé de moins de 18 ans
    Quand le membre soumet ses données sans consentement du tuteur légal
    Alors la création du compte est refusée
    Et le système demande le nom du tuteur et son acceptation de la décharge

  Scénario: Refuser une invitation non valide
    Étant donné un code d'invitation inconnu ou déjà utilisé
    Quand un membre tente de s'enregistrer
    Alors aucun compte de randonneur n'est créé
```

## `src/test/resources/contracts/envoyer_rappel.feature`

```gherkin
# language: fr
Fonctionnalité: Envoyer un rappel de randonnée
  Les inscrits confirmés reçoivent un rappel la veille.

  Scénario: Inclure un avertissement pour pluie ou neige
    Étant donné une randonnée prévue le lendemain avec des inscriptions confirmées
    Et le service météorologique annonce de la pluie au point de départ
    Quand le système prépare les rappels
    Alors chaque inscrit confirmé reçoit un rappel avec les détails de la randonnée
    Et le message contient un avertissement météorologique
    Et aucun inscrit uniquement en attente ne reçoit ce rappel

  Scénario: Envoyer les rappels si la météo est indisponible
    Étant donné une randonnée prévue le lendemain avec une inscription confirmée
    Et le service météorologique est indisponible
    Quand le système prépare le rappel
    Alors le rappel est envoyé sans avertissement météorologique
```

## `src/test/resources/contracts/fermer_randonnee.feature`

```gherkin
# language: fr
Fonctionnalité: Fermer une randonnée
  Le système bloque les mouvements d'inscription dès la date limite.

  Scénario: Fermer automatiquement une randonnée à la date limite
    Étant donné une randonnée ouverte ou complète
    Et la date limite d'inscription est atteinte
    Quand le système exécute la fermeture automatique
    Alors la randonnée passe au statut « FERMÉE »
    Et l'auteur de la transition est enregistré comme « SYSTEM »
    Et aucune nouvelle inscription ni annulation n'est acceptée

  Scénario: Conserver le statut annulé
    Étant donné une randonnée annulée avant la date limite
    Quand le système vérifie les fermetures à effectuer
    Alors le statut de la randonnée reste « ANNULÉE »
```

## `src/test/resources/contracts/mettre_a_jour_adhesion.feature`

```gherkin
# language: fr
Fonctionnalité: Mettre à jour le statut d'adhésion
  Un administrateur peut modifier l'admissibilité d'un membre.

  Scénario: Suspendre une adhésion
    Étant donné un administrateur authentifié et un randonneur actif
    Quand l'administrateur place son adhésion au statut « SUSPENDUE »
    Alors le nouveau statut est enregistré
    Et le randonneur ne peut plus effectuer de nouvelle inscription

  Scénario: Refuser un changement par un randonneur ordinaire
    Étant donné un randonneur authentifié sans rôle d'administrateur
    Quand il tente de modifier un statut d'adhésion
    Alors l'opération est refusée
```

## `src/test/resources/contracts/mettre_a_jour_profil.feature`

```gherkin
# language: fr
Fonctionnalité: Mettre à jour le profil d'un randonneur
  Un randonneur peut modifier ses informations personnelles.

  Scénario: Modifier ses propres coordonnées
    Étant donné un randonneur authentifié avec un profil existant
    Quand il soumet un numéro de téléphone et une adresse valides
    Alors ses informations personnelles sont mises à jour

  Scénario: Interdire la modification du profil d'autrui
    Étant donné un randonneur authentifié sans droit d'administration
    Quand il demande de modifier le profil d'un autre randonneur
    Alors l'opération est refusée
```

## `src/test/resources/contracts/parcourir_randonnees.feature`

```gherkin
# language: fr
Fonctionnalité: Parcourir les randonnées
  Les visiteurs et les membres recherchent les randonnées accessibles.

  Scénario: Consulter les randonnées classées par date
    Étant donné des randonnées ouvertes et une randonnée annulée
    Quand un visiteur parcourt les randonnées sans filtre
    Alors les randonnées ouvertes sont présentées par date croissante
    Et la randonnée annulée n'est pas présentée
    Et le nom du sentier, l'organisateur et les places disponibles sont indiqués

  Scénario: Filtrer les randonnées par sentier, date et difficulté
    Étant donné des randonnées sur plusieurs sentiers et à diverses dates
    Quand un visiteur demande le sentier « Sentier A », une date et la difficulté « FACILE »
    Alors seules les randonnées correspondant aux trois filtres sont présentées

  Scénario: Visualiser une randonnée complète pour rejoindre la liste d'attente
    Étant donné une randonnée complète dont la date limite d'inscription n'est pas atteinte
    Quand un visiteur parcourt les randonnées avec l'option « inclure les randonnées complètes »
    Alors la randonnée complète est affichée avec zéro place disponible
```

## `src/test/resources/contracts/s_inscrire_randonnee.feature`

```gherkin
# language: fr
Fonctionnalité: S'inscrire à une randonnée
  Les membres actifs admissibles prennent une place ou attendent leur tour.

  Scénario: Confirmer une inscription lorsqu'une place existe
    Étant donné un randonneur connecté dont l'adhésion est active
    Et une randonnée admissible ouverte avec une place restante
    Et la date limite d'inscription n'est pas atteinte
    Quand le randonneur s'inscrit à la randonnée
    Alors son inscription est confirmée et horodatée
    Et la randonnée devient complète
    Et un courriel de confirmation est demandé

  Scénario: Ajouter à la liste d'attente dans l'ordre d'arrivée
    Étant donné une randonnée complète avant sa date limite
    Et un randonneur connecté admissible sans inscription à cette randonnée
    Quand le randonneur demande son inscription
    Alors il rejoint la fin de la liste d'attente horodatée
    Et sa position lui est communiquée

  Scénario: Rejeter une difficulté excessive ou une adhésion inactive
    Étant donné un randonneur dont l'adhésion est suspendue
    Quand le randonneur tente de s'inscrire à une randonnée
    Alors l'inscription est refusée

  Scénario: Empêcher les inscriptions en double
    Étant donné un randonneur déjà inscrit à une randonnée
    Quand il tente de s'y inscrire une deuxième fois
    Alors aucune deuxième inscription active n'est créée
```

