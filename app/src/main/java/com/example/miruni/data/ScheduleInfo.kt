package com.example.miruni.data

/**
 * 일정 우선 순위
 */
enum class Priority(val localLabel: String) {
    HIGH("상"),
    MEDIUM("중"),
    LOW("하");

    companion object {
        fun fromLocalLabel(label: String): Priority? =
            values().find { it.localLabel == label }
    }
}

/**
 * 일정 유형
 */
enum class Type(val localLabel: String) {
    IMMERSIVE("몰입형/사고 중심 작업"),
    CREATIVE("창작/표현 작업"),
    STUDY_ORGANIZATION("학습/정보 정리 작업"),
    PRACTICAL_ADMIN("실무/행정 처리 작업"),
    ROUTINE("반복/루틴형 작업"),
    COLLAB_COMMUNICATION("협업/의사소통 작업"),
    PREPARATION_PLANNING("준비/계획형 작업");

    companion object {
        fun fromLocalLabel(label: String): Type? =
            values().find { it.localLabel == label }
    }
}
