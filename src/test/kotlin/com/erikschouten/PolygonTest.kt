package com.erikschouten

import com.erikschouten.polygon.Polygon
import org.junit.Test

class PolygonTest {

    @Test
    fun polygonTestValid() {
        val polygon = Polygon()
        polygon.addPoint(1, 1)
        polygon.addPoint(1, 4)
        polygon.addPoint(4, 4)
        polygon.addPoint(4, 1)

        assert(polygon.contains(1, 1))
    }

    @Test
    fun polygonTestInvalid() {
        val polygon = Polygon()
        polygon.addPoint(1, 1)
        polygon.addPoint(1, 4)
        polygon.addPoint(4, 4)
        polygon.addPoint(4, 1)

        assert(!polygon.contains(0, 0))
    }
}
