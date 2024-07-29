package br.com.afischer.aflibrary.views

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import br.com.afischer.aflibrary.R
import br.com.afischer.aflibrary.databinding.ProgressViewBinding
import br.com.afischer.aflibrary.extensions.asColor
import br.com.afischer.aflibrary.extensions.asHtml
import br.com.afischer.aflibrary.extensions.hide
import br.com.afischer.aflibrary.extensions.isLightNavigationBar
import br.com.afischer.aflibrary.extensions.layoutInflater
import br.com.afischer.aflibrary.extensions.setNavigationBarColor
import br.com.afischer.aflibrary.extensions.show



class Progress(val context: Context) {

        var binding: ProgressViewBinding = ProgressViewBinding.inflate(context.layoutInflater)
        var viewGroup: ViewGroup = (context as Activity).window.decorView.findViewById(android.R.id.content)
        var navigationBarColor = -1




        fun show(message: String = "waiting...", listener: () -> Unit = {}) {

                (context as Activity).apply {
                        navigationBarColor = window.navigationBarColor
                        setNavigationBarColor(R.color.black.asColor())
                        isLightNavigationBar = false
                }

                viewGroup.removeView(binding.root)
                viewGroup.addView(binding.root)

                binding.message.text = message.replace("\n", "<br>").asHtml()
                // binding.progressViewCloseButton.showIf(listener != {})
                binding.closeButton.setOnClickListener { listener() }

                if (!binding.container.isShown) {
                        context.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        binding.container.show()
                }
        }

        
        fun hide(local: String = "", listener: () -> Unit = {}) {
                (context as Activity).apply {
                        window.navigationBarColor = navigationBarColor
                        setNavigationBarColor(R.color.black.asColor())
                        isLightNavigationBar = false
                }

                binding.message.text = ""
                binding.container.hide(true)

                context.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

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