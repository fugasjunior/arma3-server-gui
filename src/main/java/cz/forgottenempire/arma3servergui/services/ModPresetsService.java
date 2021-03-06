package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.model.ModListPreset;
import java.util.Collection;

public interface ModPresetsService {

    Collection<ModListPreset> getAllPresets();

    void createOrUpdatePreset(String name, Collection<Long> modIds);

    void deletePreset(String name);
}
