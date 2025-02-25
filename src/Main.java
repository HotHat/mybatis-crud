public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");

        Builder b = new Builder();
        b.select("name", "age")
         .from("User")
         .from("Admin", "adm")
         .where("username", "admin")
                .where("name1", "1")
                .where("name2", "2")
                .orWhere("phone1", "1")
                .orWhere("phone2", "2")
                .where((query) -> {
                    query.where("qa1", "1")
                            .where("qa2", "2")
                            .orWhere("qo2", "2");
                })

        ;

        System.out.printf(b.toString());
    }
}