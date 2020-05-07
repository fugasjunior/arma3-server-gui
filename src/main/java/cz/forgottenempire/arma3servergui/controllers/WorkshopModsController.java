package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.SteamCredentialsService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import cz.forgottenempire.arma3servergui.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.services.SteamWorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController()
public class WorkshopModsController {
    private SteamWorkshopService steamWorkshopService;
    private SteamCmdService steamCmdService;
    private SteamCredentialsService steamCredentialsService;

    private JsonDbService<WorkshopMod> modsDb;

    @GetMapping("/mods")
    public ResponseEntity<Collection<WorkshopMod>> getAllMods() {
        return new ResponseEntity<>(modsDb.findAll(WorkshopMod.class), HttpStatus.OK);
    }

    @PostMapping("/mods/install/{id}")
    public ResponseEntity<WorkshopMod> installOrUpdateMod(@PathVariable Long id) {
        WorkshopMod mod = modsDb.find(id, WorkshopMod.class);

        if(mod == null) {
            mod = new WorkshopMod(id);
            mod.setName(steamWorkshopService.getModName(id));
            modsDb.save(mod, WorkshopMod.class);
        }

        steamCmdService.installOrUpdateMod(steamCredentialsService.getAuthAccount(), mod);

        return new ResponseEntity<>(mod, HttpStatus.OK);
    }

    @DeleteMapping("/mods/uninstall/{id}")
    public ResponseEntity<WorkshopMod> uninstallMod(@PathVariable Long id) {
        WorkshopMod mod = modsDb.find(id, WorkshopMod.class);
        if(mod != null) {
            steamCmdService.deleteMod(mod);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/mods/refresh")
    public ResponseEntity<WorkshopMod> refreshMods() {
        if(!steamCmdService.refreshMods(steamCredentialsService.getAuthAccount()))
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setSteamWorkshopService(SteamWorkshopService steamWorkshopService) {
        this.steamWorkshopService = steamWorkshopService;
    }

    @Autowired
    public void setSteamCmdService(SteamCmdService steamCmdService) {
        this.steamCmdService = steamCmdService;
    }

    @Autowired
    public void setModsDb(JsonDbService<WorkshopMod> modsDb) {
        this.modsDb = modsDb;
    }

    @Autowired
    public void setSteamCredentialsService(SteamCredentialsService steamCredentialsService) {
        this.steamCredentialsService = steamCredentialsService;
    }
}