package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import umcandroid.essential.miruni.databinding.FragmentResetpwd2Binding


class ResetPwdFragment2 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd2Binding.inflate(inflater, container, false)
        val email = arguments?.getString("email")

        binding.sendBtn.setOnClickListener {
            val bundle = Bundle().apply { putString("email", email) }
            val resetPwdFragment3 = ResetPwdFragment3()
            resetPwdFragment3.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, resetPwdFragment3) // Activity에 있는 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()
        }

        binding.back2Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}