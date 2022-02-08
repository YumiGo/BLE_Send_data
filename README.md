# BLE_Send_data
 저전력 블루투스를 이용하여 워치의 센서데이터(중력, 자이로, 선형가속도)를 실시간으로 스마트폰에 전송하는 어플리케이션
 
// 태그 형식
<img src="https://user-images.githubusercontent.com/72744190/152900083-30c372e9-0b20-4219-9ce1-342bb1e625fc.png"  width="700" height="370">
<img src="https://user-images.githubusercontent.com/72744190/152900070-28243c3d-5e9f-4d1d-9e0f-e9a0965cdc03.jpg"  width="700" height="370">

# 사용방법
- 워치에는 BluetoothLeChat을, 워치에는 BluetoothLeChatWear를 설치한다.
- [ 스마트폰 ]
- 블루투스가 꺼져 았는 경우 'ENABLE BLUWTOOTH' 버튼을 눌러 블루투스를 활성화 한다.
- 'CONNECT WITH NEARBY DEVICES' 버튼을 눌러 BLE 기기를 스캔한다.
- [ 워치 ]
- 앱을 실행시키면 워치가 advertising을 한다.
- [ 스마트폰 ]
- 기기목록에서 해당 워치 이름이 뜨면 클릭하여 연결한다.
- [ 워치 ]
- 연결 되면 'connected'라는 토스트 메세지가 뜬다
- [ 스마트폰 ]
- '테스트 버튼'을 누르면 워치에서 'TEST'라는 토스트 메세지가 뜬다. 
- 토스트가 안뜬다면 연결이 불안정한 것이니 앞의 과정을 반복해 재연결해야 한다. 
- [ 워치 ]
- 'START' 버튼을 누르면 데이터 전송이 시작 된다. 
- [ 스마트폰 ]
- '구역1'~'구역16' 버튼을 이용하면 태깅을 할 수 있다. 
- [ 워치 ]
- 'STOP' 버튼을 누르면 데이터 수집이 종료 된다.
- [ 스마트폰 ]
- '저장' 버튼을 누르면 데이터가 스마트폰에 txt 파일로 저장 된다. 
- 위치: Phone > Android > data > com.example.bluetoothlechat > files > Documents
- START 버튼을 눌렀던 시간을 제목으로 하여 txt 파일이 저장 된다.
