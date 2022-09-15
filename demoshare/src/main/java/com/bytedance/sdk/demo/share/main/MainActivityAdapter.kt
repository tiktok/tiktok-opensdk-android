package com.bytedance.sdk.demo.share.main

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.R
import com.bytedance.sdk.demo.share.model.*

class MainActivityAdapter(val models: List<DataModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ToggleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val toggle: ToggleButton
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            toggle = view.findViewById(R.id.toggle)
        }
    }
    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val header: TextView
        init {
            header = view.findViewById(R.id.header)
        }
    }
    class InfoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val info: TextView
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            info = view.findViewById(R.id.info)
        }
    }
    class HintedTextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val editText: EditText
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            editText = view.findViewById(R.id.edit_text)
        }
    }
    class LogoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val logo: ImageView
        init {
            logo = view.findViewById(R.id.image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(ViewType.typeFrom(viewType)) {
            ViewType.LOGO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.logo_item, parent, false)
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
            ViewType.INFO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.info_item, parent, false)
                InfoViewHolder(view)
            }
            ViewType.HINTED_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.hinted_item, parent, false)
                HintedTextViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.logo_item, parent, false)
                ToggleViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (models.size <= position) {
            return
        }
        when(val model = models[position]) {
            is HeaderModel -> {
                (holder as HeaderViewHolder).let {
                    it.header.text = model.title
                }
            }
            is ToggleModel -> {
                (holder as ToggleViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.toggle.isChecked = model.isOn.value ?: false
                    it.toggle.setOnCheckedChangeListener() { _, isOn ->
                        model.isOn.postValue(isOn)
                    }
                }
            }
            is HintedTextModel -> {
                (holder as HintedTextViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.editText.setText(model.text.value, TextView.BufferType.EDITABLE)
                    it.editText.hint = model.placeholder
                    it.editText.addTextChangedListener(object: TextWatcher {
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun onTextChanged(text: CharSequence?, start: Int, lenBefore: Int, lenAfter: Int) {
                            model.text.postValue(text.toString())
                        }
                        override fun afterTextChanged(p0: Editable?) {}
                    })
                    model.isEditable.observeForever { isEditable ->
                        it.editText.isEnabled = isEditable
                        if (!isEditable) {
                            it.editText.text = null
                        }
                    }
                }
            }
            is InfoModel -> {
                (holder as InfoViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.info.text = model.info.value ?: ""
                }
            }
            is LogoModel -> {
            }
            else -> {

            }
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}