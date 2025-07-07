package com.example.miruni
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private lateinit var binding : FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater, container, false)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        binding.startNextTv.setOnClickListener {
            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, LockFragment())
                .commitAllowingStateLoss()
        }
    }
}