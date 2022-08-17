package com.bytedance.sdk.open.tiktok

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bytedance.sdk.open.tiktok.base.Anchor
import com.bytedance.sdk.open.tiktok.base.MediaContent
import com.bytedance.sdk.open.tiktok.common.constants.Keys
import com.bytedance.sdk.open.tiktok.share.Share
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertThat

@RunWith(AndroidJUnit4::class)
class ShareTest {

    @Test
    fun testShare() {
        val hashtag1 = "hashtag1"
        val hashtag2 = "hashtag2"
        val callerLocalEntry = "com.tiktok.MainActivity"
        val callerPackage = "caller_package"
        val state = "ready"
        val shareExtra = "share_extra"
        val extraShareOption: HashMap<String, Any> = hashMapOf(Pair("option1", "value1"), Pair("option2", "value2"))
        val mediaList = arrayListOf("media_url1", "media_url2")
        val anchor = Anchor()
        anchor.anchorBusinessType = 10
        anchor.anchorTitle = "title"
        anchor.anchorContent = "anchor_content"
        anchor.sourceType = "anchor_source_type"

        val mediaContent = MediaContent(Share.MediaType.VIDEO, mediaList)
        val shareRequest = Share.Request()
        shareRequest.callerLocalEntry = callerLocalEntry
        shareRequest.callerPackage = callerPackage
        shareRequest.shareFormat = Share.Format.GREEN_SCREEN
        shareRequest.hashTagList = arrayListOf(hashtag1, hashtag2)
        shareRequest.state = state
        shareRequest.anchor = anchor
        shareRequest.targetSceneType = Keys.Scene.LANDPAGE_SCENE_CUT
        shareRequest.shareExtra = shareExtra
        shareRequest.extraShareOptions = extraShareOption
        shareRequest.mediaContent = mediaContent

        val bundle = shareRequest.toBundle()
        assertEquals(bundle.getInt(Keys.Share.SHARE_FORMAT), Share.Format.GREEN_SCREEN.format)
        assertEquals(bundle.getStringArrayList(Keys.Share.SHARE_HASHTAG_LIST), arrayListOf(hashtag1, hashtag2))
        assertEquals(bundle.getString(Keys.Share.SHARE_DEFAULT_HASHTAG), hashtag1)
        assertEquals(bundle.getString(Keys.Share.CALLER_LOCAL_ENTRY), callerLocalEntry)
        assertEquals(bundle.getString(Keys.Share.CALLER_PKG), callerPackage)
        assertEquals(bundle.getString(Keys.Share.STATE), state)
        assertEquals(bundle.getString(Keys.Share.OPENPLATFORM_EXTRA), shareExtra)
        assertEquals(bundle.getSerializable(Keys.Share.EXTRA_SHARE_OPTIONS), extraShareOption)
        assertEquals(bundle.get(Keys.VIDEO_PATH), mediaList)
        val parsedAnchor = Gson().fromJson(bundle.getString(Keys.Share.SHARE_ANCHOR_INFO), Anchor::class.java)
        assertEquals(parsedAnchor, anchor)
        assertEquals(bundle.getInt(Keys.Share.SHARE_TARGET_SCENE), Keys.Scene.LANDPAGE_SCENE_CUT)
    }
}