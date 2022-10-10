package ru.netology.customview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.customview.R
import ru.netology.customview.utils.db
import kotlin.math.min
import kotlin.random.Random

class StateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var textSize = db(context, 20F).toFloat()
    private var lineWeight = db(context, 5F)
    private var colors = listOf<Int>()
    private var emptyColor: Int = 0

    init {
        context.withStyledAttributes(
            attrs, R.styleable.StateView
        ) {
            textSize = getDimension(R.styleable.StateView_textSize, textSize)
            lineWeight = getDimension(R.styleable.StateView_lineWidth, lineWeight.toFloat()).toInt()

            emptyColor = getColor(R.styleable.StateView_emptyColor, generateRandomColor())

            colors = listOf(
                getColor(R.styleable.StateView_color1, generateRandomColor()),
                getColor(R.styleable.StateView_color2, generateRandomColor()),
                getColor(R.styleable.StateView_color3, generateRandomColor()),
                getColor(R.styleable.StateView_color4, generateRandomColor())
            )
        }
    }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = lineWeight.toFloat()
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = this@StateView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    var maxValue: Float = 100F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWeight.toFloat()
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return

        if (data.sum() > maxValue) {
            Log.e("StateView", "Invalid args! Sum is over of max value")
            return
        }

        var startFrom = -90f

        data.forEachIndexed { index, datum ->
            val angle = 360F * datum / maxValue
            paint.color = colors.getOrElse(index) { generateRandomColor() }
            canvas.drawArc(oval, startFrom, angle, false, paint)
            startFrom += angle
        }

        if (maxValue > data.sum()) {
            paint.color = emptyColor
            canvas.drawArc(oval, startFrom, 360F * (maxValue - data.sum()) / maxValue, false, paint)
        }

        dotPaint.color = colors[0]
        canvas.drawCircle(center.x + 1F, center.y - radius, lineWeight.toFloat() / 2, dotPaint)

        canvas.drawText(
            "%.2f%%".format(data.sum() / maxValue * 100),
            center.x, center.y + textPaint.textSize / 4, textPaint
        )
    }

    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}