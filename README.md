# MiBand SDK [ ![Download](https://api.bintray.com/packages/iyeonghun/maven/miband-sdk/images/download.svg) ](https://bintray.com/iyeonghun/maven/miband-sdk/_latestVersion)

이전에 개발되어있던 [panglian/miband-sdk-android](https://github.com/pangliang/miband-sdk-android) 라이브러리가 더이상 유지보수가 안되고 연결상의 문제가 있어 이전 라이브러리 구조 기반으로 다시 만든 Mi Band 라이브러리 입니다.

##### To - do

- 현재 MiBand 2 버튼과 손목 들어올려 확인 하는 기능, 심박수 측정 기능을 추가하기 위해 작업 중 입니다.<br>

## Description

![예제 어플리케이션](./App_Example_Image.png=300px)

Mi Band 1S를 사용하기 위한 Android 라이브러리 입니다.<br>현재 아래와 같은 기능이 구현되어 있으며 추후 다른 기능들을 추가 할 예정 입니다.

- 심박수 측정(1번, 약 10번 연속)
- Led, 진동 알람
- 현재 걸음수 측정
- 실시간 걸음수 측정
- 배터리 잔량 확인

## Adding to project

app의 `build.gradle`파일에 다음과 같이 추가 합니다.

```
compile 'com.jellygom:miband-sdk:0.1.0'
```

## How to use



## Release Notes

#### v0.2 - 2016.09.19

- 현재 배터리 잔량을 가져 올 수 있게 되었다.
- 오늘 현재 걸음수 및 실시간으로 걸음수를 가져 올 수 있게 되었다.
- 예제 앱 변경

#### v0.1 - 2016.09.04

- Mi Band 1S 연결 API 재작성
- Mi Band를 흔들지 않아도 심박 측정가능
  - [이전 라이브러리](https://github.com/pangliang/miband-sdk-android)에서 심박 측정 전 setUserInfo를 하게되면 Mi Band를 실제 연결 하는것처럼 Mi Band를 흔들어야 했었다.
- Mi Band에 기록된 UserInfo를 가져올 수 있다
- 모든 API에 CallBack을 통해 각 API가 종료 되었는지 확인 가능

## License

```
Copyright 2016 Lee,YeoungHun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

