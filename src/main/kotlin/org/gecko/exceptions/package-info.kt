/**
 * Contains custom [Exceptions][java.lang.Exception] used in Gecko to signalize errors or faulty
 * interactions. A [org.gecko.exceptions.MissingViewModelElementException] is thrown during the
 * creation of view model elements that have missing dependencies and cannot be created. A
 * [org.gecko.exceptions.ModelException] is thrown during the creation of model elements or while
 * changing their properties, if the provided values are invalid.
 */
package org.gecko.exceptions
