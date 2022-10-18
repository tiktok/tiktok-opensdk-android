package com.bytedance.sdk.open.tiktok.core

/*
    Copyright 2022 TikTok Pte. Ltd.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import com.bytedance.sdk.open.tiktok.core.utils.Md5Utils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Md5UtilTests {
    private val testCases = arrayListOf("test", "1234567890", "test with spaces", "\$peCi@l %^&*()<>:}{'/ ")
    private val verifyCases = arrayListOf(
        "098f6bcd4621d373cade4e832627b4f6",
        "e807f1fcf82d132f9bb018ca6738a19f",
        "7e457361ad0ce5929afd179909589c10",
        "9f22af6f204f90cdb3bb47590f69aa06"
    )
    @Test
    fun testMd5() {
        var md5Digest = ""
        for (i in 0 until testCases.size) {
            md5Digest = Md5Utils.hexDigest(testCases[i])!!
            Assert.assertEquals(verifyCases[i], md5Digest)
        }
    }
}
