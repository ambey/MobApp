package com.extenprise.mapp.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RxItem implements Parcelable {
    private int srNo;
    private boolean saved;
    private String drugName;
    private String drugStrength;
    private String drugForm;
    private String doseQty;
    private int courseDur;
    private boolean beforeMeal;
    private boolean morning;
    private boolean afternoon;
    private boolean evening;
    private String mTime;
    private String aTime;
    private String eTime;
    private String inTakeSteps;
    private String altDrugName;
    private String altDrugStrength;
    private String altDrugForm;
    private int available;

    public RxItem() {
    }

    protected RxItem(Parcel in) {
        srNo = in.readInt();
        saved = (in.readInt() > 0);
        drugName = in.readString();
        drugStrength = in.readString();
        drugForm = in.readString();
        doseQty = in.readString();
        courseDur = in.readInt();
        beforeMeal = in.readByte() != 0;
        morning = in.readByte() != 0;
        afternoon = in.readByte() != 0;
        evening = in.readByte() != 0;
        mTime = in.readString();
        aTime = in.readString();
        eTime = in.readString();
        inTakeSteps = in.readString();
        altDrugName = in.readString();
        altDrugStrength = in.readString();
        altDrugForm = in.readString();
        available = in.readInt();
    }

    public static final Creator<RxItem> CREATOR = new Creator<RxItem>() {
        @Override
        public RxItem createFromParcel(Parcel in) {
            return new RxItem(in);
        }

        @Override
        public RxItem[] newArray(int size) {
            return new RxItem[size];
        }
    };

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugStrength() {
        return drugStrength;
    }

    public void setDrugStrength(String drugStrength) {
        this.drugStrength = drugStrength;
    }

    public String getDrugForm() {
        return drugForm;
    }

    public void setDrugForm(String drugForm) {
        this.drugForm = drugForm;
    }

    public String getDoseQty() {
        return doseQty;
    }

    public void setDoseQty(String doseQty) {
        this.doseQty = doseQty;
    }

    public int getCourseDur() {
        return courseDur;
    }

    public void setCourseDur(int courseDur) {
        this.courseDur = courseDur;
    }

    public boolean isBeforeMeal() {
        return beforeMeal;
    }

    public void setBeforeMeal(boolean beforeMeal) {
        this.beforeMeal = beforeMeal;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(boolean afternoon) {
        this.afternoon = afternoon;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getaTime() {
        return aTime;
    }

    public void setaTime(String aTime) {
        this.aTime = aTime;
    }

    public String geteTime() {
        return eTime;
    }

    public void seteTime(String eTime) {
        this.eTime = eTime;
    }

    public String getInTakeSteps() {
        return inTakeSteps;
    }

    public void setInTakeSteps(String inTakeSteps) {
        this.inTakeSteps = inTakeSteps;
    }

    public String getAltDrugName() {
        return altDrugName;
    }

    public void setAltDrugName(String altDrugName) {
        this.altDrugName = altDrugName;
    }

    public String getAltDrugStrength() {
        return altDrugStrength;
    }

    public void setAltDrugStrength(String altDrugStrength) {
        this.altDrugStrength = altDrugStrength;
    }

    public String getAltDrugForm() {
        return altDrugForm;
    }

    public void setAltDrugForm(String altDrugForm) {
        this.altDrugForm = altDrugForm;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getDailyDose() {
        int count = 0;
        if (morning) count++;
        if (afternoon) count++;
        if (evening) count++;
        return count;
    }

    public String getDailyDoseString() {
        return String.format("%s---%s---%s",
                morning ? doseQty : "X", afternoon ? doseQty : "X", evening ? doseQty : "X");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(srNo);
        dest.writeInt(saved ? 1 : 0);
        dest.writeString(drugName);
        dest.writeString(drugStrength);
        dest.writeString(drugForm);
        dest.writeString(doseQty);
        dest.writeInt(courseDur);
        dest.writeByte((byte) (beforeMeal ? 1 : 0));
        dest.writeByte((byte) (morning ? 1 : 0));
        dest.writeByte((byte) (afternoon ? 1 : 0));
        dest.writeByte((byte) (evening ? 1 : 0));
        dest.writeString(mTime);
        dest.writeString(aTime);
        dest.writeString(eTime);
        dest.writeString(inTakeSteps);
        dest.writeString(altDrugName);
        dest.writeString(altDrugStrength);
        dest.writeString(altDrugForm);
        dest.writeInt(available);
    }
}
