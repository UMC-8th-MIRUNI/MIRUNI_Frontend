package com.example.miruni

import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.FragmentMemoirWriteBinding

class MemoirWriteFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirWriteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.emoirWriteOk.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.process_frm, MemoirAddFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }


}