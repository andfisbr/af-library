package br.com.afischer.aflibrary.extensions

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import br.com.afischer.aflibrary.enums.AlerterType
import com.tapadoo.alerter.Alert
import com.tapadoo.alerter.Alerter
import javax.crypto.Cipher


fun Fragment.callBiometricChecker(
        title: String,
        message: String? = null,
        negativeLabel: String,
        confirmationRequired: Boolean = true,
        initializedCipher: Cipher? = null,
        onAuthSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onAuthError: (Int, String) -> Unit
) {
        val executor = ContextCompat.getMainExecutor(context!!)
        val prompt = BiometricPrompt(
                this,
                executor,
                object: BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                onAuthSuccess(result)
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                                onAuthError(errorCode, errString.toString())
                        }
                }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .apply { if (message != null) { setDescription(message) } }
                .setConfirmationRequired(confirmationRequired)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                .setNegativeButtonText(negativeLabel)
                .build()

        initializedCipher?.let {
                val cryptoObject = BiometricPrompt.CryptoObject(it)
                prompt.authenticate(promptInfo, cryptoObject)
                return
        }

        prompt.authenticate(promptInfo)
}



fun Fragment.alerter(type: AlerterType = AlerterType.NORMAL, duration: Long = 3000L, body: Alerter.() -> Unit): Alert? {
        return requireActivity().alerter(
                type,
                duration,
                body
        )
}
