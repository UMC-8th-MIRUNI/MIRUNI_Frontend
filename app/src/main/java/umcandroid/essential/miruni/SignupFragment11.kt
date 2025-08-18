package umcandroid.essential.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import umcandroid.essential.miruni.databinding.FragmentSignup11Binding

class SignupFragment11 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private var isAgree = false // 체크 상태 저장

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SignupFragment1", "onCreateView 호출됨")
        val binding = FragmentSignup11Binding.inflate(inflater, container, false)


        binding.ivButton.setOnClickListener {
            val name = binding.etName.text.toString()
            val birthday = binding.etBirthday.text.toString()
            val phoneNumber = binding.etPhone.text.toString()


            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (birthday.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


//            if (!isAgree) {
//                Toast.makeText(requireContext(), "약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            // 이동
            findNavController().navigate(R.id.action_signupFragment11_to_signupFragment2)

            viewModel.name.value = name
            viewModel.birthday.value = birthday
            viewModel.phoneNumber.value = phoneNumber
        }

        binding.ivSignupBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        return binding.root
    }

}
