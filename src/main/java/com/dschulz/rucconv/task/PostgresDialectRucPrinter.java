package com.dschulz.rucconv.task;

import com.dschulz.rucconv.model.Contribuyente;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;

public class PostgresDialectRucPrinter extends PrintWriter implements RecordListExporter<Contribuyente> {
    public PostgresDialectRucPrinter(String fileName, Charset charset) throws IOException {
        super(fileName, charset);
    }

    public void export(List<Contribuyente> lista) {


        String ddl = """
            DROP TABLE IF EXISTS ruc;
            CREATE TABLE IF NOT EXISTS ruc (
               id INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
               doc TEXT UNIQUE NOT NULL,
               dv SMALLINT NOT NULL,
               denominacionOriginal TEXT NOT NULL,
               denominacionCorregida TEXT
            );
            """;


        this.println("-- Volcado RUCs para PostgreSQL");
        this.println("-- Recuperado de la basura que publica la SET");
        this.println(String.format("-- %s ", LocalDateTime.now()));
        this.println();


        surroundWithTransaction(() -> {
            this.println(ddl);

            String plantilla = "INSERT INTO ruc ( doc, dv, denominacionOriginal, denominacionCorregida) VALUES(%s, %d, %s, %s);";

            for (Contribuyente c : lista) {
                String sql = String.format(plantilla,
                    singleQuote(c.getRuc()),
                    c.getVerificador(),
                    singleQuote(c.getDenominacion().replaceAll("\"", "")),
                    (c.getDenominacionCorregida() != null) ? singleQuote( c.getDenominacionCorregida() ) : "null"
                );
                this.println(sql);
            }

            this.println("UPDATE ruc SET denominacionCorregida = denominacionOriginal WHERE denominacionCorregida IS NULL;");
            this.println("-- Opcional: ");
            this.println("-- ALTER TABLE ruc DROP COLUMN denominacionOriginal ;");
            this.println("-- ALTER TABLE ruc RENAME COLUMN denominacionCorregida TO denominacion;");
            this.println();

        });



    }

    private String singleQuote(String orig){
        return "E'" +
            orig.replaceAll("'", "\\\\'") +
            "'";
    }

    private String doubleQuote(String orig){
        return "\"" +
            orig.replaceAll("\"", "\\\\\"" ) +
            "\"";
    }


    private void surroundWithTransaction(SQLWriter r) {
        this.println("\nBEGIN;");
        r.writeSQL();
        this.println("COMMIT;\n");
    }

}