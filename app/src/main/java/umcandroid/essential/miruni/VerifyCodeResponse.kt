package umcandroid.essential.miruni

data class VerifyCodeResponse(
    val errorCode: String?,
    val message: String,
    val result: VerifyCodeResult?
)

data class VerifyCodeResult(
    val resetToken: String
)