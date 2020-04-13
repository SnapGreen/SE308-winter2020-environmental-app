package com.acme.snapgreen
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Destin Estrela
 *
 * Unit testing framework: Junit is also compatible with kotlin and runs on dev machine.
 */
class TestDrivenDevelopmentAssignment {

    @Test
    fun test_greet_1() {
        assertEquals("Hello, Bob.", greet("Bob"))
    }

    @Test
    fun test_greet_2() {
        assertEquals("Hello, my friend.", greet(null))
    }

    @Test
    fun test_greet_3() {
        assertEquals("HELLO JERRY!", greet("JERRY"))
    }

    @Test
    fun test_greet_4() {
        assertEquals("Hello, Jill and Jane.", greet("Jill", "Jane"))
    }

    @Test
    fun test_greet_5() {
        assertEquals(
            "Hello, Amy, Brian, and Charlotte.",
            greet("Amy", "Brian", "Charlotte")
        )
    }

    @Test
    fun test_greet_6() {
        assertEquals(
            "Hello, Amy and Charlotte. AND HELLO BRIAN!",
            greet("Amy", "BRIAN", "Charlotte")
        )
    }

    @Test
    fun test_greet_7() {
        assertEquals(
            "Hello, Bob, Charlie, and Dianne.",
            greet("Bob", "Charlie, Dianne")
        )
    }

    @Test
    fun test_greet_8() {
        assertEquals(
            "Hello, Bob and Charlie, Dianne.",
            greet("Bob", "\"Charlie, Dianne\"")
        )
    }

    private fun isAllUpper(string: String?): Boolean {
        var isUpper = true
        string?.toCharArray()?.forEach { c: Char ->
            if (!c.isUpperCase()) isUpper = false
        }
        return isUpper
    }

    private fun greet(vararg strings: String?): String {
        var ret = ""
        var ret2 = ""
        val lowerList = ArrayList<String?>()
        var upperString = ""
        for (string in strings) {
            if (string != null) {
                when {
                    string.contains("\"") -> {
                        lowerList.add(string.substring(1 until string.length - 1))
                    }
                    string.contains(',') -> {
                        var split = string.split(", ")
                        for (splitString in split) {
                            lowerList.add(splitString)
                        }
                    }
                    isAllUpper(string) -> {
                        upperString = string
                    }
                    else -> {
                        lowerList.add(string)
                    }
                }
            }
        }

        when {
            strings[0].isNullOrEmpty() -> {
                ret = "Hello, my friend."
            }
            strings.size == 1 -> {

                ret = if (isAllUpper(strings[0])) {
                    "HELLO " + strings[0] + "!"
                } else {
                    "Hello, " + strings[0] + "."
                }
            }
            else -> {
                if (lowerList.size == 2) {
                    ret = "Hello, " + lowerList[0] + " and " + lowerList[1] + "."
                } else {
                    ret = "Hello, "
                    for (i in lowerList.indices) {
                        ret += if (i != lowerList.size - 1) {
                            lowerList[i] + ", "
                        } else {
                            "and " + lowerList[i] + "."
                        }
                    }
                }
                if (upperString.isNotEmpty()) {
                    ret2 = " AND HELLO $upperString!"
                }
            }
        }

        return "$ret$ret2"
    }

}
