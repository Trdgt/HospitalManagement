public class Patient extends Person {
    private String disease; // بیماری

    // سازنده
    public Patient(String firstName, String lastName, String nationalCode, String disease) {
        super(firstName, lastName, nationalCode); // فراخوانی سازنده کلاس پایه (Person)
        this.disease = disease;
    }

    // متدهای getter و setter
    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
