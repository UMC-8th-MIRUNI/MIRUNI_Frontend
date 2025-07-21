package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.miruni.R
import com.example.miruni.databinding.FragmentSignup2Binding

class SignupFragment2 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignup2Binding.inflate(inflater, container, false)
        binding.ivNicknameButton.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            viewModel.nickname = nickname

            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            findNavController().navigate(R.id.action_signupFragment2_to_signupFragment3)

        }

        binding.ivNicknameBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }


}