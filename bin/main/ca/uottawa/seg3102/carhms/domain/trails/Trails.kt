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
