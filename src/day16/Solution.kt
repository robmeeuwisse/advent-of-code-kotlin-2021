package day16

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import readInput

/**
 * [Day 16: Packet Decoder](https://adventofcode.com/2021/day/16)
 */
class PacketDecoder {

    @Test
    fun part1() {
        assertEquals(16, part1("8A004A801A8002F478"))
        assertEquals(12, part1("620080001611562C8802118E34"))
        assertEquals(23, part1("C0015000016115A2E0802F182340"))
        assertEquals(31, part1("A0016C880162017C3686B18A3D4780"))
        assertEquals(866, part1(readInput("input.txt").single()))
    }

    @Test
    fun part2() {
        assertEquals(3, "C200B40A82".asHexIterator().readHexPacket().value)
        assertEquals(54, "04005AC33890".asHexIterator().readHexPacket().value)
        assertEquals(7, "880086C3E88112".asHexIterator().readHexPacket().value)
        assertEquals(9, "CE00C43D881120".asHexIterator().readHexPacket().value)
        assertEquals(1, "D8005AC2A8F0".asHexIterator().readHexPacket().value)
        assertEquals(0, "F600BC2D8F".asHexIterator().readHexPacket().value)
        assertEquals(0, "9C005AC2F8F0".asHexIterator().readHexPacket().value)
        assertEquals(1, "9C0141080250320F1802104A08".asHexIterator().readHexPacket().value)
        assertEquals(1392637195518, part2(readInput("input.txt").single()))
    }
}

private fun part1(input: String): Int {
    val packet = input.asHexIterator().readHexPacket()
    val result = packet.versionSum()
    println("Part 1 answer: $result")
    return result
}

private fun part2(input: String): Long {
    val packet = input.asHexIterator().readHexPacket()
    val result = packet.value
    println("Part 2 answer: $result")
    return result
}

class PacketDecoderTests {

    @Test
    fun `Input is uppercase hexadecimal`() {
        readInput("input.txt").single().forEachIndexed { index, char ->
            assertTrue("Non-uppercase hexadecimal char $char at $index", char in "0123456789ABCDEF")
        }
    }

    @Test
    fun `As binary sequence`() {
        assertEquals("110100101111111000101000", "D2FE28".asHexIterator().asBits().asSequence().joinToString(""))
    }

    @Test
    fun `Parse example 0 with excess hex digits`() {
        val hex = "D2FE28DEADBEEF".asHexIterator()
        val actual = hex.readHexPacket()
        val expected = Packet.Literal(6, "011111100101".toLong(2))
        assertEquals(expected, actual)
        assertEquals("Expected excess hex digits", "DEADBEEF", hex.asSequence().joinToString(""))
    }

