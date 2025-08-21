package umcandroid.essential.miruni

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
//import com.example.miruni.databinding.ActivitySignupBinding
import umcandroid.essential.miruni.databinding.ActivitySignupBinding

//import umcandroid.essential.miruni.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 Fragment 추가
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SignupFragment1())
                .commit()
        }
    }

    // 카카오 로그인 후 회원가입 페이지로 이동
    fun goToKakaoSignupFragment() {
        val fragment = SignupFragment2()
        fragment.arguments = Bundle().apply {
            putBoolean("isKakao", true) // 카카오 로그인 여부 전달
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
