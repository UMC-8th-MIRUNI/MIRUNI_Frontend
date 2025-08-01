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

    private val viewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignup4Binding.inflate(inflater, container, false)

        binding.ivCompleteButton.setOnClickListener {
            val selected = mutableListOf<String>()
            // 체크박스 체크 확인 (예시)
            if (binding.checkboxOption1.isChecked) selected.add("FTYPE")
            if (binding.checkboxOption2.isChecked) selected.add("GTYPE")
            if (binding.checkboxOption3.isChecked) selected.add("HTYPE")
            if (binding.checkboxOption4.isChecked) selected.add("ITYPE")
            if (binding.checkboxOption5.isChecked) selected.add("JTYPE")

            viewModel.addPreferences(selected)

            findNavController().navigate(R.id.action_signupFragment4_to_signupFragment5)
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}