package br.com.afischer.afextensions.application

import android.app.Application


object App {
        
        private lateinit var instance: Application
        
        
        fun init(app: Application) {
                instance = app
        }
        
        
        operator fun invoke() = instance
}