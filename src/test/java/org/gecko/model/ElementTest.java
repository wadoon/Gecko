package org.gecko.model;

import org.gecko.exceptions.MissingViewModelElementException;
import org.gecko.exceptions.ModelException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ElementTest {
    static Element element;

    @BeforeAll
    static void setUp() {
        assertThrows(ModelException.class, () -> element = new Element(-1) {
            @Override
            public void accept(ElementVisitor visitor) throws ModelException, MissingViewModelElementException {

            }
        });
        assertDoesNotThrow(() -> element = new Element(0) {
            @Override
            public void accept(ElementVisitor visitor) throws ModelException, MissingViewModelElementException {

            }
        });
    }

    @Test
    void testHashCode() {
        assertNotEquals(0, element.hashCode());
    }

    @Test
    void testEquals() {
        assertEquals(element, element);

        final Element[] other = new Element[4];
        assertDoesNotThrow(() -> other[0] = new Element(1) {
            @Override
            public void accept(ElementVisitor visitor) throws ModelException, MissingViewModelElementException {

            }
        });
        assertDoesNotThrow(() -> other[1] = new Element(0) {
            @Override
            public void accept(ElementVisitor visitor) throws ModelException, MissingViewModelElementException {

            }
        });

        assertDoesNotThrow(() -> other[2] = new State(2, "state"));
        assertDoesNotThrow(() -> other[3] = new State(0, "state"));

        assertNotEquals(element, other[0]);
        assertEquals(element, other[1]);
        assertNotEquals(element, other[2]);
        assertEquals(element, other[3]);
        assertNotEquals(null, element);
    }
}
