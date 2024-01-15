package org.gecko.actions;

import org.gecko.viewmodel.ContractViewModel;

public class ChangePostconditionViewModelElementAction extends Action {

    private final ContractViewModel contractViewModel;
    private final String newPostcondition;

    private final String oldPostcondition;

    public ChangePostconditionViewModelElementAction(ContractViewModel contractViewModel, String newPostcondition) {
        this.contractViewModel = null;
        this.newPostcondition = newPostcondition;
        this.oldPostcondition = contractViewModel.getPostcondition();
    }

    @Override
    void run() {
        contractViewModel.setPostcondition(newPostcondition);
    }

    @Override
    Action getUndoAction(ActionFactory actionFactory) {
        return actionFactory.createChangePostconditionViewModelElementAction(contractViewModel, oldPostcondition);
    }
}