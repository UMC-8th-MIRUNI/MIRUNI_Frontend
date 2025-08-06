package umcandroid.essential.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import umcandroid.essential.miruni.databinding.FragmentFindpwdmailBinding


class FindPwdMailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFindpwdmailBinding.inflate(inflater, container, false)

        return binding.root
    }
}