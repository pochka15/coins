package pw.coins.usos.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class CourseName(
    val pl: String,
    val en: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Participant(
    val id: String,
    val firstName: String,
    val lastName: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class UserGroup(
    val courseUnitId: String,
    val lecturers: List<Participant>,
    val participants: List<Participant>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ApiCourseEdition(
    val courseId: String,
    val courseName: CourseName,
    val userGroups: List<UserGroup>
)
