package br.com.afischer.afextensions

import android.app.Application


object AFExtApp {
        
        private lateinit var instance: Application
        
        
        fun init(app: Application) {
                instance = app
        }
        
        
        operator fun invoke() = instance
}