    @Test
    fun `Parse example 1`() {
        val actual = "38006F45291200".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 1,
            operatorType = PacketType.LessThan,
            packets = listOf(
                Packet.Literal(6, "1010".toLong(2)),
                Packet.Literal(2, "00010100".toLong(2)),
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Parse example 2`() {
        val actual = "EE00D40C823060".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 7,
            operatorType = PacketType.Maximum,
            packets = listOf(
                Packet.Literal(2, "0001".toLong(2)),
                Packet.Literal(4, "0010".toLong(2)),
                Packet.Literal(1, "0011".toLong(2)),
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `Parse example 3`() {
        val actual = "8A004A801A8002F478".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 4,
            operatorType = PacketType.Minimum,
            packets = listOf(
                Packet.Operator(
                    version = 1,
                    operatorType = PacketType.Minimum,
                    packets = listOf(
                        Packet.Operator(
                            version = 5,
                            operatorType = PacketType.Minimum,
                            packets = listOf(
                                Packet.Literal(
                                    version = 6,
                                    value = "1111".toLong(2)
                                ),
                            ),
                        ),
                    ),
                ),
            )
        )
        assertEquals(expected, actual)
        assertEquals(16, actual.versionSum())
    }

    @Test
    fun `Parse example 4`() {
        val actual = "620080001611562C8802118E34".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 3,
            operatorType = PacketType.Sum,
            packets = listOf(
                Packet.Operator(
                    version = 0,
                    operatorType = PacketType.Sum,
                    packets = listOf(
                        Packet.Literal(
                            version = 0,
                            value = "1010".toLong(2)
                        ),
                        Packet.Literal(
                            version = 5,
                            value = "1011".toLong(2)
                        ),
                    )
                ),
                Packet.Operator(
                    version = 1,
                    operatorType = PacketType.Sum,
                    packets = listOf(
                        Packet.Literal(
                            version = 0,
                            value = "1100".toLong(2)
                        ),
                        Packet.Literal(
                            version = 3,
                            value = "1101".toLong(2)
                        ),
                    ),
                ),
            )
        )
        assertEquals(expected, actual)
        assertEquals(12, actual.versionSum())
    }

    @Test
    fun `Parse example 5`() {
        val actual = "C0015000016115A2E0802F182340".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 6,
            operatorType = PacketType.Sum,
            packets = listOf(
                Packet.Operator(
                    version = 0,
                    operatorType = PacketType.Sum,
                    packets = listOf(
                        Packet.Literal(
                            version = 0,
                            value = "1010".toLong(2)
                        ),
                        Packet.Literal(
                            version = 6,
                            value = "1011".toLong(2)
                        ),
                    )
                ),
                Packet.Operator(
                    version = 4,
                    operatorType = PacketType.Sum,
                    packets = listOf(
                        Packet.Literal(
                            version = 7,
                            value = "1100".toLong(2)
                        ),
                        Packet.Literal(
                            version = 0,
                            value = "1101".toLong(2)
                        ),
                    ),
                ),
            )
        )
        assertEquals(expected, actual)
        assertEquals(23, actual.versionSum())
    }

    @Test
    fun `Parse example 6`() {
        val actual = "A0016C880162017C3686B18A3D4780".asHexIterator().readHexPacket() as Packet.Operator
        val expected = Packet.Operator(
            version = 5,
            operatorType = PacketType.Sum,
            packets = listOf(
                Packet.Operator(
                    version = 1,
                    operatorType = PacketType.Sum,
                    packets = listOf(
                        Packet.Operator(
                            version = 3,
                            operatorType = PacketType.Sum,
                            packets = listOf(
                                Packet.Literal(
                                    version = 7,
                                    value = "0110".toLong(2)
                                ),
                                Packet.Literal(
                                    version = 6,
                                    value = "0110".toLong(2)
                                ),
                                Packet.Literal(
                                    version = 5,
                                    value = "1100".toLong(2)
                                ),
                                Packet.Literal(
                                    version = 2,
                                    value = "1111".toLong(2)
                                ),
                                Packet.Literal(
                                    version = 2,
                                    value = "1111".toLong(2)
                                ),
                            )
                        ),
                    )
                ),
            )
        )
        assertEquals(expected, actual)
        assertEquals(31, actual.versionSum())
    }
}

private fun Packet.versionSum(): Int =
    when (this) {
        is Packet.Literal -> version
        is Packet.Operator -> version + packets.sumOf { it.versionSum() }
    }

private sealed interface Packet {
    val version: Int
    val value: Long

    data class Literal(
        override val version: Int,
        override val value: Long,
    ) : Packet

    data class Operator(
        override val version: Int,
        val operatorType: PacketType,
        val packets: List<Packet>,
    ) : Packet {
        override val value: Long
            get() = when (operatorType) {
                PacketType.Sum -> packets.fold(0) { result, packet -> result + packet.value }
                PacketType.Product -> packets.fold(1) { result, packet -> result * packet.value }
                PacketType.Minimum -> packets.minOf { it.value }
                PacketType.Maximum -> packets.maxOf { it.value }
                PacketType.Literal -> error("Illegal operator type $operatorType")
                PacketType.GreaterThan -> if (packets[0].value > packets[1].value) 1 else 0
                PacketType.LessThan -> if (packets[0].value < packets[1].value) 1 else 0
                PacketType.EqualTo -> if (packets[0].value == packets[1].value) 1 else 0
            }
    }
}

private enum class PacketType(val id: Int) {
    Sum(0),
    Product(1),
    Minimum(2),
    Maximum(3),
    Literal(4),
    GreaterThan(5),
    LessThan(6),
    EqualTo(7);

    override fun toString(): String = "$name($id)"
}

private sealed interface LengthType {
    data class Bits(val length: Int) : LengthType
    data class Packets(val length: Int) : LengthType
}

private fun Iterator<Hex>.readHexPacket(): Packet {
    return this.asBits().readBitPacket()
}

private fun Iterator<Bit>.readBitPacket(): Packet {
    val version = readInt(3)

    fun readLiteral() = Packet.Literal(
        version = version,
        value = buildString {
            do {
                val more = next()
                repeat(4) { append(next()) }
            } while (more.value == '1')
        }.toLong(2)
    )

    fun readOperator(packetType: PacketType): Packet.Operator {
        val packets = when (val lengthType = readLengthType()) {
            is LengthType.Bits ->
                buildList {
                    val sub = this@readBitPacket.asSequence().take(lengthType.length).iterator()
                    while (sub.hasNext()) {
                        add(sub.readBitPacket())
                    }
                }
            is LengthType.Packets ->
                (0 until lengthType.length).map {
                    readBitPacket()
                }
        }
        return Packet.Operator(
            version = version,
            operatorType = packetType,
            packets = packets,
        )
    }

    return when (val packetType = readPacketType()) {
        PacketType.Sum -> readOperator(packetType)
        PacketType.Product -> readOperator(packetType)
        PacketType.Minimum -> readOperator(packetType)
        PacketType.Maximum -> readOperator(packetType)
        PacketType.Literal -> readLiteral()
        PacketType.GreaterThan -> readOperator(packetType)
        PacketType.LessThan -> readOperator(packetType)
        PacketType.EqualTo -> readOperator(packetType)
    }
}

private fun Iterator<Bit>.readPacketType(): PacketType =
    when (val packetType = readInt(3)) {
        0 -> PacketType.Sum
        1 -> PacketType.Product
        2 -> PacketType.Minimum
        3 -> PacketType.Maximum
        4 -> PacketType.Literal
        5 -> PacketType.GreaterThan
        6 -> PacketType.LessThan
        7 -> PacketType.EqualTo
        else -> error("Illegal packet type $packetType")
    }

private fun Iterator<Bit>.readLengthType(): LengthType =
    when (readInt(1)) {
        0 -> LengthType.Bits(readInt(15))
        1 -> LengthType.Packets(readInt(11))
        else -> error("illegal Length type ID")
    }

private fun Iterator<Bit>.readInt(size: Int): Int =
    (0 until size).map { next() }.joinToString("").toInt(2)


@JvmInline
private value class Hex(val value: Char) {
    init {
        check(value in "0123456789ABCDEF") { "Not a hex digit: $value" }
    }

    override fun toString(): String = value.toString()
}

@JvmInline
private value class Bit(val value: Char) {
    init {
        check(value in "01") { "Not a bit char: $value" }
    }

    override fun toString(): String = value.toString()
}

private fun String.asHexIterator() = iterator { forEach { yield(Hex(it)) } }
private fun Iterator<Hex>.asBits() = iterator { forEach { yieldAll(it.toBits()) } }

private fun Hex.toBits(): List<Bit> {
    return when (value) {
        '0' -> "0000"
        '1' -> "0001"
        '2' -> "0010"
        '3' -> "0011"
        '4' -> "0100"
        '5' -> "0101"
        '6' -> "0110"
        '7' -> "0111"
        '8' -> "1000"
        '9' -> "1001"
        'A' -> "1010"
        'B' -> "1011"
        'C' -> "1100"
        'D' -> "1101"
        'E' -> "1110"
        'F' -> "1111"
        else -> error("Not a hexadecimal digit $value")
    }.map(::Bit)
}
