package it.uniparthenope.francescobottino001.labucaasd.activities

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import it.uniparthenope.francescobottino001.labucaasd.R
import it.uniparthenope.francescobottino001.labucaasd.persistence.TimerData
import kotlinx.android.synthetic.main.timer_form_dialog.*
import java.lang.Exception

class DeleteTimerDialog(ctx: Context, callback: ()->Unit): AlertDialog(ctx) {

    private val root = LayoutInflater.from(ctx).inflate(R.layout.timer_form_dialog, null, true)

    private val title: TextView = root.findViewById(R.id.alert_title)
    private val nameField: TextInputLayout = root.findViewById(R.id.name_field_layout)
    private val hourlyCostField: TextInputLayout = root.findViewById(R.id.hourly_cost_field_layout)
    private val positiveButton: Button = root.findViewById(R.id.positive_button)
    private val negativeButton: Button = root.findViewById(R.id.negative_button)

    init {
        setView(root)

        title.text = this.context.resources.getString(R.string.delete_timer_dialog_title)

        positiveButton.text = this.context.resources.getString(R.string.delete_timer_dialog_positive_button)
        negativeButton.text = this.context.resources.getString(R.string.timer_dialog_negative_button)

        nameField.visibility = View.GONE
        hourlyCostField.visibility = View.GONE

        positiveButton.setOnClickListener {
            callback.invoke()
            dismiss()
        }
        negativeButton.setOnClickListener {
            dismiss()
        }
    }
}