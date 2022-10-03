package com.bytedance.sdk.demo.share.publish

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
import com.bytedance.sdk.demo.share.model.DataModel
import com.bytedance.sdk.demo.share.model.EditModel
import com.bytedance.sdk.demo.share.model.HeaderModel
import com.bytedance.sdk.demo.share.model.TextType
import com.bytedance.sdk.demo.share.model.ToggleModel
import com.bytedance.sdk.demo.share.model.ViewType

class ShareActivityAdapter(
    private var editTextChange: (TextType, String) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        var editTextWatcher: TextWatcher?
        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
            editText = view.findViewById(R.id.edittext)
            editTextWatcher = null
        }

        fun addTextWatcher(editTextChange: (String) -> Unit) {
            editTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                    editTextChange(text.toString())
                }

                override fun afterTextChanged(s: Editable?) = Unit
            }
            editText.addTextChangedListener(editTextWatcher)
        }

        fun removeTextWatcher() {
            editTextWatcher?.let {
                editText.removeTextChangedListener(it)
            }
            editTextWatcher = null
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
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.edittext_item, parent, false)
                EditTextViewHolder(view)
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
                        model.onToggleChange(isOn)
                    }
                }
            }
            is EditModel -> {
                (holder as EditTextViewHolder).let {
                    it.title.text = holder.itemView.context.getString(model.titleRes)
                    it.desc.text = holder.itemView.context.getString(model.descRes)
                    it.addTextWatcher { editTextChange(model.type, it) }
                }
            }
        }
    }

    fun updateModels(models: List<DataModel>) {
        this.models = models
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is ToggleViewHolder -> {
                holder.toggle.setOnCheckedChangeListener(null)
            }
            is EditTextViewHolder -> {
                holder.removeTextWatcher()
            }
            else -> Unit
        }
    }

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}
