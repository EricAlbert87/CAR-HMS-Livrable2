package ca.uottawa.seg3102.carhms.application.services

import ca.uottawa.seg3102.carhms.domain.notifications.EmailMessage
import ca.uottawa.seg3102.carhms.domain.notifications.Forecast
import java.util.UUID

// Ports pour les services externes. Le livrable 2 utilise des doubles en tests.
fun interface EmailService { fun enqueue(message: EmailMessage) }
fun interface WeatherService { fun forecast(trailId: String): Forecast? }
fun interface CredentialService { fun register(memberId: UUID, rawPassword: String) }
