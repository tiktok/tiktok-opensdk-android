package com.tiktok.open.sdk.auth.utils

import java.security.MessageDigest
import java.security.SecureRandom

object PKCEUtils {

    private const val BYTE_ARRAY_SIZE = 32

    fun generateCodeVerifier(): String {
        val alphanumeric = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val secureRandom = SecureRandom()
        val codeVerifier = (1..BYTE_ARRAY_SIZE)
            .map { alphanumeric[secureRandom.nextInt(alphanumeric.size)] }
            .joinToString("")

        return codeVerifier
    }

    internal fun generateCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(codeVerifier.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
