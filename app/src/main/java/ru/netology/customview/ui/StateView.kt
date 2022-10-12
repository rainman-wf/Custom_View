package ru.netology.customview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
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
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var hasEmpty: Boolean = false
    private var emptyValue: Float = 0f
    var maxValue: Float = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.StateView) {
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
        color = colors[0]
    }

    var data: List<Float> = emptyList()
        set(value) {
            if (value.sum() > maxValue) maxValue = value.sum()
            hasEmpty = maxValue > value.sum()
            val mData = mutableListOf<Float>()
            mData.addAll(value)
            if (hasEmpty) {
                emptyValue = maxValue - value.sum()
                mData.add(emptyValue)
            }
            field = mData
            update()
        }


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

        var startFrom = 0f

        data.forEachIndexed { index, datum ->

            val angle = datum * 360 / maxValue
            
            paint.color =
                if (hasEmpty && index == data.lastIndex) emptyColor
                else colors.getOrElse(index) { generateRandomColor() }

            canvas.drawArc(oval, startFrom + progress, angle * (progress + 90) / 360, false, paint)

            canvas.drawText(
                "%.2f%%".format((data.sum() - emptyValue) / maxValue * 100 * (progress + 90) / 360),
                center.x, center.y + textPaint.textSize / 4, textPaint)

            startFrom += angle
        }

        if (progress == 270f) canvas.drawCircle(center.x + 1, center.y - radius, lineWeight.toFloat() / 2, dotPaint)

    }


    private fun generateRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    private fun update() {
        valueAnimator?.let {
            it.removeAllUpdateListeners()
            it.cancel()
        }
        progress = -90F
        valueAnimator = ValueAnimator.ofFloat(-90f, 270f).apply {
            addUpdateListener {
                progress = it.animatedValue as Float
                invalidate()
            }
            duration = 2000
            interpolator = LinearInterpolator()
            start()
        }
    }
}