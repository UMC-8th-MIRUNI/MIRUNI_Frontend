package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.FragmentMemoirCompleteBinding

class MemoirCompleteFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirCompleteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val content = arguments?.getString("content")
        val date = arguments?.getString("date")

        binding.memoirWriteTitle.text = content
        binding.memoirWirteTime.text = date

        binding.memoirWriteMenu.setOnClickListener {
            showMemu(it)
        }
    }
    private fun showMemu(view: View){
        val menu = PopupMenu(requireContext(), view)
        menu.menuInflater.inflate(R.menu.memoir_write_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.write_modify->{
                    true
                }
                R.id.write_delete -> {
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
}