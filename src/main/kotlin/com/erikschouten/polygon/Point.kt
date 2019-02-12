package com.erikschouten.polygon

data class Point(
    var x: Double,
    var y: Double
) {

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
}