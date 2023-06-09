package com.tiktok.open.sdk.auth.utils

import java.security.MessageDigest
import java.security.SecureRandom

object PKCEUtils {

    private const val BYTE_ARRAY_SIZE = 32

    fun generateCodeVerifier(): String {
        val codeVerifierBytes = ByteArray(BYTE_ARRAY_SIZE)
        val secureRandom = SecureRandom()
        secureRandom.nextBytes(codeVerifierBytes)

        return String(codeVerifierBytes, Charsets.US_ASCII)
    }

    internal fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(codeVerifier.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
