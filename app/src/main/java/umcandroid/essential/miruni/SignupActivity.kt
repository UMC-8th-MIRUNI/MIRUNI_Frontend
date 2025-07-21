package umcandroid.essential.miruni

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import umcandroid.essential.miruni.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}
