package com.example.miruni

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

@Preview
@Composable
fun HomepageProcessBoxpreview(){
    // HomepageProcessBox 이미지 미리보기 함수
    HomepageProcessBox(progress = 80)
}
@Composable
fun HomepageProcessBox(modifier: Modifier = Modifier, progress: Int){
    Column(
        modifier = Modifier
            .width(328.dp)
            .height(177.dp)
            .background(
                color = Color(0xffffffff),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xffF3F4F6),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ){
        ProgressBox(progress)

        // 공간 간격주기
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HomapageTaskStatus("예정", "3", R.drawable.homepage_expected_iv)
            HomapageTaskStatus("미완료", "2", R.drawable.homepage_fail_iv)
            HomapageTaskStatus("완료", "1", R.drawable.homepage_complete_iv)
            HomapageTaskStatus("연기", "1", R.drawable.homepage_delay_iv)
        }
    }
}
@Composable
// 과목 진행상태 이미지, 텍스트 저장 함수
fun HomapageTaskStatus(label: String, count: String, @DrawableRes imageRes: Int){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = Color(0xff4B5563)
        )
        Text(
            text = count,
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.poppins_bold))
        )
    }
}
@Composable
// Process변수 받는 text, bar 관리 함수
fun ProgressBox(progress: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(text = "진행률",
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.dmsans_bold))
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "오늘 목표 ${progress}%를 달성했어요!",
                fontFamily = FontFamily(Font(R.font.poppins_medium)),
                fontSize = 12.sp,
                color = Color(0xff374151)
            )

            Text(
                text = "${progress}%",
                color = Color(0xff1AE019),
                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // progressBar 커스텀
        Box(
            modifier = Modifier
                .height(7.dp)
                .fillMaxWidth()
                .background(Color(0xffE5E7EB))
                .clip(RoundedCornerShape(9.dp))
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress / 100f)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xff017D916), Color(0xff09AE08))
                        )
                    )
            )
        }

    }
}

@Preview
@Composable
fun HomepageNextBox(modifier: Modifier = Modifier){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(119.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xff4DE647), Color(0xff35C134))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 20.dp, top = 10.dp)
    ){
        val (titleTxt, calendarIcon, date, backgr, play) = createRefs()
        Text(
            text = "OPIC 자격증 공부",
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.poppins_bold)),
            color = Color(0xffffffff),
            modifier = Modifier
                .constrainAs(titleTxt){

                }
        )
        Image(
            painter = painterResource(R.drawable.homepage_calendar_iv),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(calendarIcon){
                    top.linkTo(titleTxt.bottom)
                }
        )
        Text(
            text = "2025.07.12  오전 9:30",
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.dmsans_medium)),
            color = Color(0xffffffff),
            modifier = Modifier
                .constrainAs(date){
                    start.linkTo(calendarIcon.end, margin = 5.dp)
                    top.linkTo(titleTxt.bottom)
                }
        )
        Image(
            painter = painterResource(R.drawable.homepage_next_background2),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(backgr){
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                }
        )
        Image(
            painter = painterResource(R.drawable.homepage_next_play),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(play){
                    end.linkTo(parent.end, margin = 20.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}



@Preview
@Composable
fun PrevtodayTaskRV(modifier: Modifier = Modifier){

    //todayTaskRV(datas = dummyList)
    //deleteTask(datas = dummyList)
}
@Composable
fun deleteTask(datas: List<Task>, checkedTask: MutableList<Int>){
    val chunks = datas.chunked(5)
    //val checkedTask = remember { mutableStateListOf<Int>() }
    LazyRow(
        modifier = Modifier
            //.fillMaxWidth()
            //.width(300.dp)
            .height(300.dp)
            .padding(8.dp)
    ) {
        items(chunks){ chunk->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    //.width(300.dp)
                        .height(300.dp)
                ) {
                    chunk.forEach { data ->
                        TaskList(data = data,
                            true,
                            isChecked = checkedTask.contains(data.id),
                            onCheckedChange = { checked ->
                                if(checked) checkedTask.add(data.id)
                            }

                        )
                    }
                Log.d("DeleteListCheck","checkedTask확인: ${checkedTask}")
                }
            }
        }
    }
@Composable
fun todayTaskRV(datas: List<Task>){
    val chunks = datas.chunked(5)
    LazyRow(
        modifier = Modifier
            //.fillMaxWidth()
            //.width(300.dp)
            .height(300.dp)
            .padding(8.dp)
    ) {
        items(chunks){ chunk->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    //.width(300.dp)
                    .height(300.dp)
            ) {
                chunk.forEach { data ->
                    TaskList(data = data, click = false )
                }
            }
        }
    }
}
@Composable
fun TaskList(data: Task, click: Boolean, isChecked: Boolean = false, onCheckedChange: (Boolean) -> Unit = {}) {

    ConstraintLayout(
        modifier = Modifier
            //.fillMaxWidth()
            .width(340.dp)
            .height(60.dp)
            //.padding(8.dp)
            .background(
                color = Color(0xffffffff),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        val(staus, task, time, play) = createRefs()
        if(!click) {
            Image(
                painter = painterResource(
                    when (data.status) {
                        "expected" -> R.drawable.homepage_expected_staus_iv
                        "fail" -> R.drawable.homepage_fail_status_iv
                        "delay" -> R.drawable.homepage_dealy_staus_iv
                        "complete" -> R.drawable.homepage_complete_status_iv
                        else -> R.drawable.mypage_face
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(staus) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, 10.dp)
                    }
            )
        }else{
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
                    .constrainAs(staus) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, 10.dp)
                    }
            )

        }
        Text(
            text = data.content ?: "",
            fontSize = 11.5.sp,
            fontFamily = FontFamily(Font(R.font.dmsans_bold)),
            modifier = Modifier
                .constrainAs(task){
                    start.linkTo(staus.end, 10.dp)
                    top.linkTo(parent.top, 5.dp)
                }
        )
        Text(
            text = data.startTime,
            fontSize = 10.sp,
            fontFamily = FontFamily(Font(R.font.poppins_regular)),
            color = Color(0xff6B7280),
            modifier = Modifier
                .constrainAs(time){
                    top.linkTo(task.bottom)
                    start.linkTo(staus.end, 10.dp)
                }
        )
        Image(
            painter = painterResource(
                when(data.status){
                    "complete" -> R.drawable.homepage_revire_btn
                    else -> R.drawable.homepage_play
                }
            ),
            contentDescription = null,
            modifier = Modifier
                .constrainAs(play){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end, margin = 20.dp)
                }

        )
    }
}


