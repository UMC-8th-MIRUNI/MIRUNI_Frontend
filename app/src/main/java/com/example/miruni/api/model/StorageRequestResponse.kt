package com.example.miruni.api.model

/* 보관함 정보 조회 */
data class StorageRequest(
    val year: Int,
    val month: Int
)
data class StorageResponse(
    val peanutCount: Int,   // 사용자 땅콩 수
    val completionRatePercent: Int, // !!이번달 일정 달성률
    val isOpenedThisMonth: Boolean, // true면 '오픈하기' 대신 '이번달 리포' 버튼 보여주도록
    val canOpenThisMonth: Boolean,  // !! 이번달 리포트 오픈 조건(땅콩 ≥ 30 && 완료율 ≥ 80%) 충족 여부
    val isOpenedLastMonth: Boolean, // !! 저번달 리포트 오픈 여부
    val lockState: String,  //"잠김" or "열림"
    val isOpenButtonVisible: Boolean // !! false면 '저번달 리포트 보기' 만 보여주기 / true면 '오픈하기' 버튼

    /*
    * 그래서 isOpenButtonVisible이게 오픈조건 만족했을때 true인데 한번 누르면 false
    * 가 되고 isOpenedThisMonth가 true가 되는데 그거 구분하려고 한거긴해..!!
    * */

    /*
    * isOpenedLastMonth : true & isOpenButtonVisible : false -> 저번달 리포트 보기
    isOpenedLastMonth : true & isOpenButtonVisible : true -> 저번달 리포트 , 오픈하기
    isOpenedLastMonth : true & isOpenedThisMonth : true -> 저번달 리포트 , 이번달 리포트
* */
)