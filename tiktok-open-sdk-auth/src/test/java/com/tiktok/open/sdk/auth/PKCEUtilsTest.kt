package com.tiktok.open.sdk.auth

import com.tiktok.open.sdk.auth.utils.PKCEUtils
import org.junit.Assert
import org.junit.Test

class PKCEUtilsTest {

    private val codeVerifier = "gaeCq5mVvbhA8Ce6w5O9XBVIKMkySgv-_9A-rnhm9Ns"
    private val codeChallenge = "e6a178b120743d08509508f269652b0bdf91e29332921bc9e9744fd3dd41bfef"

    @Test
    fun testCodeChallenge() {
        val codeChallengeGeneratedBySDK: String = PKCEUtils.generateCodeChallenge(codeVerifier)

        Assert.assertEquals(codeChallengeGeneratedBySDK, codeChallenge)
    }
}
