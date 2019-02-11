package com.erikschouten

import com.erikschouten.polygon.Point
import com.erikschouten.polygon.Polygon
import org.junit.Test

class PolygonTests {

    @Test
    fun testSimplePolygon() {

        val polygon = Polygon.Builder()
            .addVertex(Point(1, 3))
            .addVertex(Point(2, 8))
            .addVertex(Point(5, 4))
            .addVertex(Point(5, 9))
            .addVertex(Point(7, 5))
            .addVertex(Point(6, 1))
            .addVertex(Point(3, 1))
            .build()
        
        assert(polygon.contains(Point(5.5f, 7)))
        assert(!polygon.contains(Point(4.5f, 7)))
    }

    @Test
    fun testPolygonWithHoles() {
        val polygon = Polygon.Builder()
            .addVertex(Point(1, 2))
            .addVertex(Point(1, 6))
            .addVertex(Point(8, 7))
            .addVertex(Point(8, 1))
            .close()
            .addVertex(Point(2, 3))
            .addVertex(Point(5, 5))
            .addVertex(Point(6, 2))
            .close()
            .addVertex(Point(6, 6))
            .addVertex(Point(7, 6))
            .addVertex(Point(7, 5))
            .build()

        assert(polygon.contains(Point(6, 5)))
        assert(!polygon.contains(Point(4, 3)))
        assert(!polygon.contains(Point(6.5f, 5.8f)))
    }

    @Test
    fun testPolygonFigure6() {

        var polygon = Polygon.Builder()
            .addVertex(Point(1, 3))
            .addVertex(Point(9, 3))
            .addVertex(Point(9, 7))
            .addVertex(Point(7, 5))
            .addVertex(Point(5, 7))
            .addVertex(Point(3, 5))
            .addVertex(Point(1, 7))
            .addVertex(Point(1, 3))
            .build()

        assert(polygon.contains(Point(5, 5)))

        polygon = Polygon.Builder()
            .addVertex(Point(1, 3))
            .addVertex(Point(3, 5))
            .addVertex(Point(5, 3))
            .addVertex(Point(7, 5))
            .addVertex(Point(9, 3))
            .addVertex(Point(9, 7))
            .addVertex(Point(1, 7))
            .build()
        
        assert(polygon.contains(Point(5, 5)))
    }

    @Test
    fun testMapCoordinates1() {

        val polygon = Polygon.Builder()
            .addVertex(Point(42.499148, 27.485196))
            .addVertex(Point(42.498600, 27.480000))
            .addVertex(Point(42.503800, 27.474680))
            .addVertex(Point(42.510000, 27.468270))
            .addVertex(Point(42.510788, 27.466904))
            .addVertex(Point(42.512116, 27.465350))
            .addVertex(Point(42.512000, 27.467000))
            .addVertex(Point(42.513579, 27.471027))
            .addVertex(Point(42.512938, 27.472668))
            .addVertex(Point(42.511829, 27.474922))
            .addVertex(Point(42.507945, 27.480124))
            .addVertex(Point(42.509082, 27.482892))
            .addVertex(Point(42.536026, 27.490519))
            .addVertex(Point(42.534470, 27.499703))
            .addVertex(Point(42.499148, 27.485196))
            .build()

        assert(polygon.contains(Point(42.508956f, 27.483328f)))
        assert(polygon.contains(Point(42.505f, 27.48f)))
    }

    @Test
    fun testMapCoordinates2() {

        val polygon = Polygon.Builder()
            .addVertex(Point(40.481171, 6.4107070))
            .addVertex(Point(40.480248, 6.4101200))
            .addVertex(Point(40.480237, 6.4062790))
            .addVertex(Point(40.481161, 6.4062610))
            .build()

        assert(polygon.contains(Point(40.480890f, 6.4081030f)))
    }

    @Test
    fun testParallel() {

        val polygon = Polygon.Builder()
            .addVertex(Point(0, 0))
            .addVertex(Point(0, 1))
            .addVertex(Point(1, 2))
            .addVertex(Point(1, 99))
            .addVertex(Point(100, 0))
            .build()

        assert(polygon.contains(Point(3, 4)))
        assert(polygon.contains(Point(3, 4.1)))
        assert(polygon.contains(Point(3, 3.9)))
    }

    @Test
    fun testBorders() {

        var polygon = Polygon.Builder()
            .addVertex(Point(-1, -1))
            .addVertex(Point(-1, 1))
            .addVertex(Point(1, 1))
            .addVertex(Point(1, -1))
            .build()

        assert(!polygon.contains(Point(0, 1)))

        polygon = Polygon.Builder()
            .addVertex(Point(-1, -1))
            .addVertex(Point(-1, 1))
            .addVertex(Point(1, 1))
            .addVertex(Point(1, -1))
            .build()

        assert(polygon.contains(Point(-1, 0)))
    }
}
