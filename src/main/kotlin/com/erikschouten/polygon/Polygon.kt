package com.erikschouten.polygon

import java.util.*

class Polygon private constructor(
    val sides: List<Line>,
    private val boundingBox: BoundingBox
) {

    constructor(sides: List<Line>): this(sides, BoundingBox())

    init {
        sides.forEach {
            boundingBox.updateBoundingBox(it.start)
            boundingBox.updateBoundingBox(it.end)
        }
    }

    fun contains(x: Number, y: Number) = contains(Point(x, y))

    operator fun contains(point: Point): Boolean {
        if (inBoundingBox(point)) {
            val ray = createRay(point)
            var intersection = 0
            for (side in sides) {
                if (intersect(ray, side)) {
                    intersection++
                }
            }

            if (intersection % 2 != 0) {
                return true
            }
        }
        return false
    }

    private fun intersect(ray: Line, side: Line): Boolean {
        val intersectPoint: Point

        if (!ray.isVertical && !side.isVertical) {
            if (ray.a - side.a == 0.0) {
                return false
            }

            val x = (side.b - ray.b) / (ray.a - side.a)
            val y = side.a * x + side.b
            intersectPoint = Point(x, y)
        } else if (ray.isVertical && !side.isVertical) {
            val x = ray.start.x
            val y = side.a * x + side.b
            intersectPoint = Point(x, y)
        } else if (!ray.isVertical && side.isVertical) {
            val x = side.start.x
            val y = ray.a * x + ray.b
            intersectPoint = Point(x, y)
        } else {
            return false
        }

        return side.isInside(intersectPoint) && ray.isInside(intersectPoint)
    }

    private fun createRay(point: Point): Line {
        val epsilon = (boundingBox.xMax - boundingBox.xMin) / 10e6
        val outsidePoint = Point(boundingBox.xMin - epsilon, boundingBox.yMin)

        return Line(outsidePoint, point)
    }

    private fun inBoundingBox(point: Point) =
        point.x in boundingBox.xMin..boundingBox.xMax && point.y in boundingBox.yMin..boundingBox.yMax

    class Builder {
        private var vertexes: MutableList<Point> = ArrayList()
        private val sides = ArrayList<Line>()
        private var boundingBox: BoundingBox = BoundingBox()

        private var isClosed = false

        fun addVertex(x: Number, y: Number) = addVertex(Point(x, y))

        fun addVertex(point: Point): Builder {
            if (isClosed) {
                vertexes = ArrayList()
                isClosed = false
            }

            boundingBox.updateBoundingBox(point)
            vertexes.add(point)

            if (vertexes.size > 1) {
                val line = Line(vertexes[vertexes.size - 2], point)
                sides.add(line)
            }

            return this
        }

        fun close(): Builder {
            validate()

            sides.add(Line(vertexes[vertexes.size - 1], vertexes[0]))
            isClosed = true

            return this
        }

        fun build(): Polygon {
            validate()

            if (!isClosed) {
                sides.add(Line(vertexes[vertexes.size - 1], vertexes[0]))
            }

            return Polygon(sides, boundingBox)
        }

        private fun validate() {
            if (vertexes.size < 3) {
                throw RuntimeException("Polygon must have at least 3 points")
            }
        }
    }

    private class BoundingBox {
        var xMax = java.lang.Double.NEGATIVE_INFINITY
        var xMin = java.lang.Double.POSITIVE_INFINITY
        var yMax = java.lang.Double.NEGATIVE_INFINITY
        var yMin = java.lang.Double.POSITIVE_INFINITY

        fun updateBoundingBox(point: Point) {
            xMin = Math.min(xMin, point.x)
            xMax = Math.max(xMax, point.x)
            yMin = Math.min(yMin, point.y)
            yMax = Math.max(yMax, point.y)
        }
    }
}