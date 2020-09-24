package it.uniparthenope.francescobottino001.labucaasd.activities.main.timers_list

import com.skydoves.transformationlayout.TransformationLayout

class FormView(
    val timerForm: TimerForm, val overlay: Overlay
) {

    var currentTransformation: TransformationLayout? = null
    var isShowing: Boolean = false

    init {
        overlay.setOnClickListener {
            hide()
        }
    }

    fun show() {
        currentTransformation?.let {
            it.bindTargetView(timerForm)
            it.startTransform()
            overlay.fadeIn(it.duration)

            isShowing = true
        } ?: throw Exception()
    }

    fun hide() {
        currentTransformation?.let {
            it.finishTransform()
            overlay.fadeOut(it.duration)
            timerForm.cleanForm()

            isShowing = false
        } ?: throw Exception()
        currentTransformation = null
    }
}