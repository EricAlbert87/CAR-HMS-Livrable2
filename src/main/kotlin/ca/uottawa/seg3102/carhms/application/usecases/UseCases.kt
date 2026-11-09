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
