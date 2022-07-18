package com.bytedance.sdk.demo.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.auth.model.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var scopesView: RecyclerView
    private lateinit var authTextView: TextView
    private lateinit var models: List<DataModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scopesView = findViewById(R.id.recycler_view)
        initData()
        scopesView.adapter = ScopeAdapter(models)
        scopesView.layoutManager = LinearLayoutManager(this)
    }

    private fun initData() {
        authTextView = findViewById(R.id.auth_button)
        authTextView.setOnClickListener {
            this.authorize()
        }
        models = mutableListOf<DataModel>().apply {
            add(initLogo())
            add(initHeader())
            addAll(initScopes())
            addAll(initEditFields())
        }
    }

    private fun authorize() {
        val scopes = mutableListOf<String>()
        var additionalPermissions: Array<String>? = null
        var extraInfo: Map<String, String>? = null
        for (model in models) {
            when (model) {
                is ScopeModel -> {
                    model.title.takeIf { model.isOn }?.let { scopes.add(it) }
                }
                is EditTextModel -> {
                    // 1. validate
                    // 2. parse
                    model.gsonEditText()?.let {
                        val gson = Gson()
                        try {
                            when(model.contentType) {
                                ContentType.GSON_ARRAY -> {
                                    gson.fromJson(it, Array<String>::class.java).also { jsonArray ->
                                        additionalPermissions = jsonArray
                                    }
                                }
                                ContentType.GSON_OBJECT -> {
                                    (gson.fromJson(it, Map::class.java) as Map<String, String>)?.let { extraInfo = it }
                                }
                            }
                        } catch(e: Exception) {
                            showAlert("Input Parsing Error", "Parsing ${model.title} failed. It's of invalid format. Please try again.")
                            return@authorize
                        }
                    }
                }
            }
        }
//        Log.e("debug", "scopes are: [$scopes]")
//        Log.e("debug", "additional permissions are: [$additionalPermissions]")
//        Log.e("debug", "extraInfo are: {$extraInfo}")
    }

    private fun showAlert(title: String, desc: String) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setTitle(title)
        alertBuilder.setMessage(desc)
        alertBuilder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }
        alertBuilder.create().show()
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
        val additionalPermission = EditTextModel("Additional Permissions", "Separated by comma, for example: \"permission1\",\"permission2\"\nGo to TT4D portal for more information", ContentType.GSON_ARRAY)
        val extraInfo = EditTextModel("Extra Info", "Paired by comma and separated by comma, for example: \"name1:value1, name2:value2\"\nGo to TT4D portal for more information", ContentType.GSON_OBJECT)
        return arrayListOf(additionalPermission, extraInfo)
    }
}