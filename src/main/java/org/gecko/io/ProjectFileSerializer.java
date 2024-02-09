package org.gecko.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.gecko.model.System;
import org.gecko.viewmodel.GeckoViewModel;

/**
 * Provides methods for the conversion of Gecko-specific data to the JSON format and writing the converted data in a
 * JSON file.
 */
public class ProjectFileSerializer implements FileSerializer {
    private final ObjectMapper objectMapper;
    private final GeckoViewModel viewModel;

    public ProjectFileSerializer(GeckoViewModel viewModel) {
        this.objectMapper = new ObjectMapper();
        this.viewModel = viewModel;
    }

    @Override
    public void writeToFile(File file) throws IOException {
        GeckoJsonWrapper geckoJsonWrapper = new GeckoJsonWrapper();
        System root = viewModel.getGeckoModel().getRoot();

        String rootInJson = this.getRootInJson(root);
        geckoJsonWrapper.setModel(rootInJson);

        ViewModelElementSaveVisitor visitor = new ViewModelElementSaveVisitor(viewModel);
        List<ViewModelPropertiesContainer> viewModelProperties = visitor.getViewModelProperties(root);

        String viewModelPropertiesInJson = this.getViewModelPropertiesInJson(viewModelProperties);
        geckoJsonWrapper.setViewModelProperties(viewModelPropertiesInJson);

        String finalJson = objectMapper.writeValueAsString(geckoJsonWrapper);

        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(finalJson);
        fileWriter.close();
    }

    private String getRootInJson(System root) throws JsonProcessingException {
        return objectMapper.writeValueAsString(root);
    }

    private String getViewModelPropertiesInJson(List<ViewModelPropertiesContainer> viewModelProperties)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(viewModelProperties);
    }
}
