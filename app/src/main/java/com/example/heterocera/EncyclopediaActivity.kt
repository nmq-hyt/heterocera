package com.example.heterocera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.heterocera.databinding.ActivityCameraBinding
import com.example.heterocera.databinding.ActivityEncyclopediaBinding
import com.example.heterocera.fragments.EncyclopediaFragment

private lateinit var activityEncyclopediaBinding: ActivityEncyclopediaBinding

class EncyclopediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encyclopedia)


        if (savedInstanceState == null) {
            val fragment = EncyclopediaFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.encyclopediaFragment,fragment)
                .commit()
        }

    }



}




