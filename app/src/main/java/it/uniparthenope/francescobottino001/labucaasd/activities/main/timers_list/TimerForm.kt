package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData

class TimerForm @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialCardView(ctx, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(ctx).inflate(R.layout.timer_form, this, true)
    }

    private val title: TextView = this.findViewById(R.id.alert_title)
    private val nameFieldLayout: TextInputLayout = this.findViewById(R.id.name_field_layout)
    private val hourlyCostFieldLayout: TextInputLayout = this.findViewById(R.id.hourly_cost_field_layout)
    private val nameField: EditText = this.findViewById(R.id.name_field)
    private val hourlyCostField: EditText = this.findViewById(R.id.hourly_cost_field)
    private val positiveButton: Button = this.findViewById(R.id.positive_button)
    private val negativeButton: Button = this.findViewById(R.id.negative_button)

    init {
        nameFieldLayout.apply {
            setErrorIconOnClickListener {
                this.error = null
            }
        }
        hourlyCostFieldLayout.apply {
            setErrorIconOnClickListener {
                this.error = null
            }
        }
    }

    enum class FORM_TYPE(
        @StringRes val titleString: Int,
        @StringRes val positiveButtonString: Int,
        @StringRes val negativeButtonString: Int,
        val formVisible: Boolean
    ) {
        NEW_TIMER(
            R.string.new_timer_dialog_title,
            R.string.new_timer_dialog_positive_button,
            R.string.timer_dialog_negative_button,
            true
        ), EDIT_TIMER(
            R.string.edit_timer_dialog_title,
            R.string.edit_timer_dialog_positive_button,
            R.string.timer_dialog_negative_button,
            true
        ), DELETE_TIMER(
            R.string.delete_timer_dialog_title,
            R.string.delete_timer_dialog_positive_button,
            R.string.timer_dialog_negative_button,
            false
        );
    }

    fun setUpLayout(formType: FORM_TYPE, positiveButtonCallback: (()->Unit)? = null, negativeButtonCallback: (()->Unit)? = null, initValuesFrom: TimerData? = null) {
        title.apply {
            text = this.context.resources.getString(formType.titleString)
        }
        positiveButton.apply {
            text = this.context.resources.getString(formType.positiveButtonString)
            setOnClickListener {
                positiveButtonCallback?.invoke()
            }
        }
        negativeButton.apply {
            text = this.context.resources.getString(formType.negativeButtonString)
            setOnClickListener {
                negativeButtonCallback?.invoke()
            }
        }
        if(formType.formVisible) {
            nameFieldLayout.visibility = View.VISIBLE
            hourlyCostFieldLayout.visibility = View.VISIBLE

            initValuesFrom?.let {
                nameField.setText(it.name)
                hourlyCostField.setText(it.hourlyCost.toString())
            }
        } else {
            nameFieldLayout.visibility = View.GONE
            hourlyCostFieldLayout.visibility = View.GONE
        }
    }

    fun getFormData(callback: (String, Double)->Unit) {
        var errors = false

        val name = nameField.text.toString()
        if( name.trim().length < 3) {
            nameFieldLayout.error = this.context.resources.getString(R.string.new_timer_dialog_empty_name_error)
            errors = true
        }

        val cost= try {
            hourlyCostField.text.toString().toDouble()
        } catch (e: Exception) {
            hourlyCostFieldLayout.error = this.context.resources.getString(R.string.new_timer_dialog_wrong_cost_error)
            errors = true
            0.0
        }

        if(errors) throw Exception()
        else callback.invoke(name, cost)
    }

    fun cleanForm() {
        nameField.text = null
        hourlyCostField.text = null
        nameFieldLayout.error = null
        hourlyCostFieldLayout.error = null
    }
}