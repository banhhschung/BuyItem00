package com.example.buyitem00

import android.R
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.ActivityWelcomeBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.Supporter
import com.github.ybq.android.spinkit.sprite.Sprite
import com.github.ybq.android.spinkit.style.FoldingCube
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private val supporterViewModel: SupporterViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foldingCube: Sprite = FoldingCube()
        binding.progress.indeterminateDrawable = foldingCube

        Toast.makeText(applicationContext, "Take a few minutes to start up", Toast.LENGTH_SHORT)
            .show()
        val animationForButton = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        binding.tbStart.visibility = View.INVISIBLE
        FirebaseDatabase.getInstance().reference.child("Supporter")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val supporter = i.getValue(Supporter::class.java)
                        val localFile = File.createTempFile("tempImage", "jpg")
                        if (supporter != null) {
                            supporterViewModel.addSupporter(supporter)
                            lifecycleScope.launch(Dispatchers.IO) {
                                val storageFB =
                                    FirebaseStorage.getInstance().reference.child("images/${supporter.avatar}")
                                storageFB.getFile(localFile).addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                    imageViewModel.addToData(Image(supporter.uid, bitmap))
                                }
                            }
                        }
                    }
                    binding.tbStart.visibility = View.VISIBLE
                    binding.tbStart.startAnimation(animationForButton)
                    binding.tbStart.setOnClickListener {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}