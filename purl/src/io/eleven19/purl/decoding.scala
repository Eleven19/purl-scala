package io.eleven19.purl

private[purl] object Decoding {
  def percentDecode(input: String): String =
    input match {
      case null => input
      case _ =>
        val decoded = uriDecode(input)
        if (!decoded.equals(input)) {
          decoded
        } else {
          input
        }
    }

  def uriDecode(source: String): String =
    source match {
      case null => source
      case _ =>
        val length = source.length
        val sb     = new StringBuilder(length)
        var i      = 0
        while (i < length) {
          val c = source.charAt(i)
          if (c == '%') {
            if (i + 2 < length) {
              val hex  = source.substring(i + 1, i + 3)
              val char = Integer.parseInt(hex, 16).toChar
              sb.append(char)
              i += 2
            } else {
              sb.append(c)
            }
          } else {
            sb.append(c)
          }
        }
        sb.toString()
    }
}
