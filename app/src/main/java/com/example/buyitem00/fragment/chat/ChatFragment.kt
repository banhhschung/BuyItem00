package com.example.buyitem00.fragment.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.adapter.MessageAdapter
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.cloudmessagingservice.RetrofitInstance
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentChatBinding
import com.example.buyitem00.model.Message
import com.example.buyitem00.model.notification.NotificationData
import com.example.buyitem00.model.notification.PushNotification
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val args: ChatFragmentArgs by navArgs()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private var arrMessage = arrayListOf<Message>()
    private lateinit var dbRef: DatabaseReference
    private val messageAdapter = MessageAdapter()
    var receiveRoom: String? = ""
    var sendRoom: String? = ""
    private var sendUid = ""
    private var temp = 20
    private var loadingCount = 0
    private var supporterToken = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)

        supporterToken = args.token
        val receiveUid = args.idSupporter
        Log.d("doanpt", "${supporterToken}")
        sendUid = args.idUser
        binding.tvName.text = args.nameSupporter



        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            FirebaseService.sharedRref =
                activity?.getSharedPreferences("sharePref", Context.MODE_PRIVATE)
            if (!it.isSuccessful) {
                Log.d("doanpt", it.exception.toString())
                return@addOnCompleteListener
            }
            FirebaseService.token = it.result
            Log.d("khanhto", "this is my token ${it.result}")
        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic")

        lifecycleScope.launch(Dispatchers.IO) {
            val supporter = imageViewModel.getObjectData(args.idSupporter)
            withContext(Dispatchers.Main) {
                binding.imageAvatar.setImageBitmap(supporter.bitmap)
            }
        }

        sendRoom = receiveUid + sendUid
        receiveRoom = sendUid + receiveUid


        val layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvChatFragment.layoutManager = layoutManager
        binding.rvChatFragment.adapter = messageAdapter
//        binding.rvChatFragment.hasFixedSize()

        getCountMessage()
        getDataFormFirebase(20)


        binding.btnSend.setOnClickListener {
            val message = binding.edtMessage.text.toString()
            if (message != "") {
                sendingMessage(message)
                binding.edtMessage.text.clear()
                PushNotification(NotificationData("Dang khanh", message), supporterToken).also {
                    Log.d("khanhto", "supporter token $supporterToken")
                    sendNotification(it)
                }
            }

        }

        binding.rvChatFragment.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (temp <= loadingCount) {
                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        temp += 15
                        loadMoreFirebase(temp)
                        return
                    }
                }
            }
        })


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action = ChatFragmentDirections.actionChatFragmentToListSupporterFragment()
                findNavController().navigate(action)

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)


        return binding.root
    }

    private fun getCountMessage() {
        FirebaseDatabase.getInstance().reference.child("chatroom").child(sendRoom!!)
            .child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    loadingCount = snapshot.childrenCount.toInt()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendingMessage(message: String) {
        val c = Calendar.getInstance()
        val dateTime = "${c.get(Calendar.HOUR_OF_DAY)}:${c.get(Calendar.MINUTE)}"
        val messageObject = Message(message, sendUid, dateTime)
        dbRef.child("chatroom").child(sendRoom!!).child("message").push()
            .setValue(messageObject).addOnSuccessListener {
                dbRef.child("chatroom").child(receiveRoom!!).child("message").push()
                    .setValue(messageObject)
            }
    }


    private fun getDataFormFirebase(temp: Int) {
        dbRef = FirebaseDatabase.getInstance().getReference()
        dbRef.child("chatroom").child(sendRoom!!).child("message").limitToLast(temp)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrMessage.clear()
                    for (i in snapshot.children) {
                        val message = i.getValue(Message::class.java)
                        if (message != null) {
                            arrMessage.add(message)
                        }
                    }

                    messageAdapter.reloadData(arrMessage)
                    binding.rvChatFragment.scrollToPosition(arrMessage.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun loadMoreFirebase(number: Int) {
        FirebaseDatabase.getInstance().reference.child("chatroom").child(sendRoom!!)
            .child("message").limitToLast(number)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    arrMessage.clear()
                    for (i in snapshot.children) {
                        val message = i.getValue(Message::class.java)
                        if (message != null) {
                            arrMessage.add(message)
                        }
                    }
                    messageAdapter.reloadData(arrMessage)
                    if (arrMessage.size < loadingCount) {
                        binding.rvChatFragment.scrollToPosition(25)
                    }
                    if (arrMessage.size == loadingCount) {
                        binding.rvChatFragment.scrollToPosition(12)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("doanpt", "${Gson().toJson(response)}")
                } else {
                    Log.d("doanpt", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.d("doanpt", "${e.toString()}")
            }
        }
}