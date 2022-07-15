package com.bytedance.sdk.demo.auth

import android.content.Context
import android.media.tv.TvContract
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.auth.model.*

class MainActivity : AppCompatActivity() {
    private lateinit var scopesView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scopesView = findViewById(R.id.recycler_view)
        scopesView.adapter = ScopeAdapter(initData())
        scopesView.layoutManager = LinearLayoutManager(this)
    }

    private fun initData(): List<DataModel> {
        return mutableListOf<DataModel>().apply {
            add(initLogo())
            add(initHeader())
            addAll(initScopes())
            addAll(initEditFields())
        }
    }

    private fun initLogo(): LogoModel {
        return LogoModel()
    }
    private fun initHeader(): HeaderModel {
        return HeaderModel("Scope configuration")
    }
    private fun initScopes(): List<ScopeModel> {
        val scopes = arrayListOf("user.info.basic", "user.info.name", "user.info.phone",
                "user.info.email", "music.collection", "video.upload", "video.list", "user.ue")
        val descriptions = arrayListOf("Read your profile info (avatar, display name)",
                "Read username", "Read user phone number", "Read user email address", "Read songs added to your favorites on TikTok",
        "Read user's uploaded videos", "Read your public videos on TikTok", "Read user interests")
        val beans = scopes.zip(descriptions) { scope, desc ->
            ScopeModel(scope, desc, false)
        }
        return beans
    }
    private fun initEditFields(): List<EditTextModel> {
        val titles = arrayListOf("Additional Permissions", "Extra Info")
        val descriptions = arrayListOf("Separated by comma, for example: \"permission1\",\"permission2\"\nGo to TT4D portal for more information",
                "Paired by comma and separated by comma, for example: \"name1:value1, name2:value2\"\nGo to TT4D portal for more information")
        return titles.zip(descriptions) { title, desc ->
            EditTextModel(title, desc)
        }
    }
}