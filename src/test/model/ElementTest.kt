package org.gecko.model

import org.gecko.exceptions.MissingViewModelElementException
import org.gecko.exceptions.ModelException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.ThrowingSupplier

class ElementTest {
    @Test
    fun testHashCode() {
        Assertions.assertNotEquals(0, element.hashCode())
    }

    @Test
    fun testEquals() {
        Assertions.assertEquals(element, element)

        val other = arrayOfNulls<Element>(4)
        Assertions.assertDoesNotThrow(ThrowingSupplier<Element> {
            other[0] = object : Element(1) {
                @Throws(ModelException::class, MissingViewModelElementException::class)
                override fun accept(visitor: ElementVisitor) {
                }
            }
        })
        Assertions.assertDoesNotThrow(ThrowingSupplier<Element> {
            other[1] = object : Element(0) {
                @Throws(ModelException::class, MissingViewModelElementException::class)
                override fun accept(visitor: ElementVisitor) {
                }
            }
        })

        Assertions.assertDoesNotThrow(ThrowingSupplier<Element> { other[2] = State(2, "state") })
        Assertions.assertDoesNotThrow(ThrowingSupplier<Element> { other[3] = State(0, "state") })

        Assertions.assertNotEquals(element, other[0])
        Assertions.assertEquals(element, other[1])
        Assertions.assertNotEquals(element, other[2])
        Assertions.assertEquals(element, other[3])
        Assertions.assertNotEquals(null, element)
    }

    companion object {
        var element: Element? = null

        @BeforeAll
        fun setUp() {
            Assertions.assertThrows(ModelException::class.java) {
                element = object : Element(-1) {
                    @Throws(ModelException::class, MissingViewModelElementException::class)
                    override fun accept(visitor: ElementVisitor) {
                    }
                }
            }
            Assertions.assertDoesNotThrow(ThrowingSupplier<Element> {
                element = object : Element(0) {
                    @Throws(ModelException::class, MissingViewModelElementException::class)
                    override fun accept(visitor: ElementVisitor) {
                    }
                }
            })
        }
    }
}
