package raykasting.app

import java.awt.Color
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * This code is very complex and use a lot of mutations
 * There are some properties that I did not understand
 */
class Screen(
    private val map: Array<IntArray>,
    private val textures: List<Texture>,
    private val width: Int,
    private val height: Int
) {
    fun update(camera: Camera, pixels: IntArray) {
        clearScreen(pixels)
        for (screenXPixel in 0 until width) {
            // IDK
            val cameraX = 2 * screenXPixel / width - 1
            val rayDirection = RayDirection(
                x = camera.xDirection + camera.xPlane * cameraX,
                y = camera.yDirection + camera.yPlane * cameraX
            )
            val deltaDistance = DeltaDistance(
                x = sqrt(1 + (rayDirection.y * rayDirection.y) / (rayDirection.x * rayDirection.x)),
                y = sqrt(1 + (rayDirection.x * rayDirection.x) / (rayDirection.y * rayDirection.y))
            )
            val nextXDirection = nextXDirection(
                rayDirection = rayDirection,
                deltaDistance = deltaDistance,
                camera = camera
            )
            val nextYDirection = nextYDirection(
                rayDirection = rayDirection,
                deltaDistance = deltaDistance,
                camera = camera
            )

            // Raytracing will looking for wall
            val rayCollision = whereRayHitsAWall(camera, nextXDirection, nextYDirection, deltaDistance)

            val perpendicularWallDistance = if (rayCollision.side == 0) {
                abs((rayCollision.coordinate.x - camera.x + (1 - nextXDirection.step) / 2))
            } else abs((rayCollision.coordinate.y - camera.y + (1 - nextYDirection.step) / 2))

            val wallHeightBasedOnDistance = if (perpendicularWallDistance > 0)
                abs((height / perpendicularWallDistance).toInt())
            else height

            val startPixel = -wallHeightBasedOnDistance / 2 + height / 2
            val endPixel = wallHeightBasedOnDistance / 2 + height / 2
            val lowestPixelToPaint = if (startPixel < 0) 0 else startPixel
            val highestPixelToPaint = if (endPixel >= height) height - 1 else endPixel

            val textureTypeFromMap = map[rayCollision.coordinate.x][rayCollision.coordinate.y] - 1
            val wallHitPositionX = calculateWallX(rayCollision, camera, nextYDirection, rayDirection, nextXDirection)
            val texture = textures[textureTypeFromMap]
            val wallHitPositionXTexture = (wallHitPositionX * texture.size).toInt()

            // IDK
            val texturePixelsBasedOnHitPoint = if (
                rayCollision.side == 0 && rayDirection.x > 0
                || rayCollision.side == 1 && rayDirection.y > 0
            ) {
                texture.size - wallHitPositionXTexture - 1
            } else wallHitPositionXTexture

            for (wallYPixel in lowestPixelToPaint until highestPixelToPaint) {
                val textureY = (
                    ((wallYPixel * 2 - height + wallHeightBasedOnDistance) shl 6) / wallHeightBasedOnDistance
                ) / 2
                val color = if (rayCollision.side == 0) {
                    texture.pixels[texturePixelsBasedOnHitPoint + (textureY * texture.size)]
                } else {
                    val darkColorCode = 8355711
                    (texture.pixels[texturePixelsBasedOnHitPoint + (textureY * texture.size)] shr 1) and
                        darkColorCode
                }
                pixels[screenXPixel + wallYPixel * width] = color
            }
        }
    }

    private fun calculateWallX(
        rayCollision: RayCollision,
        camera: Camera,
        nextYDirection: NextDirection,
        rayDirection: RayDirection,
        nextXDirection: NextDirection
    ): Double {
        val wallX = if (rayCollision.side == 1) {
            camera.x + (
                    (rayCollision.coordinate.y - camera.y + (1 - nextYDirection.step) / 2) / rayDirection.y
                    ) * rayDirection.x
        } else {
            camera.y + (
                    (rayCollision.coordinate.x - camera.x + (1 - nextXDirection.step) / 2) / rayDirection.x
                    ) * rayDirection.y
        }
        return wallX - floor(wallX)
    }

    private fun whereRayHitsAWall(
        camera: Camera,
        nextXDirection: NextDirection,
        nextYDirection: NextDirection,
        deltaDistance: DeltaDistance
    ): RayCollision {
        var hit = false
        var mapX = camera.x.toInt()
        var mapY = camera.x.toInt()
        // Could be a boolean, but IDK what it means
        var side = 0
        while (!hit) {
            if (nextXDirection.distance < nextYDirection.distance) {
                nextXDirection.distance += deltaDistance.x
                mapX += nextXDirection.step
                side = 0
            } else {
                nextYDirection.distance += deltaDistance.y
                mapY += nextYDirection.step
                side = 1
            }
            hit = map[mapX][mapY] > 0
        }
        return RayCollision(side = side, coordinate = mapX to mapY)
    }

    private fun nextXDirection(rayDirection: RayDirection, deltaDistance: DeltaDistance, camera: Camera): NextDirection {
        return if (rayDirection.x < 0) NextDirection(
            step = -1,
            distance = (camera.x - camera.x.toInt()) * deltaDistance.x
        )
        else NextDirection(
            step = 1,
            distance = (camera.x.toInt() + 1 - camera.x) * deltaDistance.x
        )
    }

    private fun nextYDirection(rayDirection: RayDirection, deltaDistance: DeltaDistance, camera: Camera): NextDirection {
        return if (rayDirection.y < 0) NextDirection(
            step = -1,
            distance = (camera.y - camera.y.toInt()) * deltaDistance.y
        )
        else NextDirection(
            step = 1,
            distance = (camera.y.toInt() + 1 - camera.y) * deltaDistance.y
        )
    }

    private fun clearScreen(pixels: IntArray) {
        val pixelsEndOfX = pixels.size / 2
        for (pixel in 0 until pixelsEndOfX) {
            if (pixels[pixel] != Color.darkGray.rgb) pixels[pixel] = Color.darkGray.rgb
        }
        for (pixel in pixelsEndOfX until pixels.size) {
            if (pixels[pixel] != Color.gray.rgb) pixels[pixel] = Color.gray.rgb
        }
    }

    private data class NextDirection(var step: Int, var distance: Double)
    private data class RayDirection(val x: Double, val y: Double)
    private data class DeltaDistance(val x: Double, val y: Double)
    private data class RayCollision(val side: Int, val coordinate: Pair<Int, Int>)
    private val Pair<Int, Int>.x get() = first
    private val Pair<Int, Int>.y get() = second
}