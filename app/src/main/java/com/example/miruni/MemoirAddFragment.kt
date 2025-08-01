package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.FragmentMemoirAddBinding
import kotlinx.coroutines.launch

class MemoirAddFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirAddBinding.inflate(layoutInflater)
    }
    private lateinit var taskDB : TaskDatabase
    private lateinit var taskDatas : List<Task>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            taskDB = TaskDatabase.getInstance(requireContext())
            taskDatas = taskDB.taskDao().getTask()
        }

        val dapter = MemoirAddRVAdapter(taskDatas) { clickedTask ->
            val bundle = Bundle().apply {
                putString("content", clickedTask.content)
                putString("date", clickedTask.startTime)
            }
            val transaction = parentFragmentManager.beginTransaction()
            val fragment = MemoirCompleteFragment()
            fragment.arguments = bundle

            transaction.replace(R.id.process_frm, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.memeoirAddRv.adapter = dapter
        binding.memeoirAddRv.layoutManager = LinearLayoutManager(requireContext())


        binding.memoirAddBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.process_frm, MemoirWriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}