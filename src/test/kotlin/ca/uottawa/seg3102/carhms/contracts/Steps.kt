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
