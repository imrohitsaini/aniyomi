package com.justappz.aniyomitv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.justappz.aniyomitv.databinding.SplashActivityBinding
import com.justappz.aniyomitv.main.ui.activities.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : FragmentActivity() {

    //region variables
    private lateinit var binding: SplashActivityBinding
    //endregion

    //region onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Delay for 5 seconds, then start MainActivity
        lifecycleScope.launch {
            delay(5000) // 5 seconds
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
    //endregion
}
