/**
 * The contained classes hold together the data and functionalities provided in the other packages
 * of Gecko. [org.gecko.application.Main] provides the execution of the application and instantiates
 * an initial [org.gecko.application.Gecko], which holds a [GeckoModel][org.gecko.model.GeckoModel],
 * a [GeckoView][org.gecko.view.GeckoView] and a
 * [GeckoViewModel][org.gecko.viewmodel.GeckoViewModel] and is managed by the
 * [org.gecko.application.GeckoManager]. The creation and modification of files afferent to Gecko
 * projects is also managed at this level by the [org.gecko.application.GeckoIOManager].
 */
package org.gecko.application
