package org.gecko.tools

import javafx.stage.Stage
import org.gecko.application.GeckoIOManager
import org.gecko.application.GeckoManager
import org.gecko.exceptions.ModelException

import org.gecko.view.GeckoView
import org.gecko.viewmodel.GeckoViewModel
import org.gecko.viewmodel.StateViewModel
import org.gecko.viewmodel.SystemViewModel
import org.gecko.viewmodel.ViewModelFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class CursorToolTest {
    private var geckoViewModel: GeckoViewModel? = null
    private var geckoView: GeckoView? = null
    private var rootSystemViewModel: SystemViewModel? = null
    private var viewModelFactory: ViewModelFactory? = null
    private var source: StateViewModel? = null
    private var destination: StateViewModel? = null

    @Start
    @Throws(ModelException::class)
    private fun start(stage: Stage) {
        stage.show()

        val geckoManager = GeckoManager(stage, null)
        GeckoIOManager.geckoManager = geckoManager
        GeckoIOManager.stage = stage

        geckoViewModel = geckoManager.gecko.viewModel
        geckoView = geckoManager.gecko.view

        viewModelFactory = geckoViewModel!!.viewModelFactory
        rootSystemViewModel = geckoViewModel!!.currentEditor.currentSystem
        try {
            source = viewModelFactory!!.createStateViewModelIn(rootSystemViewModel!!)
            destination = viewModelFactory!!.createStateViewModelIn(rootSystemViewModel!!)
            viewModelFactory!!.createRegionViewModelIn(rootSystemViewModel!!)
            viewModelFactory!!.createPortViewModelIn(rootSystemViewModel!!)
            viewModelFactory!!.createEdgeViewModelIn(rootSystemViewModel!!, source!!, destination!!)
            viewModelFactory!!.createSystemViewModelIn(rootSystemViewModel!!)
        } catch (e: ModelException) {
            throw RuntimeException(e)
        } catch (e: Exception) {
            Assertions.fail<Any>()
        }

        geckoViewModel!!.switchEditor(rootSystemViewModel!!, true)
    } //    @Test
    //    void select(FxRobot robot) {
    //        assertEquals(geckoView.getCurrentView().getCurrentViewElements().size(), 4);
    //
    //        for (ViewElement<?> viewElement : geckoView.getCurrentView().getCurrentViewElements()) {
    //            robot.clickOn(viewElement.drawElement(), MouseButton.PRIMARY);
    //        }
    //
    //        geckoViewModel.switchEditor(rootSystemViewModel, false);
    //        for (ViewElement<?> viewElement : geckoView.getCurrentView().getCurrentViewElements()) {
    //            robot.clickOn(viewElement.drawElement(), MouseButton.PRIMARY);
    //        }
    //    }
    /*
    @Test
    void drag(FxRobot robot) {
        ViewModelFactory viewModelFactory = geckoViewModel.getViewModelFactory();
        rootSystemViewModel = geckoViewModel.getCurrentEditor().getCurrentSystem();
        Point2D newPosition = destination.getPosition().add(new Point2D(100, 100));
        destination.position = (newPosition);
        ViewElement<?> sourceViewElement = geckoView.getCurrentView()
            .getCurrentViewElements()
            .stream()
            .filter(viewElement -> viewElement.getTarget().equals(source))
            .findFirst()
            .orElse(null);
        ViewElement<?> destinationViewElement = geckoView.getCurrentView()
            .getCurrentViewElements()
            .stream()
            .filter(viewElement -> viewElement.getTarget().equals(destination))
            .findFirst()
            .orElse(null);


        robot.clickOn(sourceViewElement.drawElement(), MouseButton.PRIMARY);
        robot.drag(sourceViewElement.drawElement(), MouseButton.PRIMARY).dropTo(destinationViewElement.drawElement());
    }
     */
}
