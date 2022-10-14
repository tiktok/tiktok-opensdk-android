package com.bytedance.sdk.demo.share.publish

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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.R
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditTextModel
import com.bytedance.sdk.demo.share.model.EditTextType
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ToggleType
import com.bytedance.sdk.demo.share.model.ViewType

class ShareActivityAdapter(
    private val onSaveEditTextValue: (EditTextType, String) -> Unit,
    private val onSaveToggleStatus: (ToggleType, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    private var models: List<DataModel> = listOf()

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val header: TextView
        val desc: TextView
        init {
            header = view.findViewById(R.id.header)
            desc = view.findViewById(R.id.desc)
        }
    }
    class ToggleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val toggle: ToggleButton
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            toggle = view.findViewById(R.id.toggle)
        }
    }

    class EditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val desc: TextView
        val editText: EditText
        var editTextType: EditTextType? = null

        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            editText = view.findViewById(R.id.edittext)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (ViewType.typeFrom(viewType)) {
            ViewType.HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(view)
            }
            ViewType.TOGGLE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.toggle_item, parent, false)
                return ToggleViewHolder(view)
            }
            ViewType.EDIT_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.edittext_item, parent, false)
                EditTextViewHolder(view)
            }
            else -> throw Exception("Invalid View Type")
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val model = models[position]) {
            is HeaderModel -> {
                (holder as HeaderViewHolder).let {
                    it.header.text = model.title
                    model.desc?.let { desc ->
                        it.desc.text = desc
                        it.desc.visibility = View.VISIBLE
                    }
                }
            }
            is ToggleModel -> {
                (holder as ToggleViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.toggle.isChecked = model.isOn
                    it.toggle.setOnCheckedChangeListener { _, isOn ->
                        onSaveToggleStatus(model.toggleType, isOn)
                    }
                }
            }
            is EditTextModel -> {
                (holder as EditTextViewHolder).let {
                    it.title.text = holder.itemView.context.getString(model.titleRes)
                    it.desc.text = holder.itemView.context.getString(model.descRes)
                    it.editText.setText(model.text)
                    it.editTextType = model.type
                }
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
                    onSaveEditTextValue(it, holder.editText.text.toString())
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
                    onSaveEditTextValue(it, holder.editText.text.toString())
                }
            }
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}
