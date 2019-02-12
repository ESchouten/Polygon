package com.erikschouten.polygon

open class Line(
    val start: Point,
    val end: Point
) {

    var a = Double.NaN
        private set

    var b = Double.NaN
        private set

    var isVertical = false
        private set

    init {
        if (this.end.x - this.start.x != 0.0) {
            a = (this.end.y - this.start.y) / (this.end.x - this.start.x)
            b = this.start.y - a * this.start.x
        } else {
            isVertical = true
        }
    }

    fun isInside(point: Point) =
        point.x in Math.min(start.x, end.x)..Math.max(start.x, end.x)
                && point.y in Math.min(start.y, end.y)..Math.max(start.y, end.y)
}