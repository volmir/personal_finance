package com.example

import com.example.data.model.TrajectoryPoint
import com.example.ui.components.interpolateAccumulatedCapital
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FireTrajectoryChartTest {
    private fun point(age: Double, capital: Double) = TrajectoryPoint(
        age = age,
        yearOffset = age - 50.0,
        accumulatedCapital = capital,
        requiredCapital = 0.0,
        isRetired = false
    )

    @Test
    fun `fractional retirement age interpolates within neighboring ages`() {
        val trajectory = listOf(point(50.0, 100.0), point(52.0, 500.0), point(55.0, 200.0))
        assertEquals(200.0, interpolateAccumulatedCapital(trajectory, 50.5)!!, 0.000001)
        assertEquals(400.0, interpolateAccumulatedCapital(trajectory, 51.5)!!, 0.000001)
        assertEquals(350.0, interpolateAccumulatedCapital(trajectory, 53.5)!!, 0.000001)
    }

    @Test
    fun `exact ages and ages outside trajectory use endpoint capital`() {
        val trajectory = listOf(point(50.0, 100.0), point(52.0, 500.0), point(55.0, 200.0))
        assertEquals(100.0, interpolateAccumulatedCapital(trajectory, 50.0)!!, 0.0)
        assertEquals(500.0, interpolateAccumulatedCapital(trajectory, 52.0)!!, 0.0)
        assertEquals(200.0, interpolateAccumulatedCapital(trajectory, 55.0)!!, 0.0)
        assertEquals(100.0, interpolateAccumulatedCapital(trajectory, 49.0)!!, 0.0)
        assertEquals(200.0, interpolateAccumulatedCapital(trajectory, 56.0)!!, 0.0)
    }

    @Test
    fun `empty or single point trajectory does not divide by zero`() {
        assertNull(interpolateAccumulatedCapital(emptyList(), 50.0))
        val single = listOf(point(50.0, 0.0))
        for (age in listOf(49.0, 50.0, 51.0)) {
            assertEquals(0.0, interpolateAccumulatedCapital(single, age)!!, 0.0)
        }
    }
}
