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
                    query.where("l1w1", "1")
                            .where("l1w22", "2")
                            .orWhere("l1ow1", "2")
                            .where((query1)-> {
                                query1.where("l2w1", "3");
                            })
                            .where("l1w3", "4")
                    ;
                })
                .where("l0w2", "5")

        ;

        System.out.printf(b.toString());
    }
}