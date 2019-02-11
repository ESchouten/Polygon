package com.erikschouten.polygon

import java.awt.geom.AffineTransform
import java.awt.geom.PathIterator
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import sun.awt.geom.Crossings
import java.awt.Point
import java.awt.Rectangle
import java.awt.Shape
import java.io.Serializable
import java.util.Arrays

private const val MIN_LENGTH = 4

class Polygon(var xpoints: IntArray,
              var ypoints: IntArray,
              var npoints: Int = 0) : Shape, Serializable {

    private var bounds: Rectangle? = null

    constructor(): this(IntArray(MIN_LENGTH), IntArray(MIN_LENGTH))

    init {
        if (npoints > xpoints.size || npoints > ypoints.size) {
            throw IndexOutOfBoundsException("npoints > xpoints.length || " + "npoints > ypoints.length")
        }

        if (npoints < 0) {
            throw NegativeArraySizeException("npoints < 0")
        }
    }

    private fun calculateBounds(xpoints: IntArray, ypoints: IntArray, npoints: Int) {
        var boundsMinX = Integer.MAX_VALUE
        var boundsMinY = Integer.MAX_VALUE
        var boundsMaxX = Integer.MIN_VALUE
        var boundsMaxY = Integer.MIN_VALUE

        for (i in 0 until npoints) {
            val x = xpoints[i]
            boundsMinX = Math.min(boundsMinX, x)
            boundsMaxX = Math.max(boundsMaxX, x)
            val y = ypoints[i]
            boundsMinY = Math.min(boundsMinY, y)
            boundsMaxY = Math.max(boundsMaxY, y)
        }
        bounds = Rectangle(
            boundsMinX, boundsMinY,
            boundsMaxX - boundsMinX,
            boundsMaxY - boundsMinY
        )
    }

    private fun updateBounds(x: Int, y: Int) {
        if (x < bounds!!.x) {
            bounds!!.width = bounds!!.width + (bounds!!.x - x)
            bounds!!.x = x
        } else {
            bounds!!.width = Math.max(bounds!!.width, x - bounds!!.x)
        }

        if (y < bounds!!.y) {
            bounds!!.height = bounds!!.height + (bounds!!.y - y)
            bounds!!.y = y
        } else {
            bounds!!.height = Math.max(bounds!!.height, y - bounds!!.y)
        }
    }

    fun addPoint(x: Int, y: Int) {
        if (npoints >= xpoints.size || npoints >= ypoints.size) {
            var newLength = npoints * 2

            if (newLength < MIN_LENGTH) {
                newLength = MIN_LENGTH
            } else if (newLength and newLength - 1 != 0) {
                newLength = Integer.highestOneBit(newLength)
            }

            xpoints = Arrays.copyOf(xpoints, newLength)
            ypoints = Arrays.copyOf(ypoints, newLength)
        }
        xpoints[npoints] = x
        ypoints[npoints] = y
        npoints++
        if (bounds != null) {
            updateBounds(x, y)
        }
    }

    override fun getBounds(): Rectangle {
        if (npoints == 0) {
            return Rectangle()
        }
        if (bounds == null) {
            calculateBounds(xpoints, ypoints, npoints)
        }
        return bounds!!.bounds
    }

    operator fun contains(p: Point): Boolean {
        return contains(p.x, p.y)
    }

    fun contains(x: Int, y: Int): Boolean {
        return contains(x.toDouble(), y.toDouble())
    }

    override fun getBounds2D(): Rectangle2D {
        return getBounds()
    }

    override fun contains(x: Double, y: Double): Boolean {
        if (npoints <= 2 || !getBounds().contains(x, y)) {
            return false
        }
        var hits = 0

        var lastx = xpoints[npoints - 1]
        var lasty = ypoints[npoints - 1]
        var curx: Int
        var cury: Int

        var i = 0
        while (i < npoints) {
            curx = xpoints[i]
            cury = ypoints[i]

            if (cury == lasty) {
                lastx = curx
                lasty = cury
                i++
                continue
            }

            val leftx: Int
            if (curx < lastx) {
                if (x >= lastx) {
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                leftx = curx
            } else {
                if (x >= curx) {
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                leftx = lastx
            }

            val test1: Double
            val test2: Double
            if (cury < lasty) {
                if (y < cury || y >= lasty) {
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                if (x < leftx) {
                    hits++
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                test1 = x - curx
                test2 = y - cury
            } else {
                if (y < lasty || y >= cury) {
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                if (x < leftx) {
                    hits++
                    lastx = curx
                    lasty = cury
                    i++
                    continue
                }
                test1 = x - lastx
                test2 = y - lasty
            }

            if (test1 < test2 / (lasty - cury) * (lastx - curx)) {
                hits++
            }
            lastx = curx
            lasty = cury
            i++
        }

        return hits and 1 != 0
    }

    private fun getCrossings(
        xlo: Double, ylo: Double,
        xhi: Double, yhi: Double
    ): Crossings? {
        val cross = Crossings.EvenOdd(xlo, ylo, xhi, yhi)
        var lastx = xpoints[npoints - 1]
        var lasty = ypoints[npoints - 1]
        var curx: Int
        var cury: Int

        // Walk the edges of the polygon
        for (i in 0 until npoints) {
            curx = xpoints[i]
            cury = ypoints[i]
            if (cross.accumulateLine(lastx.toDouble(), lasty.toDouble(), curx.toDouble(), cury.toDouble())) {
                return null
            }
            lastx = curx
            lasty = cury
        }

        return cross
    }

    override fun contains(p: Point2D): Boolean {
        return contains(p.x, p.y)
    }

    override fun intersects(x: Double, y: Double, w: Double, h: Double): Boolean {
        if (npoints <= 0 || !getBounds().intersects(x, y, w, h)) {
            return false
        }

        val cross = getCrossings(x, y, x + w, y + h)
        return cross == null || !cross.isEmpty
    }

    override fun intersects(r: Rectangle2D): Boolean {
        return intersects(r.x, r.y, r.width, r.height)
    }

    override fun contains(x: Double, y: Double, w: Double, h: Double): Boolean {
        if (npoints <= 0 || !getBounds().intersects(x, y, w, h)) {
            return false
        }

        val cross = getCrossings(x, y, x + w, y + h)
        return cross != null && cross.covers(y, y + h)
    }

    override fun contains(r: Rectangle2D): Boolean {
        return contains(r.x, r.y, r.width, r.height)
    }

    override fun getPathIterator(at: AffineTransform): PathIterator {
        return PolygonPathIterator(this, at)
    }

    override fun getPathIterator(at: AffineTransform, flatness: Double): PathIterator {
        return getPathIterator(at)
    }

    internal inner class PolygonPathIterator(var poly: Polygon, var transform: AffineTransform?) : PathIterator {
        var index: Int = 0

        init {
            if (poly.npoints == 0) {
                index = 1
            }
        }

        override fun getWindingRule(): Int {
            return PathIterator.WIND_EVEN_ODD
        }

        override fun isDone(): Boolean {
            return index > poly.npoints
        }

        override fun next() {
            index++
        }

        override fun currentSegment(coords: FloatArray): Int {
            if (index >= poly.npoints) {
                return PathIterator.SEG_CLOSE
            }
            coords[0] = poly.xpoints[index].toFloat()
            coords[1] = poly.ypoints[index].toFloat()
            if (transform != null) {
                transform!!.transform(coords, 0, coords, 0, 1)
            }
            return if (index == 0) PathIterator.SEG_MOVETO else PathIterator.SEG_LINETO
        }

        override fun currentSegment(coords: DoubleArray): Int {
            if (index >= poly.npoints) {
                return PathIterator.SEG_CLOSE
            }
            coords[0] = poly.xpoints[index].toDouble()
            coords[1] = poly.ypoints[index].toDouble()
            if (transform != null) {
                transform!!.transform(coords, 0, coords, 0, 1)
            }
            return if (index == 0) PathIterator.SEG_MOVETO else PathIterator.SEG_LINETO
        }
    }
}
