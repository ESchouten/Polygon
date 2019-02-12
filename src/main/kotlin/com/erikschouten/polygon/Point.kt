package com.erikschouten.polygon

open class Point(
    var x: Double,
    var y: Double
) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
}