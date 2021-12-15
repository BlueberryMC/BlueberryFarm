package net.blueberrymc.blueberryFarm.util

object Util {
    fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder()
        for (b in bytes) {
            val hex = Integer.toHexString(0xFF and b.toInt())
            if (hex.length == 1) hexString.append('0')
            hexString.append(hex)
        }
        return hexString.toString()
    }

    fun hexToBytes(s: String): ByteArray {
        require(s.length % 2 == 0) { "Hex $s must be divisible by two" }
        val bytes = ByteArray(s.length / 2)
        for (i in bytes.indices) {
            val left = s[i * 2]
            val right = s[i * 2 + 1]
            val b = (getValue(left) shl 4 or (getValue(right) and 0xF)).toByte()
            bytes[i] = b
        }
        return bytes
    }

    private fun getValue(c: Char): Int {
        val i = Character.digit(c, 16)
        require(i >= 0) { "Invalid hex char: $c" }
        return i
    }

    fun String.stripSnapshot() = this.replace("-SNAPSHOT", "")
}
