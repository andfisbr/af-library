package br.com.afischer.aflibrary

import android.app.Application


object AFLibraryApp {
        
        private lateinit var instance: Application
        
        
        fun init(app: Application) {
                instance = app
        }
        
        
        operator fun invoke() = instance
}