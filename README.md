# 스마트폰 기반 위치 인지 시스템

![image](https://github.com/TongtongG17/CNU-PDR/assets/145681939/aa8f8aef-42bf-4147-9fab-e3a2cc4e037c)

## 사용된 기술
<img src="https://img.shields.io/badge/androidstudio-3DDC84?style=flat-square&logo=androidStudio&logoColor=white"/> <img src="https://img.shields.io/badge/java-04738F?style=flat-square&logo=java&logoColor=white"/>

## 프로젝트 소개
스마트폰에 내장된 자이로, 가속도 센서 등을 이용하여 실내 위치를 측정하는 PDR(Pedestrian Dead Reckoning) 기법이 있습니다. 스마트폰에 내장된 센서를 이용하는 기법은 보행자의 보폭과 보 검출을 통해 직선 이동거리를 계산하고, 진행 방향을 검출하여 이동 궤적을 생성함으로써 출발지점으로부터 상대위치를 산출합니다. 이러한 기법은 위치 측정에 필요한 데이터베이스의 양이 적고, 외부 무선 신호 자원을 사용하지 않기 때문에 외부 무선 신호망이 없는 장소에서도 사용가능하며, 실내 환경에 변화에 거의 영향을 받지 않습니다. 하지만 기존에 제안된 방식들은 누적되는 센서 값의 오차로 인해 장거리를 이동하게 되면 위치 측정의 신뢰도가 떨어지기 때문에, 누적되는 위치 측정 오차를 보정하기 위해 BLE나 Wi-Fi AP와 같은 외부 자원이 도움이 필요합니다. 따라서 자이로, 가속도 센서를 합쳐서 하나의 어플로 만들고, 실험을 진행했습니다.

## 역할
이고운: PDR 추측 기법을 위한 Heading  상보필터 제작 및 PDR 수식 적용.<br>
김승영: 가속도 센서를 이용한 스텝카운터 제작 및 SVM 수식 적용

## 논문링크

https://drive.google.com/file/d/1tG1xCsC1nGkM5MtFjTPdaGoKWpLhY95_/view?usp=drive_link
