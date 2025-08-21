package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
//import com.example.miruni.R
//import com.example.miruni.databinding.FragmentSignup4Binding
import umcandroid.essential.miruni.databinding.FragmentSignup4Binding

class SignupFragment4 : Fragment() {

    private val viewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignup4Binding.inflate(inflater, container, false)

        binding.ivCompleteButton.setOnClickListener {
            val level = when {
                binding.checkboxOption1.isChecked -> "NORMAL"
                binding.checkboxOption2.isChecked -> "RARELY"
                binding.checkboxOption3.isChecked -> "NORMAL"
                binding.checkboxOption4.isChecked -> "OFTEN"
                binding.checkboxOption5.isChecked -> "ALWAYS"
                else -> ""
            }
        viewModel.level.value = level

            val signupFragment5 = SignupFragment5()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, signupFragment5) // Activity의 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}