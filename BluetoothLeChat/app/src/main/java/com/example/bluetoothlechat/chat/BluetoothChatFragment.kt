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
package com.example.bluetoothlechat.chat

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.bluetooth.ChatServer
import com.example.bluetoothlechat.bluetooth.Message
import com.example.bluetoothlechat.databinding.FragmentBluetoothChatBinding
import com.example.bluetoothlechat.gone
import com.example.bluetoothlechat.visible
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.text.SimpleDateFormat
import java.util.*


private const val TAG = "BluetoothChatFragment"

class BluetoothChatFragment : Fragment() {
    var connected_once = false
    private var _binding: FragmentBluetoothChatBinding? = null
    // this property is valid between onCreateView and onDestroyView.
    private val binding: FragmentBluetoothChatBinding
        get() = _binding!!

    /*txt file*/
    private var output: String? = ""
    private var time: String? = null
    private var date = Date(System.currentTimeMillis())
    private var filename = ""
    private var outputStream: PrintWriter? = null

    private val deviceConnectionObserver = Observer<DeviceConnectionState> { state ->
        when(state) {
            is DeviceConnectionState.Connected -> {
                val device = state.device
                Log.d(TAG, "Gatt connection observer: have device $device")
                chatWith(device)
                connected_once = true
            }
            is DeviceConnectionState.Disconnected -> {
                // ?????? ?????? ??? ?????? ?????????
                if(connected_once){
                    //?????????
                    ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
                }
                // ??? ???????????????
                else{
                    showDisconnected()
                }

            }
        }

    }

    private val connectionRequestObserver = Observer<BluetoothDevice> { device ->
        Log.d(TAG, "Connection request observer: have device $device")
        ChatServer.setCurrentChatConnection(device)
    }

    //????????? ?????? ?????????, ?????? ???????????? ?????? ????????????.
    private val messageObserver = Observer<Message> { message ->
        Log.d(TAG, "Have message ${message.text}")
        when {
            message.text.contains("TEST") -> { // ???????????? ????????? ?????????
                Toast.makeText(context, message.text, Toast.LENGTH_SHORT).show();
            }
            message.text.contains("[START]") -> {
                val pathFormat = SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS", Locale.KOREA)
                time = pathFormat.format(date)
                filename = "$time.txt"
                System.out.println("????????????:$filename")
                outputStream = PrintWriter(FileOutputStream(getAppDataFileFromExternalStorage(filename), true))
            }
            message.text.contains("TAG") -> {
                output += ("[" + message.text + "]")
                output?.let { it1 -> saveToExternalStorage(it1) }
                output = "" // ?????????
            }
            else -> {
                try{
                    output?.let { it1 -> saveToExternalStorage(it1) } // output??? ?????? ????????? ??????
                    output = "" // output ?????????
                }catch (e: InvocationTargetException) {
                    e.getTargetException().printStackTrace(); //getTargetException
                }
            }
        }
    }

    private val inputMethodManager by lazy {
        requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBluetoothChatBinding.inflate(inflater, container, false)

        showDisconnected()

        binding.connectDevices.setOnClickListener {
            findNavController().navigate(R.id.action_find_new_device)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        requireActivity().setTitle(R.string.chat_title)
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        this.requestPermissions(permissions, 5)
        ChatServer.connectionRequest.observe(viewLifecycleOwner, connectionRequestObserver)
        ChatServer.deviceConnection.observe(viewLifecycleOwner, deviceConnectionObserver)
        ChatServer.messages.observe(viewLifecycleOwner, messageObserver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun chatWith(device: BluetoothDevice) {
        binding.connectedContainer.visible()
        binding.notConnectedContainer.gone()

        val chattingWithString = resources.getString(R.string.chatting_with_device, device.name)
        binding.connectedDeviceName.text = chattingWithString
        /*???????????? ????????? ??????.*/
        binding.test.setOnClickListener {
            val message = "TEST"
            sendMessage(message)
        }
        binding.tag1.setOnClickListener {
            val message = "TAG1"
            sendMessage(message)
        }
        binding.tag2.setOnClickListener {
            val message = "TAG2"
            sendMessage(message)
        }
        binding.tag3.setOnClickListener {
            val message = "TAG3"
            sendMessage(message)
        }
        binding.tag4.setOnClickListener {
            val message = "TAG4"
            sendMessage(message)
        }
        binding.tag5.setOnClickListener {
            val message = "TAG5"
            sendMessage(message)
        }
        binding.tag6.setOnClickListener {
            val message = "TAG6"
            sendMessage(message)
        }
        binding.tag7.setOnClickListener {
            val message = "TAG7"
            sendMessage(message)
        }
        binding.tag8.setOnClickListener {
            val message = "TAG8"
            sendMessage(message)
        }
        binding.tag9.setOnClickListener {
            val message = "TAG9"
            sendMessage(message)
        }
        binding.tag10.setOnClickListener {
            val message = "TAG10"
            sendMessage(message)
        }
        binding.tag11.setOnClickListener {
            val message = "TAG11"
            sendMessage(message)
        }
        binding.tag12.setOnClickListener {
            val message = "TAG12"
            sendMessage(message)
        }
        binding.tag13.setOnClickListener {
            val message = "TAG13"
            sendMessage(message)
        }
        binding.tag14.setOnClickListener {
            val message = "TAG14"
            sendMessage(message)
        }
        binding.tag15.setOnClickListener {
            val message = "TAG15"
            sendMessage(message)
        }
        binding.tag16.setOnClickListener {
            val message = "TAG16"
            sendMessage(message)
        }
        binding.save.setOnClickListener{
            // ?????? ????????? ?????????
            when{
                !isExternalStorageWritable() -> Toast.makeText(context,"?????? ???????????? ??????", Toast.LENGTH_LONG).show()
                else -> {
                    Toast.makeText(context,"?????????????????????",Toast.LENGTH_LONG).show()
                    outputStream?.close()
                    output = ""
                }
            }
        }
    }

    fun sendMessage(message:String){
        // only send message if it is not empty
        if (message.isNotEmpty()) {
            ChatServer.sendMessage(message)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
    //?????? ???????????? ???????????? ?????? ??????
    fun isExternalStorageWritable() : Boolean{
        when{
            //?????????????????? ????????? media-mounted??? ????????????
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> return true
            else -> return false
        }
    }

    //???????????????????????? ??? ?????????????????? ????????? ?????? ????????? ??????
    fun getAppDataFileFromExternalStorage(filename: String) : File{
        val dir = context?.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        dir?.mkdirs() // ????????????????????? ??????
        return File("${dir!!.absolutePath}${File.separator}${filename}")
    }

    //?????? ???????????? ????????? ??????
    fun saveToExternalStorage(text: String){
        outputStream?.print(text)
    }
    private fun showDisconnected() {
        hideKeyboard()
        binding.notConnectedContainer.visible()
        binding.connectedContainer.gone()
    }

    private fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}