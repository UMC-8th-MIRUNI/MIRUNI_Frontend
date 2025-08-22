package com.example.miruni.ui.homepage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruni.api.GetSplitScheduleResponse
import com.example.miruni.api.ResultOfGetSplitSchedule
import com.example.miruni.api.model.DeleteTaskRequest
import com.example.miruni.api.model.HiddenResponse
import com.example.miruni.api.model.HomepageResponse
import com.example.miruni.api.model.NextTask
import com.example.miruni.api.model.TaskItem
import com.example.miruni.api.model.Tasks
import com.example.miruni.data.repository.HomepageRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class HomepageViewModel(private val repository: HomepageRepository): ViewModel() {

    private val HomepageDatas = MutableLiveData<uiData>()
    val homepagedatas: LiveData<uiData> = HomepageDatas
    /* 홈페이지 전체 정보 조회 */
    fun loadHomepage(token: String){
        viewModelScope.launch {
            try {
                var homepage = repository.getHomepage(token)
                /* 연결 확인 */
                if(homepage.isSuccessful){
                    Log.d("홈페이지 전체 정보 조회", "성공: ${homepage.body()}")

                    val result = homepage?.body()?.result
                    result?.let { it ->
                        val paused = it.tasks?.paused ?: emptyList()
                        val finished = it.tasks?.finished ?: emptyList()
                        val notStarted = it.tasks?.notStarted ?: emptyList()

                        val nextTask = it.nextTask ?: emptyList()

                        val allTask = paused + finished + notStarted

                        HomepageDatas.value = uiData(
                            username = it.name ?: "",
                            totalCount = it.totalCount ?: 0,
                            scheduledCount = it.scheduledCount ?: 0,
                            pausedCount = it.pausedCount ?: 0,
                            completedCount = it.completedCount ?: 0,
                            achievementRate = it.achievementRate ?: 0,

                            paused = paused,
                            finished = finished,
                            notStarted = notStarted,

                            nextTask = nextTask,

                            allTask = allTask
                        )
                    }


                }else{  Log.e("홈페이지 전체 정보 조회", "에러: ${homepage.code()} - ${homepage.message()}") }


            }catch (e: Exception){
                Log.e("홈페이지 전체 정보 조회", "연결 에러", e)
                Log.e("홈페이지 전체 정보 조회", "연결 에러: ${e.message}")
            }
        }
    }

    /* 일정 삭젠데 곧 미루기로 변경 */
    fun deleteTask(token: String, request: DeleteTaskRequest){
        viewModelScope.launch {
            try {
                val response = repository.deleteTask(token, request)

                if(response.isSuccessful){
                    Log.d("일정 삭제", "성공!: ${response.body()}")
                }else { Log.e("일정 삭제", "응답 에러: ${response.code()}")}
            }catch (e: Exception){
                Log.e("일정 삭제", "에러: ${e.message}")
            }
        }
    }

    /* 일정 전체 조회 */
    private val ScheduleData = MutableLiveData<ResultOfGetSplitSchedule>()
    val scheduleData: LiveData<ResultOfGetSplitSchedule> = ScheduleData

    fun getSchedule(token: String, planId: Int){
        viewModelScope.launch {
            try {
                val response = repository.getSchedule(token, planId)
                if(response.isSuccessful){
                    ScheduleData.value = response.body()?.result
                    Log.e("일정 전체 조회", "조회 성공: ${response.raw()}")
                }else { Log.e("일정 삭제", "응답 에러: ${response.code()}")}
            }catch (e: Exception) { Log.e("일정 전체 조회", "연결 에러: ${e.message}")}
        }
    }

    /* 일정 숨기기 */
    private val HiddenData = MutableLiveData<HiddenResponse>()
    val hiddenData: LiveData<HiddenResponse> = HiddenData

    fun getHidden(token: String, planId: Int){
        viewModelScope.launch {
            try {
                val response = repository.getHidden(token, planId)
                if(response.isSuccessful){
                    //HiddenData.value = response.body()?.result
                    Log.e("일정 전체 조회", "조회 성공: ${response.raw()}")
                }else { Log.e("일정 삭제", "응답 에러: ${response.code()}")}
            }catch (e: Exception) { Log.e("일정 전체 조회", "연결 에러: ${e.message}")}
        }
    }

    data class uiData(
        val username: String,
        val totalCount: Int = 0,
        val scheduledCount: Int = 0,
        val pausedCount: Int = 0,
        val completedCount: Int = 0,
        val achievementRate : Int = 0,

        val paused: List<TaskItem>,
        val finished: List<TaskItem>,
        val notStarted: List<TaskItem>,

        val nextTask: List<NextTask>,

        val allTask: List<TaskItem>,
    )


}