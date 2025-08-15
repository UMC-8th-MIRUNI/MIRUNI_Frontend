package umcandroid.essential.miruni

data class SurveyRequest(
    val situations: List<String>,
    val level: String,
    val reasons: List<String>
)
