package com.bytedance.sdk.demo.auth

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.sdk.demo.auth.ViewType.*
import com.bytedance.sdk.demo.auth.model.*

enum class ViewType(val value: Int) {
    SCOPE(0), LOGO(1), HEADER(2), EDIT_TEXT(3);
    companion object {
        fun typeFrom(value: Int): ViewType {
            return when (value) {
                1 -> { LOGO }
                2 -> { HEADER }
                3 -> { EDIT_TEXT }
                else -> { SCOPE }
            }
        }
    }
}

class ScopeAdapter(private val models: List<DataModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ScopeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val subtitle: TextView
        val toggle: ToggleButton
        lateinit var model: ScopeModel

        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            toggle = view.findViewById(R.id.toggle)
        }
    }
    class EditTextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val desc: TextView
        val editText: EditText
        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            editText = view.findViewById(R.id.edittext)
        }
    }
    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val header: TextView
        init {
            header = view.findViewById(R.id.header)
        }
    }
    class LogoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val logo: ImageView
        init {
            logo = view.findViewById(R.id.image_view)
        }
    }
    private var textWatchers = mutableListOf<TextWatcher>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(ViewType.typeFrom(viewType)) {
            SCOPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.scope_item_layout, parent, false)
                return ScopeViewHolder(view)
            }
            EDIT_TEXT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.edittext_item_layout, parent, false)
                EditTextViewHolder(view)
            }
            HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.header_item, parent, false)
                HeaderViewHolder(view)
            }
            LOGO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.logo_item, parent, false)
                return LogoViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (models.size <= position) {
            return
        }
        when(val model = models[position]) {
            is EditTextModel ->  {
                (holder as EditTextViewHolder).apply {
                    title.text = model.title
                    desc.text = model.desc
                    val textWatcher = object: TextWatcher {
                        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                        override fun afterTextChanged(p0: Editable?) {}
                        override fun onTextChanged(text: CharSequence?, start: Int, lenBefore: Int, lenAfter: Int) {
                            model.editText = text.toString()
                        }
                    }
                    editText.addTextChangedListener(textWatcher)
                }
            }
            is ScopeModel -> {
                (holder as ScopeViewHolder).let {
                    it.title.text = model.title
                    it.subtitle.text = model.desc
                    it.toggle.isChecked = model.isOn
                    it.model = model
                    it.toggle.setOnCheckedChangeListener() { _, isOn ->
                        for (inputModel in this.models) {
                            if (model == inputModel) {
                                model.isOn = isOn
                                break
                            }
                        }
                    }
                }
            }
            is HeaderModel -> {
                (holder as HeaderViewHolder).apply {
                    header.text = model.header
                }
            }
            is LogoModel -> {}
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is EditTextViewHolder -> {
                this.textWatchers.forEach {
                    holder.editText.removeTextChangedListener(it)
                }
            }
            is ScopeViewHolder -> {
                holder.toggle.setOnClickListener(null)
            }
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value

}