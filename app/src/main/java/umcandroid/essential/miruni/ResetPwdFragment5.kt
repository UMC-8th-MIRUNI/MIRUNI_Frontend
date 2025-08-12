package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import umcandroid.essential.miruni.databinding.FragmentResetpwd5Binding

class ResetPwdFragment5 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd5Binding.inflate(inflater, container, false)

        binding.tologinBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resetPwdFragment5_to_loginFragment)
        }

        binding.back5Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}