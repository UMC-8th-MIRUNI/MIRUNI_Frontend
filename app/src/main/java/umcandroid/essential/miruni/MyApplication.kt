package umcandroid.essential.miruni

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "6060a4309ed695fc355f3fc8c292cc81")
    }
}