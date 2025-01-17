public class Staff extends Person {
    private String jobTitle;
    private double salary;

    // سازنده
    public Staff(String firstName, String lastName, String nationalCode, String jobTitle, double salary) {
        super(firstName, lastName, nationalCode); // فراخوانی سازنده کلاس پایه
        this.jobTitle = jobTitle;
        this.salary = salary;
    }

    // متدهای getter و setter
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
