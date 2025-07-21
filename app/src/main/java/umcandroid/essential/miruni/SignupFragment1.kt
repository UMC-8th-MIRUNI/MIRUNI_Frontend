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
import umcandroid.essential.miruni.databinding.FragmentSignup1Binding

class SignupFragment1 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private var isAgree = false // 체크 상태 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SignupFragment1", "onCreateView 호출됨")
        val binding = FragmentSignup1Binding.inflate(inflater, container, false)

        // 체크박스 클릭 이벤트
        binding.ivCheckbox.setOnClickListener {
            isAgree = !isAgree
            if (isAgree) {
                binding.ivCheckbox.setImageResource(R.drawable.checkbox) // 체크된 이미지
            } else {
                binding.ivCheckbox.setImageResource(R.drawable.img) // 체크 안된 이미지
            }
        }

        binding.ivButton.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val password_check = binding.etPasswordCheck.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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

//            if (!isAgree) {
//                Toast.makeText(requireContext(), "약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // 이동
            findNavController().navigate(R.id.action_signupFragment1_to_signupFragment2)

            viewModel.name = name
            viewModel.email = email
            viewModel.password = password
            viewModel.password_check = password_check
            viewModel.isAgree = isAgree  // ViewModel에 동의 여부 전달



        }

        binding.ivSignupBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return binding.root
    }

}
