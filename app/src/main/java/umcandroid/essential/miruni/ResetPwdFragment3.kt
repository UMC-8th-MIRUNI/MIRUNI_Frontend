package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import umcandroid.essential.miruni.databinding.FragmentResetpwd3Binding


class ResetPwdFragment3 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd3Binding.inflate(inflater, container, false)

        binding.send2Btn.setOnClickListener {
            findNavController().navigate(R.id.action_resetPwdFragment3_to_resetPwdFragment4)
        }

        binding.back3Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return binding.root
    }
}