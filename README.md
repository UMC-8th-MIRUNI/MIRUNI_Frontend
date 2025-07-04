# MIRUNI Frontend

# Git 관리

### branch 이름 설정
맡은 사람 이름으로 branch 이름 설정
<ul>
  <li>이창환 -> lee</li>
  <li>문설빈 -> moon</li>
  <li>사혜빈 -> sa</li>
</ul>

### Issue 설정
주요 Issue는 다음과 같음
<ol>
  <li>Login-Signup</li>
  <li>CalendarView</li>
</ol>

### commit 컨벤션
<ul>
  <li>Feat – 새로운 기능 구현</li>
  <li>Fix – 버그 수정</li>
  <li>Mod – 코드 수정</li>
  <li>Ui - UI 수정</li>
  <li>Del – 필요 없는 파일 삭제</li>
  <li>Merge – 다른 브랜치와 merge</li>
  <li>Move – 파일이나 코드의 이동</li>
  <li>Rename – 이름 수정</li>
  <li>Docs – README와 같은 문서 수정</li>
</ul>

# AndroidStudio 환경 설정

### AndroidStudio 버전

### SDK 버전
<ul>
  <li>targetSDK = 35</li>
  <li>minSDK = 26</li>
</ul>

### libs.versions.toml
```kotlin
[versions]
agp = "8.8.0"
kotlin = "2.0.0"
coreKtx = "1.16.0"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
lifecycleRuntimeKtx = "2.9.1"
activityCompose = "1.10.1"
composeBom = "2024.04.01"
appcompat = "1.7.1"
constraintlayout = "2.2.1"
recyclerview = "1.4.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
androidx-recyclerview = { group = "androidx.recyclerview", name = "recyclerview", version.ref = "recyclerview" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### 사용 라이브러리 및 기술 스택
<ul>
  <li>material-calendarview:2.0.1</li>
  <li>kotlinx-datetime:0.7.0</li>
  <li>RecyclerView</li>
</ul>

# UI 분석 및 역할 분배

### 화면 분배

<ul>
  <li>Calendar Page -> 창환</li>
  <li>시작부(Login, Signup) -> 혜빈</li>
  <li>Home Page, My Page -> 설빈</li>
</ul>

### Color
<ul>
  <li>PrimaryColor = </li>
  <li>SecondaryColor = </li>
</ul>

### Font
<ol>
  <li>ClimateCrisis</li>
  <li>Inter_18pt</li>
  <li>Lato</li>
  <li>manrope</li>
  <li>NotoSansSymbols</li>
  <li>Poppins</li>
  <li>RaleWay</li>
</ol>



