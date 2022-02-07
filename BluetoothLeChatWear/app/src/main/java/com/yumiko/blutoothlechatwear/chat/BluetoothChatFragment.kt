/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yumiko.blutoothlechatwear.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.yumiko.blutoothlechatwear.bluetooth.ChatServer
import com.yumiko.blutoothlechatwear.bluetooth.Message
import com.yumiko.blutoothlechatwear.databinding.FragmentBluetoothChatBinding
import com.yumiko.blutoothlechatwear.visible
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "BluetoothChatFragment"

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
class BluetoothChatFragment : Fragment(), SensorEventListener {

    /*Sensor*/
    private val manager: SensorManager by lazy {
        requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager  //센서 매니저에대한 참조를 얻기위함
    }
    private var sensor1: Sensor? = null //LIN_ACC
    private var sensor2: Sensor? = null // GY
    private var sensor3: Sensor? = null // GRAV

    private var startClicked: Boolean = false
    private var stopClicked: Boolean = true

    /*txt file*/
    private var output = ""
    private var output_gy = ""
    private var output_lin = ""
    private var output_grav = ""
    private var time: String? = null
    private var date = Date(System.currentTimeMillis())
    //private var read: Boolean = true

    /*Queue*/
    private var queue: Queue<String> = LinkedList<String>() // 큐 선언하기

    private var connected = false
    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                connected = true
                toast("connected")
                communicate()
            }
            is DeviceConnectionState.Disconnected -> {
                connected = false
                toast("Not connected")
            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    //자기가 받는 메세지도, 보낸 메세지도 여기서 처리 하는거 같다
    private val messageObserver = Observer<Message> { message ->
        Log.d(TAG, "Have message ${message.text}")
        if(message.text.contains("TAG")){
            toast(message.text) // 어떤 태그인지 토스트를 띄운다
        }
        else if(message.text.contains("TEST")){ // 테스트용 토스트 메세지
            toast(message.text)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)
        connected = false
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        sensor1 = manager.getDefaultSensor(Sensor.TYPE_GRAVITY) // 보정되지 않은 가속도계 센서
        sensor2 = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensor3 = manager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*센서데이터 수집 - txt 저장*/
    private fun communicate() {
        binding.connectedContainer.visible()
        /*START 버튼 눌렀을 때- 센서 데이터 수집 시작. 태그 메세지 받기*/
        binding.btnStart.setOnClickListener {

            if(connected){
                if(startClicked){
                    toast("이미 누름")
                }
                else{
                    startClicked = true
                    stopClicked = false

                    toast("START")

                    manager.registerListener(
                        this,
                        sensor1,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                    manager.registerListener(
                        this,
                        sensor2,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                    manager.registerListener(
                        this,
                        sensor3,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )

                }

            }
            else{
                toast("Not connected")
            }


        }
        /*STOP 버튼 눌렀을 때 - 센서 데이터 수집 끝내고 txt 파일 저장*/
        binding.btnStop.setOnClickListener {
            //val db = Firebase.firestore
            if(stopClicked){
                toast("Click START")
            }
            else{
                toast("STOP")
                manager.unregisterListener(this)
                startClicked = false
                stopClicked = true
            }

        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onSensorChanged(event: SensorEvent?) {

        val sensor = event!!.sensor
        val mFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS")
        date = Date(System.currentTimeMillis()) // 이거 꼭 여기 있어야 시간 업뎃 됨
        time = mFormat.format(date)
        when (sensor.type) {
            Sensor.TYPE_GYROSCOPE -> {
                var i = 0
                output_gy = ""
                while (i < event.values.size) {
                    // 형식: 시간;x;x값;y;y값;z;z값,
                    // 예: 2021-11-23_17:23:28_667;GRAV[0];0.82929057;GRAV[1];1.950275;GRAV[2];9.57492,
                    val message = when (i) {
                        0 -> {
                            time + ";" + "GY[" + i + "];" + event.values[i] + ";"
                        }
                        2 -> {
                            "GY[" + i + "];" + event.values[i] + ","
                        }
                        else -> {
                            "GY[" + i + "];" + event.values[i] + ";"
                        }
                    }
                    output_gy += message
                    ++i
                }
                queue.add(output_gy)
                Handler().postDelayed({
                    if(queue.peek()!=null){
                        System.out.println("큐: " + queue.peek())
                        output = queue.peek()
                        ChatServer.sendMessage(output) // 보낼게 있으면 보냄
                        // 맨 앞 데이터 삭제하기
                        queue.poll()
                    }
                }, 200)

            }
            Sensor.TYPE_GRAVITY -> {
                var i = 0
                output_grav = ""
                while (i < event.values.size) {
                    val message = when (i) {
                        0 -> {
                            time + ";" + "GV[" + i + "];" + event.values[i] + ";"
                        }
                        2 -> {
                            "GV[" + i + "];" + event.values[i] + ","
                        }
                        else -> {
                            "GV[" + i + "];" + event.values[i] + ";"
                        }
                    }
                    output_grav += message
                    ++i
                }
                queue.add(output_grav)
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                var i = 0
                output_lin = ""
                while (i < event.values.size) {
                    val message = when (i) {
                        0 -> {
                            time + ";" + "LA[" + i + "];" + event.values[i] + ";"
                        }
                        2 -> {
                            "LA[" + i + "];" + event.values[i] + ","
                        }
                        else -> {
                            "LA[" + i + "];" + event.values[i] + ";"
                        }
                    }
                    output_lin += message
                    ++i
                }
                queue.add(output_lin)
            }
        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    /*토스트 메소드
    * 보통 뜨는 것보다 아래쪽에 뜨게 했다. */
    private fun toast(text: String){
        try {
            val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM, 0, 15)
            toast.show()
        }
        catch (e: WindowManager.BadTokenException) {
            //use a log message
            Log.d("에러", "토스트 에러")
        }
    }
}