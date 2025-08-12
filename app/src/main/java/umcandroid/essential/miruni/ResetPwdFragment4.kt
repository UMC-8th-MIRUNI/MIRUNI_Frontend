package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import umcandroid.essential.miruni.databinding.FragmentResetpwd4Binding

class ResetPwdFragment4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd4Binding.inflate(inflater, container, false)

        binding.resetBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resetPwdFragment4_to_resetPwdFragment5)
        }

        binding.back4Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}