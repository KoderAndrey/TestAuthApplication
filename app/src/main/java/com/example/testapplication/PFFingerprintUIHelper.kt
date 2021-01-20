package com.example.testapplication

import android.widget.ImageView
import android.widget.TextView
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal

class PFFingerprintUIHelper(
    private val mFingerprintManager: FingerprintManagerCompat,
    private val mIcon: ImageView, private val mErrorTextView: TextView,
    private val mCallback: PFFingerprintAuthListener
) : FingerprintManagerCompat.AuthenticationCallback() {
    private var mCancellationSignal: CancellationSignal? = null
    private var mSelfCancelled = false

    // The line below prevents the false positive inspection from Android Studio
    // noinspection ResourceType
    val isFingerprintAuthAvailable: Boolean
        get() =// The line below prevents the false positive inspection from Android Studio
            // noinspection ResourceType
            (mFingerprintManager.isHardwareDetected
                    && mFingerprintManager.hasEnrolledFingerprints())

    fun startListening(cryptoObject: FingerprintManagerCompat.CryptoObject?) {
        if (!isFingerprintAuthAvailable) {
            return
        }
        mCancellationSignal = CancellationSignal()
        mSelfCancelled = false
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        mFingerprintManager.authenticate(
            cryptoObject, 0, mCancellationSignal, this, null
        )
        mIcon.setImageResource(R.drawable.ic_fingerprint)
    }

    fun stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true
            mCancellationSignal!!.cancel()
            mCancellationSignal = null
        }
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        if (!mSelfCancelled) {
            showError(errString)
            mIcon.postDelayed(
                { mCallback.onError() },
                PFFingerprintUIHelper.Companion.ERROR_TIMEOUT_MILLIS
            )
        }
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        showError(helpString)
    }

    override fun onAuthenticationFailed() {
        showError(
           "Отпечаток пальца не распознан."
        )
    }

    override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable)
        mIcon.setImageResource(R.drawable.ic_fingerprint_success_pf)
        mErrorTextView.setTextColor(
            mErrorTextView.resources.getColor(R.color.success_color, null)
        )
        mErrorTextView.text = "Отпечаток пальца распознан"
        mIcon.postDelayed(
            { mCallback.onAuthenticated() },
            PFFingerprintUIHelper.Companion.SUCCESS_DELAY_MILLIS
        )
    }

    private fun showError(error: CharSequence) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error_pf)
        mErrorTextView.text = error
        mErrorTextView.setTextColor(
            mErrorTextView.resources.getColor(R.color.warning_color, null)
        )
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable)
        mErrorTextView.postDelayed(
            mResetErrorTextRunnable,
            PFFingerprintUIHelper.Companion.ERROR_TIMEOUT_MILLIS
        )
    }

    private val mResetErrorTextRunnable = Runnable {
        mErrorTextView.setTextColor(
            mErrorTextView.resources.getColor(R.color.hint_color, null)
        )
        mErrorTextView.text = "Сенсорный датчик"
        mIcon.setImageResource(R.drawable.ic_fingerprint)
    }

    companion object {
        private const val ERROR_TIMEOUT_MILLIS: Long = 1600
        private const val SUCCESS_DELAY_MILLIS: Long = 200
    }
}
