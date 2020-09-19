package it.uniparthenope.francescobottino001.labucaasd.activities

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import it.uniparthenope.francescobottino001.labucaasd.R

class NewTimerDialog(ctx: Context, callback: (String, Double)->Unit): AlertDialog(ctx) {

    private val root = LayoutInflater.from(ctx).inflate(R.layout.timer_form_dialog, null, true)

    private val title: TextView = root.findViewById(R.id.alert_title)
    private val nameFieldLayout: TextInputLayout = root.findViewById(R.id.name_field_layout)
    private val hourlyCostFieldLayout: TextInputLayout = root.findViewById(R.id.hourly_cost_field_layout)
    private val nameField: EditText = root.findViewById(R.id.name_field)
    private val hourlyCostField: EditText = root.findViewById(R.id.hourly_cost_field)
    private val positiveButton: Button = root.findViewById(R.id.positive_button)
    private val negativeButton: Button = root.findViewById(R.id.negative_button)

    init {
        setView(root)

        title.text = this.context.resources.getString(R.string.new_timer_dialog_title)

        positiveButton.text = this.context.resources.getString(R.string.new_timer_dialog_positive_button)
        negativeButton.text = this.context.resources.getString(R.string.timer_dialog_negative_button)

        positiveButton.setOnClickListener {
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

            if(errors) return@setOnClickListener

            callback.invoke(name, cost)
            dismiss()
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
    }
}