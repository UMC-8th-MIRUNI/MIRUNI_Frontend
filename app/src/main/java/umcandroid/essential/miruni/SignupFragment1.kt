package umcandroid.essential.miruni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
//import com.example.miruni.R
//import com.example.miruni.databinding.FragmentSignup1Binding
import umcandroid.essential.miruni.databinding.FragmentSignup1Binding

//import umcandroid.essential.miruni.databinding.FragmentSignup1Binding

class SignupFragment1 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SignupFragment1", "onCreateView 호출됨")
        val binding = FragmentSignup1Binding.inflate(inflater, container, false)

        val emailFromGoogle = arguments?.getString("email") ?: ""
        if (emailFromGoogle.isNotEmpty()) {
            viewModel.email.value = emailFromGoogle
            binding.etEmail.setText(emailFromGoogle)
            binding.etEmail.isEnabled = false
        }


        binding.ivButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val password_check = binding.etPasswordCheck.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password_check.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != password_check) {
                Toast.makeText(requireContext(), "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이동
            val signupFragment11 = SignupFragment11()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, signupFragment11) // Activity의 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()


            viewModel.email.value = email
            viewModel.password.value = password
        }

        binding.ivSignupBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return binding.root
    }

}
