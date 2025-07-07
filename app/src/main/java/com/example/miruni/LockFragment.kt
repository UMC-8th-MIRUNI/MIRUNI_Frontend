package com.example.miruni
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentLockBinding

class LockFragment : Fragment() {

    private lateinit var binding : FragmentLockBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockBinding.inflate(layoutInflater, container, false)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        /** No */
        binding.lockNoTv.setOnClickListener {

        }
//        /** Yes */
//        binding.lockYesTv.setOnClickListener {
//            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.process_frm, GrowFragment())
//                .commitAllowingStateLoss()
//        }
    }
}