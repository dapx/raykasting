package raykasting.app

import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO

class Texture(private val inputStream: InputStream, val size: Int) {
    val pixels = IntArray(size * size)
    init { load() }

    private fun load() {
        ImageIO.read(inputStream).getRGB(
            startX = 0,
            startY = 0,
            rgbArray = pixels,
            offset = 0
        )
    }
}

private fun BufferedImage.getRGB(
    startX: Int,
    startY: Int,
    rgbArray: IntArray,
    offset: Int
): IntArray = this.getRGB(startX, startY, width, height, rgbArray, offset, width)
