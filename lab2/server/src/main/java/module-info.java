module example
{
    requires java.sql;
    requires java.naming;
    requires org.postgresql.jdbc;
    exports org.example;
    exports org.example.model;
}