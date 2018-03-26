package models;

import java.util.ArrayList;

public class IncrementType extends BaseEntity {

    private final String description;
    private final int incrementTypeId;

    public IncrementType(int id, String description) {
        super(id, null);
        this.incrementTypeId = id;
        this.description = description;
    }

    public int getIncrementTypeId() {
        return incrementTypeId;
    }

    public String getDescription() {
        return description;
    }

    public static ArrayList<IncrementType> getIncrements() {
        ArrayList<IncrementType> list = new ArrayList<>();
        list.add(new IncrementType(1, Seconds));
        list.add(new IncrementType(2, Minutes));
        list.add(new IncrementType(3, Hours));
        return list;
    }

    public static IncrementType getById(int incrementTypeId) {
        return getIncrements().stream().filter(x -> x.getId() == incrementTypeId).findFirst().get();
    }

    public static IncrementType getByDesription(String description) {
        return getIncrements().stream().filter(x -> x.getDescription().equals(description)).findFirst().get();
    }

    public static final String Seconds = "Seconds";
    public static final String Minutes = "Minutes";
    public static final String Hours = "Hours";

}
