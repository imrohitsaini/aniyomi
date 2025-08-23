package com.justappz.aniyomitv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
        val startTime = System.currentTimeMillis() // 👈 use descriptive
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("Splash", "setContentView done in ${System.currentTimeMillis() - startTime} ms")
    }
    //endregion

    //region onResume
    override fun onResume() {
        super.onResume()
        loadData()
    }
    //endregion

    //region loadData
    private fun loadData() {
        Log.d("Splash", "loadData started at ${System.currentTimeMillis()}")
        val drawable = binding.ivLogo.drawable
        when (drawable) {
            is AnimatedVectorDrawable -> {
                drawable.registerAnimationCallback(
                    object : Animatable2.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable?) {
                            Log.d("Splash", "Animation ended at ${System.currentTimeMillis()}")
                            startMain()
                        }
                    },
                )
                drawable.start()
            }

            is AnimatedVectorDrawableCompat -> {
                drawable.registerAnimationCallback(
                    object :
                        Animatable2Compat.AnimationCallback() {
                        override fun onAnimationEnd(drawable: Drawable) {
                            Log.d("Splash", "Animation ended at ${System.currentTimeMillis()}")
                            startMain()
                        }
                    },
                )
                drawable.start()
            }
        }
    }
    //endregion

    //region startMain
    private fun startMain() {
        // Delay for 5 seconds, then start MainActivity
        lifecycleScope.launch {
            delay(1000) // 5 seconds
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
