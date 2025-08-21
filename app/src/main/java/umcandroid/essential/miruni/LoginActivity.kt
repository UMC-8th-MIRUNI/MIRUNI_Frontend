package umcandroid.essential.miruni

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import umcandroid.essential.miruni.databinding.ActivityLoginBinding

//import com.example.miruni.databinding.ActivityLoginBinding

//import umcandroid.essential.miruni.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 화면 회전 등으로 이미 Fragment가 있는 경우 중복 추가 방지
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }

}