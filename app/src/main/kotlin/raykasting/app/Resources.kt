package raykasting.app

import java.io.InputStream

object Resources {
    fun readAsStream(name: String): InputStream {
        val classLoader = this.javaClass.classLoader
        return checkNotNull(classLoader.getResourceAsStream(name)) {
            "File $name not found in resources"
        }
    }
}