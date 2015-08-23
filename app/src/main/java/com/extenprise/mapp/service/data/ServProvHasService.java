package com.extenprise.mapp.service.data;

import java.util.ArrayList;

public class ServProvHasService {

    private String idServProvHasServ;
    private float experience;
    private Service service;
    private ArrayList<ServProvHasServHasServPt> servProvHasServHasServPts;
    private ServiceProvider servProv;

    public String getIdServProvHasServ() {
        return idServProvHasServ;
    }

    public void setIdServProvHasServ(String idServProvHasServ) {
        this.idServProvHasServ = idServProvHasServ;
    }

    public boolean addServProvHasServHasSaervPt(ServProvHasServHasServPt spsspt) {
        if (servProvHasServHasServPts == null) {
            servProvHasServHasServPts = new ArrayList<>();
        }
        return servProvHasServHasServPts.add(spsspt);
    }

    public boolean isWorkPlaceAdded() {
        return (servProvHasServHasServPts != null &&
                servProvHasServHasServPts.size() > 0);
    }

    public int getWorkPlaceCount() {
        if (servProvHasServHasServPts == null) {
            return 0;
        }
        return servProvHasServHasServPts.size();
    }

    public ServiceProvider getServProv() {
        return servProv;
    }

    public void setServProv(ServiceProvider servProv) {
        this.servProv = servProv;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ArrayList<ServProvHasServHasServPt> getServProvHasServHasServPts() {
        return servProvHasServHasServPts;
    }

    public void setServProvHasServHasServPts(ArrayList<ServProvHasServHasServPt> servProvHasServHasServPts) {
        this.servProvHasServHasServPts = servProvHasServHasServPts;
    }
}
