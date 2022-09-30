package com.bytedance.sdk.demo.share.publish

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.share.R
import com.bytedance.sdk.demo.share.main.MainActivityAdapter
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ViewType

class ShareActivityAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            editText = view.findViewById(R.id.edittext)
        }
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
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.logo_item, parent, false)
                MainActivityAdapter.LogoViewHolder(view)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (models.size <= position) {
            return
        }
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
                    it.toggle.setOnCheckedChangeListener() { _, isOn ->
                        //onScopeToggle(model.type, isOn)
                    }
                }
            }
            is EditModel -> {
                (holder as EditTextViewHolder).apply {
                    title.text = model.title
                    desc.text = model.desc

                    val textWatcher = object : TextWatcher {
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(p0: Editable?) {}
                        override fun onTextChanged(text: CharSequence?, start: Int, lenBefore: Int, lenAfter: Int) {
                            model.text = text.toString()
                        }
                    }
                    editText.addTextChangedListener(textWatcher)
                    editText.isEnabled = model.enabled ?: false
                    title.setTextColor(context.getColor(if (editText.isEnabled) R.color.colorTextPrimary else R.color.colorTextTertiary))
                    /*
                    model.enabled.observeForever { enabled ->
                        editText.isEnabled = enabled
                        if (enabled) {
                            title.setTextColor(context.getColor(R.color.colorTextPrimary))
                        } else {
                            editText.text = null
                            title.setTextColor(context.getColor(R.color.colorTextTertiary))
                        }
                    }

                     */
                }
            }
        }
    }

    fun updateModels(models: List<DataModel>) {
        this.models = models
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}
