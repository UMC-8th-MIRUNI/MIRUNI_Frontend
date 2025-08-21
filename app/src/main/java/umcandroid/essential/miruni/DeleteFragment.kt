package umcandroid.essential.miruni

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import umcandroid.essential.miruni.databinding.FragmentDeleteBinding

class DeleteFragment : Fragment() {

    private var _binding: FragmentDeleteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LogoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        binding.btnDelete.setOnClickListener {
            val accessToken = prefs.getString("access_token", null)
            if (accessToken.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "로그인 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 회원 탈퇴 API 호출
                viewModel.deleteAccount(accessToken)
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                // 탈퇴 성공
                Toast.makeText(requireContext(), "회원 탈퇴 완료!", Toast.LENGTH_SHORT).show()
                prefs.edit().clear().apply() // 토큰 및 모든 정보 삭제

                // 로그인 화면으로 이동
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment())
                    .commit()
            } else {
                Toast.makeText(requireContext(), "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}