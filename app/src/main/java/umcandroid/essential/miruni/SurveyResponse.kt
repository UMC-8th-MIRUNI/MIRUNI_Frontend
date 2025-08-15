package umcandroid.essential.miruni

data class SurveyResponse(
    val errorCode: String?,
    val message: String,
    val result: SurveyResult?
)

data class SurveyResult(
    val message: String,
    val completedAt: String,
    val status: String
)
