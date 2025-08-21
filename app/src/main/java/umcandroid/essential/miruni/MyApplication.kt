package umcandroid.essential.miruni

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        KakaoSdk.init(this, "6060a4309ed695fc355f3fc8c292cc81")
    }
}