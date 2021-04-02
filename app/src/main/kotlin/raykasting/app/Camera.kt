package raykasting.app

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    var x: Double = 4.5,
    var y: Double = 4.5,
    var xDirection: Double = 1.0,
    var yDirection: Double = .0,
    var xPlane: Double = .0,
    var yPlane: Double = -.66,
    private val moveSpeed: Double = .08,
    private val rotationSpeed: Double = .045
) : KeyListener {
    private var back: Boolean = false
    private var forward: Boolean = false
    private var right: Boolean = false
    private var left: Boolean = false

    override fun keyTyped(e: KeyEvent) {}

    override fun keyPressed(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> left = true
            KeyEvent.VK_RIGHT -> right = true
            KeyEvent.VK_UP -> forward = true
            KeyEvent.VK_DOWN -> back = true
        }
    }

    override fun keyReleased(e: KeyEvent) {
        when (e.keyCode) {
            KeyEvent.VK_LEFT -> left = false
            KeyEvent.VK_RIGHT -> right = false
            KeyEvent.VK_UP -> forward = false
            KeyEvent.VK_DOWN -> back = false
        }
    }

    fun update(map: Array<IntArray>) {
        if (forward) {
            if (map[(x + xDirection * moveSpeed).toInt()][y.toInt()] == 0) {
                x += xDirection * moveSpeed
            }
            if (map[x.toInt()][(y + yDirection * moveSpeed).toInt()] == 0) {
                y += yDirection * moveSpeed
            }
        }
        if (back) {
            if (map[(x - xDirection * moveSpeed).toInt()][y.toInt()] == 0) {
                x -= xDirection * moveSpeed
            }
            if (map[x.toInt()][(y - yDirection * moveSpeed).toInt()] == 0) {
                y -= yDirection * moveSpeed
            }
        }
        if (right) {
            yDirection = xDirection * sin(-rotationSpeed) + yDirection * cos(-rotationSpeed)
            xDirection = xDirection * cos(-rotationSpeed) - yDirection * sin(-rotationSpeed)
            yPlane = xPlane * sin(-rotationSpeed) + yPlane * cos(-rotationSpeed)
            xPlane = xPlane * cos(-rotationSpeed) - yPlane * sin(-rotationSpeed)
        }
        if (left) {
            yDirection = xDirection * sin(rotationSpeed) + yDirection * cos(rotationSpeed)
            xDirection = xDirection * cos(rotationSpeed) - yDirection * sin(rotationSpeed)
            yPlane = xPlane * sin(rotationSpeed) + yPlane * cos(rotationSpeed)
            xPlane = xPlane * cos(rotationSpeed) - yPlane * sin(rotationSpeed)
        }
    }
}