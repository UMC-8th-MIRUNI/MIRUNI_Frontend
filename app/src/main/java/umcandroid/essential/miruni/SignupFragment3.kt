package umcandroid.essential.miruni

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import umcandroid.essential.miruni.databinding.FragmentSignup3Binding

class SignupFragment3 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private lateinit var binding: FragmentSignup3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup3Binding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCompleteButton.setOnClickListener {
//            viewModel.signup()
            //예비
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // LiveData 관찰: 성공 여부에 따라 로그인 화면 이동
        viewModel.signupResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess == true) {
                Toast.makeText(requireContext(), "회원가입 완료!", Toast.LENGTH_SHORT).show()

                // LoginActivity로 이동
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // SignupActivity 종료
            } else {
                Toast.makeText(requireContext(), "회원가입 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}