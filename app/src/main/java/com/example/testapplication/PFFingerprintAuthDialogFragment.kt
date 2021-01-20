package com.example.testapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.DialogFragment

class PFFingerprintAuthDialogFragment : DialogFragment() {

    private lateinit var mCancelButton: Button
    private var mFingerprintContent: View? = null

    private var mStage: Stage =
        PFFingerprintAuthDialogFragment.Stage.FINGERPRINT

    private val mCryptoObject: FingerprintManagerCompat.CryptoObject? = null

    private var mFingerprintCallback: PFFingerprintUIHelper? = null

    private var mContext: Context? = null

    private var mAuthListener: PFFingerprintAuthListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        retainInstance = true
        setStyle(STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.setTitle("Войти")
        val v: View = inflater.inflate(
            R.layout.view_pf_fingerprint_dialog_container, container,
            false
        )
        mCancelButton = v.findViewById(R.id.cancel_button)
        mCancelButton.setOnClickListener { dismiss() }
        mFingerprintContent = v.findViewById(R.id.fingerprint_container)
        val manager = FingerprintManagerCompat.from(context!!)
        mFingerprintCallback = PFFingerprintUIHelper(
            manager,
            (v.findViewById<View>(R.id.fingerprint_icon) as ImageView),
            (v.findViewById<View>(R.id.fingerprint_status) as TextView),
            mAuthListener!!
        )
        updateStage()
        return v
    }

    override fun onResume() {
        super.onResume()
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintCallback!!.startListening(mCryptoObject)
        }
    }

    fun setStage(stage: Stage) {
        mStage = stage
    }

    override fun onPause() {
        super.onPause()
        mFingerprintCallback!!.stopListening()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    /**
     * Sets the crypto object to be passed in when authenticating with fingerprint.
     */
    /*public void setCryptoObject(FingerprintManagerCompat.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }*/
    private fun updateStage() {
        when (mStage) {
            Stage.FINGERPRINT -> {
                mCancelButton.setText(R.string.cancel_pf)
                mFingerprintContent!!.visibility = View.VISIBLE
            }
        }
    }


    fun setAuthListener(authListener: PFFingerprintAuthListener) {
        mAuthListener = authListener
    }

    /**
     * Enumeration to indicate which authentication method the user is trying to authenticate with.
     */
    enum class Stage {
        FINGERPRINT
    }
}