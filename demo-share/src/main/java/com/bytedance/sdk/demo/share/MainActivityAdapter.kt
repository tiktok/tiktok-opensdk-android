package com.bytedance.sdk.demo.share

/*
 *  Copyright (c)  2022 TikTok Pte. Ltd. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditTextModel
import com.bytedance.sdk.demo.share.model.EditTextType
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.InfoModel
import com.bytedance.sdk.demo.share.model.LogoModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ViewType

class MainActivityAdapter(
    private val onSaveEditTextValue: (String) -> Unit,
    private val onSaveToggleStatus: (Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var models: List<DataModel> = listOf()
    private lateinit var recyclerView: RecyclerView

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val header: TextView = view.findViewById(R.id.header)
    }

    class ToggleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val subtitle: TextView = view.findViewById(R.id.subtitle)
        val toggle: ToggleButton = view.findViewById(R.id.toggle)
    }

    class EditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val desc: TextView = view.findViewById(R.id.desc)
        val editText: EditText = view.findViewById(R.id.edittext)
        var editTextType: EditTextType? = null
    }

    class InfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val subtitle: TextView = view.findViewById(R.id.subtitle)
        val info: TextView = view.findViewById(R.id.info)
    }

    class LogoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logo: ImageView
        init {
            logo = view.findViewById(R.id.image_view)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.typeFrom(viewType)) {
            ViewType.LOGO -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.logo_item, parent, false)
                LogoViewHolder(view)
            }
            ViewType.HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(view)
            }
            ViewType.TOGGLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.toggle_item, parent, false)
                ToggleViewHolder(view)
            }
            ViewType.EDIT_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.edittext_item, parent, false)
                EditTextViewHolder(view)
            }
            ViewType.INFO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.info_item, parent, false)
                InfoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = models[position]) {
            is HeaderModel -> {
                (holder as HeaderViewHolder).let {
                    it.header.text = model.title
                }
            }
            is ToggleModel -> {
                (holder as ToggleViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.toggle.isChecked = model.isOn
                    it.toggle.setOnCheckedChangeListener { _, isOn ->
                        onSaveToggleStatus(isOn)
                    }
                }
            }
            is EditTextModel -> {
                (holder as EditTextViewHolder).let {
                    it.title.text = holder.itemView.context.getString(model.titleRes)
                    it.desc.text = holder.itemView.context.getString(model.descRes)
                    it.editText.setText(model.text)
                    it.editText.hint = model.hint
                    it.editTextType = model.type
                    it.editText.isEnabled = model.enabled
                }
            }
            is InfoModel -> {
                (holder as InfoViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.info.text = model.info
                }
            }
            is LogoModel -> {
            }
            else -> {
            }
        }
    }

    fun updateModels(models: List<DataModel>) {
        this.models = models
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is ToggleViewHolder -> {
                holder.toggle.setOnCheckedChangeListener(null)
            }
            is EditTextViewHolder -> {
                holder.editTextType?.let {
                    onSaveEditTextValue(holder.editText.text.toString())
                }
            }
            else -> Unit
        }
    }

    fun saveTextInput() {
        for (i in 0 until recyclerView.childCount) {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i))
            if (holder is EditTextViewHolder) {
                holder.editTextType?.let {
                    onSaveEditTextValue(holder.editText.text.toString())
                }
            }
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}
