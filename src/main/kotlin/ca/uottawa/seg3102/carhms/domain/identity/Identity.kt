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
