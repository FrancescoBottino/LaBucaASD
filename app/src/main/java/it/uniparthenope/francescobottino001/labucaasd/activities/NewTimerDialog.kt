package it.uniparthenope.francescobottino001.labucaasd.activities

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import it.uniparthenope.francescobottino001.labucaasd.R
import kotlinx.android.synthetic.main.new_timer_dialog.*
import java.lang.Exception

class NewTimerDialog(ctx: Context, callback: (String, Double)->Unit): AlertDialog(ctx) {

    private val root = LayoutInflater.from(ctx).inflate(R.layout.new_timer_dialog, null, true)

    private val title: TextView = root.findViewById(R.id.alert_title)
    private val nameField: EditText = root.findViewById(R.id.name_field)
    private val hourlyCostField: EditText = root.findViewById(R.id.hourly_cost_field)
    private val positiveButton: Button = root.findViewById(R.id.positive_button)
    private val negativeButton: Button = root.findViewById(R.id.negative_button)

    init {
        setView(root)

        title.text = this.context.resources.getString(R.string.new_timer_dialog_title)

        positiveButton.text = this.context.resources.getString(R.string.new_timer_dialog_positive_button)
        negativeButton.text = this.context.resources.getString(R.string.new_timer_dialog_negative_button)

        positiveButton.setOnClickListener {
            val name = nameField.text.toString()
            if( name.trim().length < 3) {
                nameField.error = this.context.resources.getString(R.string.new_timer_dialog_empty_name_error)
                return@setOnClickListener
            }

            val cost= try {
                hourlyCostField.text.toString().toDouble()
            } catch (e: Exception) {
                hourlyCostField.error = this.context.resources.getString(R.string.new_timer_dialog_wrong_cost_error)
                return@setOnClickListener
            }

            callback.invoke(name, cost)
            dismiss()
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
    }
}