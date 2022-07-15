package com.bytedance.sdk.demo.auth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.CompoundButton.OnCheckedChangeListener
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
        init {
            title = view.findViewById(R.id.title)
            subtitle = view.findViewById(R.id.subtitle)
            toggle = view.findViewById(R.id.toggle)
            toggle.setOnCheckedChangeListener(object: OnCheckedChangeListener {
                override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
                    print("changed: ${checked}")
                }
            })
        }
    }
    class EditTextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val desc: TextView
        init {
            title = view.findViewById(R.id.title)
            desc = view.findViewById(R.id.desc)
        }
    }
    class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val header: TextView
        init {
            header = view.findViewById(R.id.header)
        }
    }
    class LogoViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val logo: ImageView
        init {
            logo = view.findViewById(R.id.image_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(ViewType.typeFrom(viewType)) {
            SCOPE -> {
                val view =LayoutInflater.from(parent.context).inflate(R.layout.scope_item_layout, parent, false)
                ScopeViewHolder(view)
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
                }
            }
            is ScopeModel -> {
                (holder as ScopeViewHolder).apply {
                    title.text = model.title
                    subtitle.text = model.desc
                    toggle.isChecked = model.isOn
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

    override fun getItemCount(): Int = models.size
    override fun getItemViewType(position: Int): Int = models[position].viewType.value
}