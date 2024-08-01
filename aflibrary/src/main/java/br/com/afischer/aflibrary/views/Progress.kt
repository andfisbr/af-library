package br.com.afischer.aflibrary.views

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import br.com.afischer.aflibrary.R
import br.com.afischer.aflibrary.databinding.ProgressViewBinding
import br.com.afischer.aflibrary.extensions.animateWithAlpha
import br.com.afischer.aflibrary.extensions.asColor
import br.com.afischer.aflibrary.extensions.asHtml
import br.com.afischer.aflibrary.extensions.hide
import br.com.afischer.aflibrary.extensions.isLightNavigationBar
import br.com.afischer.aflibrary.extensions.layoutInflater
import br.com.afischer.aflibrary.extensions.setNavigationBarColor
import br.com.afischer.aflibrary.extensions.show
import br.com.afischer.aflibrary.extensions.showIf


class Progress(val context: Context) {

        var binding: ProgressViewBinding = ProgressViewBinding.inflate(context.layoutInflater)
        var viewGroup: ViewGroup = (context as Activity).window.decorView.findViewById(android.R.id.content)
        var navigationBarColor = -1




        fun show(message: String = "waiting...", listener: () -> Unit = {}) {
                //
                // altera cor do navigationBar
                //
                (context as Activity).apply {
                        navigationBarColor = window.navigationBarColor
                        setNavigationBarColor(R.color.black.asColor())
                        isLightNavigationBar = false
                }


                //
                // adiciona view na activity
                //
                viewGroup.removeView(binding.root)
                viewGroup.addView(binding.root)


                //
                // acerta valores
                //
                binding.message.text = message.replace("\n", "<br>").asHtml()
                binding.closeButton.showIf(listener != {})
                binding.closeButton.setOnClickListener { listener() }


                //
                // inicia chronometer e o mostra se necessario
                //
                binding.chronometer.start()
                binding.chronometer.onChronometerTickListener = object: ChronometerTextView.OnChronometerTickListener {
                        override fun onChronometerTick(timeLapsed: Long) {
                                if (timeLapsed > 10000 && !binding.chronometer.isShown) {
                                        binding.chronometer.alpha = 0f
                                        binding.chronometer.show()
                                        binding.chronometer.animateWithAlpha(0f, 1f, 500)
                                }
                        }
                }


                if (!binding.container.isShown) {
                        context.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.container.show()
                }
        }

        
        fun hide(local: String = "", listener: () -> Unit = {}) {
                //
                // altera cor do navigationBar
                //
                (context as Activity).apply {
                        window.navigationBarColor = navigationBarColor
                        setNavigationBarColor(R.color.black.asColor())
                        isLightNavigationBar = false
                }


                //
                // remove valores
                //
                binding.message.text = ""
                binding.container.hide()
                binding.chronometer.stop()
                binding.chronometer.hide()

                context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                //
                // remove a view da activity
                //
                viewGroup.removeView(binding.root)


                listener()
        }
        
        
        fun update(message: String = "") {
                if (binding.container.isShown) {
                        binding.message.text = message.replace("\n", "<br>").asHtml()
                }
        }


        val isVisible
                get() = binding.container.isShown
}