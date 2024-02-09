package org.gecko.actions;

import org.gecko.exceptions.GeckoException;
import org.gecko.model.Automaton;
import org.gecko.viewmodel.GeckoViewModel;
import org.gecko.viewmodel.PositionableViewModelElement;
import org.gecko.viewmodel.RegionViewModel;

public class DeleteRegionViewModelElementAction extends AbstractPositionableViewModelElementAction {
    private final GeckoViewModel geckoViewModel;
    private final RegionViewModel regionViewModel;
    private final Automaton automaton;

    DeleteRegionViewModelElementAction(
        GeckoViewModel geckoViewModel, RegionViewModel regionViewModel, Automaton automaton) {
        this.geckoViewModel = geckoViewModel;
        this.regionViewModel = regionViewModel;
        this.automaton = automaton;
    }

    @Override
    boolean run() throws GeckoException {
        automaton.removeRegion(regionViewModel.getTarget());
        geckoViewModel.deleteViewModelElement(regionViewModel);
        return true;
    }

    @Override
    Action getUndoAction(ActionFactory actionFactory) {
        return new RestoreRegionViewModelElementAction(geckoViewModel, regionViewModel, automaton);
    }

    @Override
    PositionableViewModelElement<?> getTarget() {
        return regionViewModel;
    }
}