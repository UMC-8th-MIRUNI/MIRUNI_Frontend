package umcandroid.essential.miruni

data class KakaoSignupRequest(
    val name: String,
    val birthday: String,
    val phoneNumber: String,
    val agreedPrivacyPolicy: Boolean,
    val nickname: String
)
