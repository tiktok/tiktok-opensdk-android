package com.bytedance.sdk.demo.share

import android.os.Bundle
import android.preference.PreferenceActivity
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.main.MainActivityAdapter
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.publish.ShareActivityAdapter

class ShareActivity: AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var publishButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var models: List<DataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.share_activity)
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener { finish() }
        publishButton = findViewById(R.id.share_button)
        publishButton.setOnClickListener { publish() }

        recyclerView = findViewById(R.id.recycler_view)
        initData()
        recyclerView.adapter = ShareActivityAdapter(models)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun publish() {

    }

    private fun initData() {
        models = mutableListOf<DataModel>().apply {
            add(initHeader())
            add(initHashtag())
            addAll(initToggles())
            add(initAnchorEditModel())
            addAll(initExtraEditText())
        }
    }

    private fun initExtraEditText(): List<EditModel> {
        val extraContent = MutableLiveData("")
        val extraEdit = EditModel("Extra", "JSONObject string of information included in Share request", extraContent)
        return arrayListOf(extraEdit)
    }

    private fun initHashtag(): EditModel {
        val hashtagContent = MutableLiveData("")
        val hashTagEdit = EditModel("Hashtag", "Hashtags attached to the video i.e. #abc #def", hashtagContent)
        return hashTagEdit
    }

    private fun initAnchorEditModel(): EditModel {
        val anchorContent = MutableLiveData("")
        val anchorEdit = EditModel("Anchor source type", "The types of anchors that will be attached to the video", anchorContent)
        return anchorEdit
    }

    private fun initToggles(): List<ToggleModel> {
        val disableMusicOn = MutableLiveData(false)
        val disableMusic = ToggleModel("Disable music selection", "Suppress automatically attaching music", disableMusicOn)

        val gsOn = MutableLiveData(false)
        val greenScreen = ToggleModel("Use green-screen format", "Automatically apply green-screen effect", gsOn)

        val anchorOn = MutableLiveData(false)
        val anchor = ToggleModel("Auto attach anchor", "Automatically attach anchor to the video", anchorOn)

        return arrayListOf(disableMusic, greenScreen, anchor)
    }

    private fun initHeader(): HeaderModel {
        return HeaderModel("Share meta info", "Description of the video kit and the features available in the SDK demo app")
    }
}