package br.com.afischer.sample_aflibrary

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.afischer.aflibrary.AFLibraryApp
import br.com.afischer.aflibrary.R
import br.com.afischer.aflibrary.databinding.ActivityMainBinding
import br.com.afischer.aflibrary.extensions.viewBinding
import br.com.afischer.aflibrary.views.Progress
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MainActivity: AppCompatActivity() {

        private val binding by viewBinding(ActivityMainBinding::inflate)
        private var progress: Progress? = null


        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                enableEdgeToEdge()
                setContentView(binding.root)

                ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
                        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                        insets
                }



                AFLibraryApp.init(this.application)

                initListeners()
        }



        private fun initListeners() {

                binding.actionbarButton.setOnClickListener {
                        if (supportActionBar?.isShowing == true) {
                                supportActionBar?.hide()
                        } else {
                                supportActionBar?.show()
                        }
                }

                binding.progressButton.setOnClickListener {
                        progress = Progress(this)
                        binding.progressButton.text = "Progress showing"
                        progress?.show()

                        Handler(Looper.myLooper()!!).postDelayed({
                                progress?.hide()
                                binding.progressButton.text = "Progress"
                        }, 15000)
                }
        }
}