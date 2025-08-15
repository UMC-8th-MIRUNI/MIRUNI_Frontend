package com.example.miruni.ui.homepage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruni.api.model.HomepageResponse
import com.example.miruni.api.model.Tasks
import com.example.miruni.data.repository.HomepageRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class HomepageViewModel(private val repository: HomepageRepository): ViewModel() {

    private val HomepageDatas = MutableLiveData<HomepageResponse>()
    val homepagedatas: LiveData<HomepageResponse> = HomepageDatas

    private lateinit var homepage: Response<HomepageResponse>
    fun loadHomepage(token: String){
        /* 홈페이지 전체 정보 조회 API */
        viewModelScope.launch {
            try {
                homepage = repository.getHomepage(token)
                HomepageDatas.value = homepage.body()
                if(homepage.isSuccessful){
                    Log.d("홈페이지 전체 정보 조회", "성공: ${homepage.body()}")
                }else{
                    Log.e("홈페이지 전체 정보 조회", "에러: ${homepage.code()} - ${homepage.message()}")
                }
            }catch (e: Exception){
                Log.e("홈페이지 전체 정보 조회", "연결 에러: ${e.message}")
            }
        }
    }
    fun getAllTask(tasks: Tasks){
    }

}