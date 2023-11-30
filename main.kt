import java.math.BigInteger
import java.util.*

class Board {
    companion object {
        @JvmStatic
        val width = 7
        val height = 11
        val leftWall = BitSet(width * height)
        val rightWall = BitSet(width * height)

        init {
            for (i in 0 until height) {
                leftWall.set(i * width)
                if (i % 2 == 0) {
                    rightWall.set(i * width + width - 2) // even rows have 1 less pixel
                } else {
                    rightWall.set(i * width + width - 1)
                }
            }
        }
    }

    val bitboards: Array<BitSet> = Array(Color.values().size) { BitSet(width * height) }

    fun getNeighbor(pixel: Int, direction: Direction): Color? {
        // xxxxxx-
        // xxxxxxx
        // xxxxxx-
        // xxxxxxx
        // xxxxxx-
        // xxxxxxx
        // xxxxxx-
        // xxxxxxx
        // xxxxxx-
        // xxxxxxx
        // xxxxxx-

        // - is empty
        // x is a valid slot

        // bitmasks goes bottom left to top right

        when (direction) {
            Direction.topLeft -> {
                val row = pixel / width;
                if (row == height - 1) {
                    return null; // at ceiling
                }
                if (row % 2 == 0) {
                    // 6 length
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel + width)) {
                            return Color.values()[i]
                        }
                    }
                } else {
                    // 7 length
                    if (pixel % width == 0) {
                        return null // 7 wide, left wall don't have left corners
                    }
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel + width - 1)) {
                            return Color.values()[i]
                        }
                    }
                }
                return null
            }
            Direction.topRight -> {
                val row = pixel / width;
                if (row == height - 1) {
                    return null; // at ceiling
                }
                if (row % 2 == 0) {
                    // 6 length
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel + width)) {
                            return Color.values()[i + 1]
                        }
                    }
                } else {
                    // 7 length
                    if (pixel % width == width - 1) {
                        return null // 7 wide, right wall don't have right corners
                    }
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel + width)) {
                            return Color.values()[i]
                        }
                    }
                }
                return null
            }
            Direction.right -> {
                if (leftWall.get(pixel)) {
                    // if it is, then it has no left neighbor
                    return null;
                }
                // otherwise, return the pixel to the left
                for (i in 0 until Color.values().size) {
                    if (bitboards[i].get(pixel + 1)) {
                        return Color.values()[i]
                    }
                }
                return null
            }
            Direction.bottomRight -> {
                val row = pixel / width;
                if (row == 0) {
                    return null; // at floor
                }
                if (row % 2 == 0) {
                    // 6 length
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel - width + 1)) {
                            return Color.values()[i]
                        }
                    }
                } else {
                    // 7 length
                    if (pixel % width == width - 1) {
                        return null // 7 wide, right wall don't have right corners
                    }
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel - width)) {
                            return Color.values()[i]
                        }
                    }
                }
                return null
            }
            Direction.bottomLeft -> {
                val row = pixel / width;
                if (row == 0) {
                    return null; // at floor
                }
                if (row % 2 == 0) {
                    // 6 length
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel - width)) {
                            return Color.values()[i]
                        }
                    }
                } else {
                    // 7 length
                    if (pixel % width == 0) {
                        return null // 7 wide, left wall don't have left corners
                    }
                    for (i in 0 until Color.values().size) {
                        if (bitboards[i].get(pixel - width - 1)) {
                            return Color.values()[i]
                        }
                    }
                }
                return null
            }
            Direction.left -> {
                // check if pixel is on the left wall
                if (rightWall.get(pixel)) {
                    // if it is, then it has no left neighbor
                    return null;
                }
                // otherwise, return the pixel to the left
                for (i in 0 until Color.values().size) {
                    if (bitboards[i].get(pixel - 1)) {
                        return Color.values()[i]
                    }
                }
                return null
            }
        }
    }

    fun loadBoard(str: String) {
        var index = 0;
        for (char in str) {
            var row = index / width;
            if (row % 2 == 0 && index % width == width - 1) {
                index++;
                row++;
            }
            when (char) {
                ' ' -> {
                    // empty slot
                }
                'w' -> {
                    bitboards[Color.WHITE.int].set(index)
                }
                'y' -> {
                    bitboards[Color.YELLOW.int].set(index)
                }
                'g' -> {
                    bitboards[Color.GREEN.int].set(index)
                }
                'p' -> {
                    bitboards[Color.PURPLE.int].set(index)
                }
                'c' -> {
                    bitboards[Color.COLORED.int].set(index)
                }
                '/' -> {
                    // new line
                    index = (row + 1) * width - 1
                }
                else -> {
                    println("Invalid character $char")
                    return
                }
            }
            index++;
        }
    }

    fun print() {
        val colors = Color.values().size
        for (row in (height - 1) downTo 0) {
            columns@ for (column in 0..<(if (row % 2 == 0) width - 1 else width)) {
                if (column == 0) {
                    if (row != height - 1) {
                        println()
                    }
                    if (row % 2 == 0) {
                        print(" ")
                    }
                }
                val index = row * width + column
                for (color in 1..<colors) {
                    if (bitboards[color].get(index)) {
                        print("${(Color from color)?.code()}X ")
                        continue@columns;
                    }
                }
                print("${Color.EMPTY.code()}- ")
            }
        }
        println()
    }
}

enum class Color(val int: Int) {
    EMPTY(0),
    WHITE(1),
    YELLOW(2),
    GREEN(3),
    PURPLE(4),
    COLORED(5);

    companion object {
        private val map = Color.values().associateBy { it.int }
        infix fun from(value: Int) = map[value]
    }

    fun code(): String {
        return when (this) {
            EMPTY -> "\u001B[0m"
            WHITE -> "\u001B[38;2;255;255;255m"
            YELLOW -> "\u001B[38;2;255;196;96m"
            GREEN -> "\u001B[38;2;96;255;96m"
            PURPLE -> "\u001B[38;2;255;96;196m"
            COLORED -> "\u001B[38;2;0;255;255m"
        }
    }
}

enum class Direction(val int: Int) {
    topLeft(0),
    topRight(1),
    right(2),
    bottomRight(3),
    bottomLeft(4),
    left(5);

    companion object {
        private val map = Direction.values().associateBy { it.int }
        infix fun from(value: Int) = map[value]
    }
}

fun main() {
    val board = Board()
    board.loadBoard("yp/wg/p")
    board.print()
    for (i in 0..<board.bitboards.size) {
        println("${Color from i} - ${board.bitboards[i]}")
    }
    val right = board.getNeighbor(board.bitboards[Color.PURPLE.int].nextSetBit(0), Direction.bottomLeft);
    print(right)
}